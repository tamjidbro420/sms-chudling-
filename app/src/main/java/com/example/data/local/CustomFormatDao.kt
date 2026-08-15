package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomFormatDao {

    @Query("SELECT * FROM custom_formats ORDER BY created_at DESC")
    fun getAllFormats(): Flow<List<CustomFormatEntity>>

    @Query("SELECT * FROM custom_formats WHERE is_enabled = 1 ORDER BY created_at DESC")
    suspend fun getEnabledFormats(): List<CustomFormatEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFormat(format: CustomFormatEntity): Long

    @Update
    suspend fun updateFormat(format: CustomFormatEntity)

    @Delete
    suspend fun deleteFormat(format: CustomFormatEntity)

    @Query("DELETE FROM custom_formats WHERE id = :id")
    suspend fun deleteFormatById(id: Long)

    @Query("SELECT COUNT(*) FROM custom_formats")
    suspend fun getFormatCount(): Int
}
