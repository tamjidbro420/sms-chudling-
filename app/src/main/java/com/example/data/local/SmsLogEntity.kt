package com.example.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sms_logs")
data class SmsLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "service_name")
    val serviceName: String,

    @ColumnInfo(name = "amount")
    val amount: Double,

    @ColumnInfo(name = "trx_id")
    val trxId: String,

    @ColumnInfo(name = "sender_number")
    val senderNumber: String,

    @ColumnInfo(name = "fee")
    val fee: Double? = 0.0,

    @ColumnInfo(name = "balance")
    val balance: Double? = null,

    @ColumnInfo(name = "date")
    val date: String? = null,

    @ColumnInfo(name = "time")
    val time: String? = null,

    @ColumnInfo(name = "raw_sms")
    val rawSms: String,

    @ColumnInfo(name = "timestamp")
    val timestamp: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "status")
    val status: String, // "SUCCESS", "PENDING", "FAILED"

    @ColumnInfo(name = "response_code")
    val responseCode: Int? = null,

    @ColumnInfo(name = "error_message")
    val errorMessage: String? = null
)
