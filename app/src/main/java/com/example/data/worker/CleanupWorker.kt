package com.example.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.data.repository.SmsLogRepository

class CleanupWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val repository = SmsLogRepository(applicationContext)
            val deletedCount = repository.perform48HourCleanup()
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}
