package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

data class ParsedSms(
    val serviceName: String,
    val amount: Double,
    val trxId: String,
    val senderNumber: String,
    val fee: Double? = 0.0,
    val balance: Double? = null,
    val date: String? = null,
    val time: String? = null,
    val rawSms: String,
    val formatSource: String = "Auto/Built-in"
)

@JsonClass(generateAdapter = true)
data class SmsWebhookPayload(
    @param:Json(name = "secret_token") val secretToken: String,
    @param:Json(name = "service") val service: String,
    @param:Json(name = "sender_number") val senderNumber: String,
    @param:Json(name = "amount") val amount: Double,
    @param:Json(name = "trx_id") val trxId: String,
    @param:Json(name = "fee") val fee: Double? = null,
    @param:Json(name = "balance") val balance: Double? = null,
    @param:Json(name = "date") val date: String? = null,
    @param:Json(name = "time") val time: String? = null,
    @param:Json(name = "raw_sms") val rawSms: String
)
