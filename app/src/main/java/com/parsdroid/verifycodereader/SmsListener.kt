package com.parsdroid.verifycodereader

import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Telephony.Sms.Intents.SMS_RECEIVED_ACTION
import android.telephony.SmsMessage
import android.util.Log
import androidx.core.text.isDigitsOnly
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SmsListener : HiltBroadcastReceiver() {

    @Inject
    lateinit var sharedPreferenceDataSource: SharedPreferenceDataSource

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
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
                    tempVerifyCode = getVerificationCodeFromSmsText(messageBody)
                    tempVerifyCode != null
                }?.let {
                    tempVerifyCode
                } ?: return

                copyAndToast(context, verifyCode)
                sharedPreferenceDataSource.verifyCode = verifyCode
            } catch (e: Exception) {
                Log.e(TAG, e.message.toString())
            }
        }
    }

    companion object {

        private const val PDU_TYPE = "pdus"
        private val KEYWORDS = listOf("رمز", "کد", "password", "code")
        private val TAG = SmsListener::class.java.simpleName
        private const val COLON = ':'

        private val smsMessageFunction: (ByteArray?, String?) -> SmsMessage? =
            if (isApiLevelAndUp(Build.VERSION_CODES.M)) {
                SmsMessage::createFromPdu
            } else {
                { byteArray, _ ->
                    @Suppress("DEPRECATION")
                    SmsMessage.createFromPdu(byteArray)
                }
            }

        fun getVerificationCodeFromSmsText(smsText: String): String? {
            val text = smsText.trim()
            val keywordIndex = findKeywordIndex(text)
            if (keywordIndex == -1) return null

            text.drop(keywordIndex + 1)
                .split("\\s+".toRegex())
                .forEach { word ->
                    if (word.isDigitsOnly()) {
                        return word
                    }
                    val colonIndex = word.indexOf(COLON)
                    if (colonIndex != -1 &&
                        word.substring(colonIndex).contains("\\d".toRegex())
                    ) {
                        return word.substring(colonIndex + 1)
                    }
                }
            return null
        }

        private fun findKeywordIndex(text: String): Int {
            return text.indexOfAny(KEYWORDS, ignoreCase = true)
        }
    }
}