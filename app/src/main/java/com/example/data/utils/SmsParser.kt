package com.example.data.utils

import com.example.data.local.CustomFormatEntity
import com.example.data.model.ParsedSms
import java.util.Locale

object SmsParser {

    /**
     * Checks whether an incoming SMS matches required financial payment criteria
     */
    fun isMatchingPaymentSms(
        senderHeader: String?,
        bodyText: String?,
        customFormats: List<CustomFormatEntity>? = null
    ): Boolean {
        if (bodyText.isNullOrBlank()) return false

        if (!customFormats.isNullOrEmpty()) {
            for (format in customFormats) {
                if (format.isEnabled && format.regexPattern.isNotBlank()) {
                    try {
                        if (Regex(format.regexPattern, RegexOption.IGNORE_CASE).containsMatchIn(bodyText)) {
                            return true
                        }
                    } catch (_: Exception) {}
                }
            }
        }

        val bodyLower = bodyText.lowercase(Locale.ROOT)

        val containsKeyword = bodyLower.contains("cash in") ||
                bodyLower.contains("cash-in") ||
                bodyLower.contains("cashin") ||
                bodyLower.contains("received") ||
                bodyLower.contains("receive") ||
                bodyLower.contains("receive money") ||
                bodyLower.contains("money received") ||
                bodyLower.contains("rcv") ||
                bodyLower.contains("rcvd") ||
                bodyLower.contains("deposit") ||
                bodyLower.contains("add money") ||
                bodyLower.contains("pay") ||
                bodyLower.contains("payment")

        val containsTrx = bodyLower.contains("trxid") ||
                bodyLower.contains("txnid") ||
                bodyLower.contains("trx id") ||
                bodyLower.contains("txn id") ||
                bodyLower.contains("txnid:") ||
                bodyLower.contains("trxid:")

        return containsKeyword && containsTrx
    }

    /**
     * Parses incoming SMS body and extracts structured financial parameters.
     */
    fun parseSms(
        senderHeader: String,
        bodyText: String,
        customFormats: List<CustomFormatEntity>? = null
    ): ParsedSms? {
        if (!isMatchingPaymentSms(senderHeader, bodyText, customFormats)) return null

        val senderUpper = senderHeader.uppercase(Locale.ROOT)
        val bodyLower = bodyText.lowercase(Locale.ROOT)

        var serviceName = when {
            senderUpper.contains("BKASH") || bodyLower.contains("bkash") -> "bKash"
            senderUpper.contains("NAGAD") || bodyLower.contains("nagad") || senderUpper.contains("16167") -> "Nagad"
            senderUpper.contains("ROCKET") || bodyLower.contains("rocket") || senderUpper.contains("16216") -> "Rocket"
            senderUpper.contains("UPAY") || bodyLower.contains("upay") -> "Upay"
            senderUpper.contains("CELLFIN") || bodyLower.contains("cellfin") -> "CellFin"
            else -> "bKash"
        }

        var formatSource = "Built-in Parser"

        if (!customFormats.isNullOrEmpty()) {
            for (format in customFormats) {
                if (format.isEnabled && format.regexPattern.isNotBlank()) {
                    try {
                        val regex = Regex(format.regexPattern, RegexOption.IGNORE_CASE)
                        if (regex.containsMatchIn(bodyText)) {
                            serviceName = format.gateway
                            formatSource = "Custom: ${format.formatName}"
                            break
                        }
                    } catch (_: Exception) {}
                }
            }
        }

        val extracted = FormatAutoDetector.analyzeSampleSms(bodyText, serviceName)

        val amount = extracted.amount ?: 0.0
        val senderNumber = extracted.senderNumber ?: cleanPhoneNumber(senderHeader)
        val fee = extracted.fee ?: 0.0
        val balance = extracted.balance
        val trxId = extracted.trxId ?: "UNKNOWN_TRX"
        val date = extracted.date
        val time = extracted.time

        return ParsedSms(
            serviceName = serviceName,
            amount = amount,
            trxId = trxId,
            senderNumber = senderNumber,
            fee = fee,
            balance = balance,
            date = date,
            time = time,
            rawSms = bodyText,
            formatSource = formatSource
        )
    }

    private fun cleanPhoneNumber(raw: String): String {
        val digits = raw.filter { it.isDigit() || it == '+' }
        return digits.ifBlank { raw }
    }
}
