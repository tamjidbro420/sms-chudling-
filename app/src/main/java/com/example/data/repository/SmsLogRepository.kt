package com.example.data.repository

import android.content.Context
import androidx.work.Constraints
import androidx.work.WorkManager
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import com.example.data.local.AppDatabase
import com.example.data.local.CustomFormatDao
import com.example.data.local.CustomFormatEntity
import com.example.data.local.SmsLogDao
import com.example.data.local.SmsLogEntity
import com.example.data.model.ParsedSms
import com.example.data.model.SmsWebhookPayload
import com.example.data.network.WebhookApiService
import com.example.data.preferences.StorageHelper
import com.example.data.worker.WebhookRetryWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class SmsLogRepository(
    private val context: Context,
    private val db: AppDatabase = AppDatabase.getInstance(context),
    private val smsLogDao: SmsLogDao = db.smsLogDao(),
    private val customFormatDao: CustomFormatDao = db.customFormatDao(),
    private val apiService: WebhookApiService = WebhookApiService.create(),
    private val storageHelper: StorageHelper = StorageHelper.getInstance(context)
) {

    val allLogs: Flow<List<SmsLogEntity>> = smsLogDao.getAllLogs()
    val successCount: Flow<Int> = smsLogDao.getSuccessCount()
    val pendingCount: Flow<Int> = smsLogDao.getPendingCount()
    val totalSuccessAmount: Flow<Double?> = smsLogDao.getTotalSuccessAmount()
    val allCustomFormats: Flow<List<CustomFormatEntity>> = customFormatDao.getAllFormats()

    fun getTodaySuccessCount(startOfDay: Long): Flow<Int> = smsLogDao.getTodaySuccessCount(startOfDay)
    fun getTodaySuccessAmount(startOfDay: Long): Flow<Double?> = smsLogDao.getTodaySuccessAmount(startOfDay)

    suspend fun getEnabledFormats(): List<CustomFormatEntity> = withContext(Dispatchers.IO) {
        customFormatDao.getEnabledFormats()
    }

    suspend fun addCustomFormat(format: CustomFormatEntity): Long = withContext(Dispatchers.IO) {
        customFormatDao.insertFormat(format)
    }

    suspend fun updateCustomFormat(format: CustomFormatEntity) = withContext(Dispatchers.IO) {
        customFormatDao.updateFormat(format)
    }

    suspend fun deleteCustomFormat(format: CustomFormatEntity) = withContext(Dispatchers.IO) {
        customFormatDao.deleteFormat(format)
    }

    suspend fun deleteCustomFormatById(id: Long) = withContext(Dispatchers.IO) {
        customFormatDao.deleteFormatById(id)
    }

    suspend fun processAndSendSms(parsedSms: ParsedSms): SmsLogEntity = withContext(Dispatchers.IO) {
        perform48HourCleanup()

        val initialEntity = SmsLogEntity(
            serviceName = parsedSms.serviceName,
            amount = parsedSms.amount,
            trxId = parsedSms.trxId,
            senderNumber = parsedSms.senderNumber,
            fee = parsedSms.fee,
            balance = parsedSms.balance,
            date = parsedSms.date,
            time = parsedSms.time,
            rawSms = parsedSms.rawSms,
            timestamp = System.currentTimeMillis(),
            status = "PENDING"
        )

        val id = smsLogDao.insertLog(initialEntity)
        val savedEntity = initialEntity.copy(id = id)

        if (!storageHelper.isSyncActive()) {
            val inactiveEntity = savedEntity.copy(
                status = "FAILED",
                errorMessage = "Sync is set to Inactive in App Settings"
            )
            smsLogDao.updateLog(inactiveEntity)
            return@withContext inactiveEntity
        }

        return@withContext trySendWebhook(savedEntity)
    }

    suspend fun trySendWebhook(log: SmsLogEntity): SmsLogEntity = withContext(Dispatchers.IO) {
        val targetUrl = storageHelper.getWebhookUrl()
        val secretToken = storageHelper.getSecretToken()

        val payload = SmsWebhookPayload(
            secretToken = secretToken,
            service = log.serviceName,
            senderNumber = log.senderNumber,
            amount = log.amount,
            trxId = log.trxId,
            fee = log.fee,
            balance = log.balance,
            date = log.date,
            time = log.time,
            rawSms = log.rawSms
        )

        val authHeader = if (secretToken.isNotBlank()) "Bearer $secretToken" else null

        try {
            val response = apiService.sendWebhook(
                url = targetUrl,
                token = authHeader,
                payload = payload
            )

            if (response.isSuccessful) {
                val updated = log.copy(
                    status = "SUCCESS",
                    responseCode = response.code(),
                    errorMessage = null
                )
                smsLogDao.updateLog(updated)
                updated
            } else {
                val errorMsg = "HTTP ${response.code()}: ${response.message().ifBlank { "Server returned error" }}"
                val updated = log.copy(
                    status = "FAILED",
                    responseCode = response.code(),
                    errorMessage = errorMsg
                )
                smsLogDao.updateLog(updated)
                enqueueRetryWorker()
                updated
            }
        } catch (e: Exception) {
            val updated = log.copy(
                status = "FAILED",
                errorMessage = "Network Exception: ${e.localizedMessage ?: "Connection failed"}"
            )
            smsLogDao.updateLog(updated)
            enqueueRetryWorker()
            updated
        }
    }

    suspend fun retryAllPendingLogs() = withContext(Dispatchers.IO) {
        perform48HourCleanup()
        val pendingLogs = smsLogDao.getPendingOrFailedLogs()
        for (log in pendingLogs) {
            trySendWebhook(log)
        }
    }

    fun enqueueRetryWorker() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val retryRequest = OneTimeWorkRequestBuilder<WebhookRetryWorker>()
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueue(retryRequest)
    }

    suspend fun clearAllLogs() = withContext(Dispatchers.IO) {
        smsLogDao.clearAllLogs()
    }

    suspend fun perform48HourCleanup(): Int = withContext(Dispatchers.IO) {
        val cutoff = System.currentTimeMillis() - 172800000L
        smsLogDao.deleteOldLogs(cutoff)
    }

    suspend fun seedInitialFormatsIfEmpty() = withContext(Dispatchers.IO) {
        val count = customFormatDao.getFormatCount()
        if (count == 0) {
            val sampleFormats = listOf(
                CustomFormatEntity(
                    gateway = "bKash",
                    formatName = "bKash Received Payment Format",
                    sampleSms = "You have received Tk 35.00 from 01609441417. Fee Tk 0.00. Balance Tk 649.00. TrxID DGP1PK3V5R at 25/07/2026 17:28",
                    regexPattern = "(?:received|cash in).*?Tk\\s*([\\d,]+\\.?\\d*).*?from\\s*(\\+?\\d+).*?Fee\\s*Tk\\s*([\\d,]+\\.?\\d*).*?Balance\\s*Tk\\s*([\\d,]+\\.?\\d*).*?TrxID\\s*([A-Za-z0-9]+)",
                    isEnabled = true
                ),
                CustomFormatEntity(
                    gateway = "Nagad",
                    formatName = "Nagad Cash In Format",
                    sampleSms = "Cash In Tk 1,000.00 from 01711223344 successful. Fee Tk 0.00. Balance Tk 2,500.00. TrxID N7G8H9J0K1",
                    regexPattern = "Cash\\s*In.*?Tk\\s*([\\d,]+\\.?\\d*).*?from\\s*(\\+?\\d+).*?TrxID\\s*([A-Za-z0-9]+)",
                    isEnabled = true
                ),
                CustomFormatEntity(
                    gateway = "Rocket",
                    formatName = "Rocket RCV Payment Format",
                    sampleSms = "RCV Tk 500.00 from 01812345678-1 Balance Tk 1,200.00 TxnId R6T7Y8U9I0",
                    regexPattern = "RCV.*?Tk\\s*([\\d,]+\\.?\\d*).*?from\\s*(\\+?\\d+).*?TxnId\\s*([A-Za-z0-9]+)",
                    isEnabled = true
                )
            )
            for (f in sampleFormats) {
                customFormatDao.insertFormat(f)
            }
        }
    }

    suspend fun seedSampleLogsIfEmpty() = withContext(Dispatchers.IO) {
        seedInitialFormatsIfEmpty()
        val count = smsLogDao.getLogCount()
        if (count == 0) {
            val now = System.currentTimeMillis()
            val sampleLogs = listOf(
                SmsLogEntity(
                    serviceName = "bKash",
                    amount = 35.00,
                    trxId = "DGP1PK3V5R",
                    senderNumber = "01609441417",
                    fee = 0.00,
                    balance = 649.00,
                    date = "25/07/2026",
                    time = "17:28",
                    rawSms = "You have received Tk 35.00 from 01609441417. Fee Tk 0.00. Balance Tk 649.00. TrxID DGP1PK3V5R at 25/07/2026 17:28",
                    timestamp = now - 5000,
                    status = "SUCCESS"
                ),
                SmsLogEntity(
                    serviceName = "Nagad",
                    amount = 1250.00,
                    trxId = "N7G8H9J0K1",
                    senderNumber = "01787654321",
                    fee = 0.00,
                    balance = 2500.00,
                    date = "25/07/2026",
                    time = "18:10",
                    rawSms = "Cash In Tk 1,250.00 from 01787654321 successful. Ref: X. Fee: Tk 0.00. Balance: Tk 2,500.00. TrxID N7G8H9J0K1",
                    timestamp = now - 10000,
                    status = "PENDING"
                ),
                SmsLogEntity(
                    serviceName = "Rocket",
                    amount = 300.00,
                    trxId = "R6T7Y8U9I0",
                    senderNumber = "01899887766",
                    fee = 0.00,
                    balance = 3500.00,
                    date = "25/07/2026",
                    time = "19:05",
                    rawSms = "RCV Tk 300.00 from 01899887766-1 Balance Tk 3,500.00 TxnId R6T7Y8U9I0 Date 25-07-26",
                    timestamp = now - 16000,
                    status = "SUCCESS"
                )
            )
            for (log in sampleLogs) {
                smsLogDao.insertLog(log)
            }
        }
    }
}
