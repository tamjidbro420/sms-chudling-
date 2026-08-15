package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SmsLogDao {

    @Query("SELECT * FROM sms_logs ORDER BY timestamp DESC")
    fun getAllLogs(): Flow<List<SmsLogEntity>>

    @Query("SELECT * FROM sms_logs WHERE status = 'PENDING' OR status = 'FAILED' ORDER BY timestamp ASC")
    suspend fun getPendingOrFailedLogs(): List<SmsLogEntity>

    @Query("SELECT * FROM sms_logs WHERE id = :id LIMIT 1")
    suspend fun getLogById(id: Long): SmsLogEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: SmsLogEntity): Long

    @Update
    suspend fun updateLog(log: SmsLogEntity)

    /**
     * Requirement 4: Automatic 48-Hour Storage Optimization
     * DELETE FROM sms_logs WHERE timestamp < (currentTime - 172800000)
     */
    @Query("DELETE FROM sms_logs WHERE timestamp < :cutoffTimestamp")
    suspend fun deleteOldLogs(cutoffTimestamp: Long): Int

    @Query("SELECT COUNT(*) FROM sms_logs")
    suspend fun getLogCount(): Int

    @Query("DELETE FROM sms_logs")
    suspend fun clearAllLogs()

    @Query("SELECT COUNT(*) FROM sms_logs WHERE status = 'SUCCESS'")
    fun getSuccessCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM sms_logs WHERE status = 'SUCCESS' AND timestamp >= :startOfDay")
    fun getTodaySuccessCount(startOfDay: Long): Flow<Int>

    @Query("SELECT COUNT(*) FROM sms_logs WHERE status = 'PENDING' OR status = 'FAILED'")
    fun getPendingCount(): Flow<Int>

    @Query("SELECT SUM(amount) FROM sms_logs WHERE status = 'SUCCESS'")
    fun getTotalSuccessAmount(): Flow<Double?>

    @Query("SELECT SUM(amount) FROM sms_logs WHERE status = 'SUCCESS' AND timestamp >= :startOfDay")
    fun getTodaySuccessAmount(startOfDay: Long): Flow<Double?>
}
