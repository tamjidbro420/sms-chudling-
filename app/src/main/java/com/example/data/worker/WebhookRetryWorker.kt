package com.example.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.data.repository.SmsLogRepository

class WebhookRetryWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val repository = SmsLogRepository(applicationContext)
            repository.retryAllPendingLogs()
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
