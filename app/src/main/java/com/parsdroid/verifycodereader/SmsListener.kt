package com.parsdroid.verifycodereader

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Telephony.Sms.Intents.SMS_RECEIVED_ACTION
import android.telephony.SmsMessage
import android.util.Log
import android.widget.Toast
import androidx.core.text.isDigitsOnly

class SmsListener : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        if (intent.action != SMS_RECEIVED_ACTION) {
            return
        }

        intent.extras?.let { bundle ->
            try {
                @Suppress("UNCHECKED_CAST")
                val pdus = bundle[PDU_TYPE] as? Array<ByteArray?> ?: return
                val format = bundle.getString("format")

                var tempVerifyCode: String? = null
                val verifyCode = pdus.lastOrNull {
                    val smsMessage = smsMessageFunction(it, format)
                    val msgFrom = smsMessage?.originatingAddress
                    val messageBody = smsMessage?.messageBody.orEmpty()
                    Log.d(TAG, "$msgFrom: $messageBody")
                    tempVerifyCode = getVerifyCodeFromSmsText(messageBody)
                    tempVerifyCode != null
                }?.let {
                    tempVerifyCode
                } ?: return

                copyToClipboard(context, "VerifyCode", verifyCode)
                val toastString = context.getString(R.string.copied_to_clipboard, verifyCode)
                Toast.makeText(context, toastString, Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Log.e(TAG, e.message.toString())
            }
        }
    }

    companion object {

        private const val PDU_TYPE = "pdus"
        private val KEYWORDS = arrayOf("رمز", "کد", "password", "code")
        private val TAG = SmsListener::class.java.simpleName

        private val smsMessageFunction: (ByteArray?, String?) -> SmsMessage? =
            if (isApiLevelAndUp(Build.VERSION_CODES.M)) {
                SmsMessage::createFromPdu
            } else {
                { byteArray, _ ->
                    @Suppress("DEPRECATION")
                    SmsMessage.createFromPdu(byteArray)
                }
            }

        fun getVerifyCodeFromSmsText(smsText: String): String? {
            val words = smsText.trim().split("\\s+".toRegex())
            val keywordIndex = findKeywordIndex(words)
            if (keywordIndex == -1) return null

            return words.drop(keywordIndex + 1).firstOrNull {
                it.isDigitsOnly()
            }
        }

        private fun findKeywordIndex(words: List<String>): Int {
            return words.indexOfLast { word ->
                KEYWORDS.any { keyword ->
                    word.contains(keyword, ignoreCase = true)
                }
            }
        }

    }
}