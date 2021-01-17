package com.parsdroid.verifycodereader

import android.content.*
import android.telephony.SmsMessage
import android.util.Log
import android.widget.Toast
import androidx.core.text.isDigitsOnly

class SmsListener : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        if (intent.action != "android.provider.Telephony.SMS_RECEIVED") {
            return
        }

        intent.extras?.let { bundle ->
            try {
                val pdus = bundle[PDUS] as Array<Any>?
                var messageBody = ""
                var msgFrom: String?
                pdus?.forEach { pdu ->
                    val message: SmsMessage? = SmsMessage.createFromPdu(pdu as ByteArray)
                    msgFrom = message?.originatingAddress
                    messageBody = message?.messageBody.orEmpty()
                }
                Log.d(TAG, messageBody)

                val verifyCode = getVerifyCodeFromSmsText(messageBody) ?: return

                copyToClipboard(context, "VerifyCode", verifyCode)
                Toast.makeText(context, "$verifyCode copied to clipboard", Toast.LENGTH_LONG)
                    .show()
            } catch (e: Exception) {
                Log.e(TAG, e.message.toString())
            }
        }
    }

    private fun copyToClipboard(context: Context, label: String, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
    }

    companion object {
        private const val PDUS = "pdus"
        private val KEYWORDS = arrayOf("رمز", "کد", "password", "code")
        private val TAG = SmsListener::class.java.simpleName


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