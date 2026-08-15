package com.example.data.utils

import java.util.Locale

data class ExtractedFields(
    val gateway: String,
    val amount: Double?,
    val senderNumber: String?,
    val fee: Double?,
    val balance: Double?,
    val trxId: String?,
    val date: String?,
    val time: String?,
    val generatedPattern: String
)

object FormatAutoDetector {

    fun analyzeSampleSms(sampleText: String, forcedGateway: String = "Auto"): ExtractedFields {
        if (sampleText.isBlank()) {
            return ExtractedFields(
                gateway = if (forcedGateway != "Auto" && forcedGateway != "All") forcedGateway else "bKash",
                amount = null,
                senderNumber = null,
                fee = null,
                balance = null,
                trxId = null,
                date = null,
                time = null,
                generatedPattern = ""
            )
        }

        val textLower = sampleText.lowercase(Locale.ROOT)
        val textUpper = sampleText.uppercase(Locale.ROOT)

        // 1. Gateway Detection
        val detectedGateway = if (forcedGateway != "Auto" && forcedGateway != "All") {
            forcedGateway
        } else {
            when {
                textLower.contains("bkash") -> "bKash"
                textLower.contains("nagad") || textUpper.contains("16167") -> "Nagad"
                textLower.contains("rocket") || textUpper.contains("16216") -> "Rocket"
                textLower.contains("upay") -> "Upay"
                textLower.contains("cellfin") -> "CellFin"
                else -> "Custom"
            }
        }

        // 2. Amount Extraction
        val amountRegex = Regex("""(?:received|cash\s*in|rcv|deposit|add\s*money|amount|pay)?\s*(?:Tk|BDT|Tk\.)\s*([\d,]+(?:\.\d{1,2})?)""", RegexOption.IGNORE_CASE)
        val amountMatch = amountRegex.find(sampleText)
            ?: Regex("""(?:Tk|BDT|Tk\.)\s*([\d,]+(?:\.\d{1,2})?)""", RegexOption.IGNORE_CASE).find(sampleText)
        val amount = amountMatch?.groupValues?.get(1)?.replace(",", "")?.toDoubleOrNull()

        // 3. Sender Number Extraction
        val senderRegex = Regex("""(?:from|by|sender:?|acc:?)\s*(\+?\d{11,14}(?:-\d)?|\d{11}(?:-\d)?)""", RegexOption.IGNORE_CASE)
        val senderMatch = senderRegex.find(sampleText)
            ?: Regex("""(?:\b|\+?)01\d{9}\b""").find(sampleText)
        val senderNumber = senderMatch?.groupValues?.let { if (it.size > 1) it[1] else it[0] }

        // 4. Fee Extraction
        val feeRegex = Regex("""Fee\s*(?:Tk|BDT|Tk\.)?\s*:?\s*([\d,]+(?:\.\d{1,2})?)""", RegexOption.IGNORE_CASE)
        val feeMatch = feeRegex.find(sampleText)
        val fee = feeMatch?.groupValues?.get(1)?.replace(",", "")?.toDoubleOrNull() ?: 0.0

        // 5. Balance Extraction
        val balanceRegex = Regex("""(?:Balance|Bal)\s*(?:Tk|BDT|Tk\.)?\s*:?\s*([\d,]+(?:\.\d{1,2})?)""", RegexOption.IGNORE_CASE)
        val balanceMatch = balanceRegex.find(sampleText)
        val balance = balanceMatch?.groupValues?.get(1)?.replace(",", "")?.toDoubleOrNull()

        // 6. TrxID Extraction
        val trxRegex = Regex("""(?:TrxID|TxnID|Trx\s*ID|Txn\s*Id)[:\s]*([A-Za-z0-9]+)""", RegexOption.IGNORE_CASE)
        val trxMatch = trxRegex.find(sampleText)
        val trxId = trxMatch?.groupValues?.get(1)

        // 7. Date Extraction
        val dateRegex = Regex("""(\d{2}[/.-]\d{2}[/.-]\d{2,4}|\d{4}[/.-]\d{2}[/.-]\d{2})""")
        val dateMatch = dateRegex.find(sampleText)
        val date = dateMatch?.groupValues?.get(1)

        // 8. Time Extraction
        val timeRegex = Regex("""(\d{1,2}:\d{2}(?:\s*[AP]M)?)""", RegexOption.IGNORE_CASE)
        val timeMatch = timeRegex.find(sampleText)
        val time = timeMatch?.groupValues?.get(1)

        val pattern = "(?:received|cash in|rcv|deposit|pay).*?Tk\\s*([\\d,]+\\.?\\d*).*?from\\s*(\\+?\\d+).*?TrxID\\s*([A-Za-z0-9]+)"

        return ExtractedFields(
            gateway = detectedGateway,
            amount = amount,
            senderNumber = senderNumber,
            fee = fee,
            balance = balance,
            trxId = trxId,
            date = date,
            time = time,
            generatedPattern = pattern
        )
    }
}
