package com.example.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "custom_formats")
data class CustomFormatEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "gateway")
    val gateway: String, // "bKash", "Nagad", "Rocket", "Upay", "CellFin", "Custom"

    @ColumnInfo(name = "format_name")
    val formatName: String,

    @ColumnInfo(name = "sample_sms")
    val sampleSms: String,

    @ColumnInfo(name = "regex_pattern")
    val regexPattern: String,

    @ColumnInfo(name = "is_enabled")
    val isEnabled: Boolean = true,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)
