package com.example.data.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import com.example.data.preferences.StorageHelper
import com.example.data.repository.SmsLogRepository
import com.example.data.utils.SmsParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SmsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) return

        val storageHelper = StorageHelper.getInstance(context)
        if (!storageHelper.isSyncActive()) {
            return
        }

        val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
        if (messages.isNullOrEmpty()) return

        val fullBodyBuilder = StringBuilder()
        var senderHeader = ""

        for (sms in messages) {
            senderHeader = sms.displayOriginatingAddress ?: sms.originatingAddress ?: ""
            fullBodyBuilder.append(sms.displayMessageBody ?: sms.messageBody ?: "")
        }

        val fullBody = fullBodyBuilder.toString()

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val repository = SmsLogRepository(context)
                val enabledFormats = repository.getEnabledFormats()
                val parsedSms = SmsParser.parseSms(senderHeader, fullBody, enabledFormats) ?: return@launch

                repository.processAndSendSms(parsedSms)
            } finally {
                pendingResult.finish()
            }
        }
    }
}
