package com.parsdroid.verificationcodereader

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Telephony.Sms.Intents.SMS_RECEIVED_ACTION
import android.telephony.SmsMessage
import androidx.core.text.isDigitsOnly
import dagger.hilt.android.AndroidEntryPoint
import io.sentry.Sentry
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
        val verificationCode = try {
            intent.extras?.parseSmsAndFindVerificationCode() ?: return
        } catch (e: Exception) {
            Sentry.captureException(e)
            return
        }
        copyAndToast(context, verificationCode)
        sharedPreferenceDataSource.verifyCode = verificationCode
    }

    companion object {

        private const val PDU_TYPE = "pdus"
        private val KEYWORDS = listOf("رمز", "کد", "password", "code")
        private const val COLON = ':'

        @OptIn(ExperimentalStdlibApi::class)
        private fun Bundle.parseSmsAndFindVerificationCode(): String? {
            @Suppress("UNCHECKED_CAST")
            val pdus = get(PDU_TYPE) as? Array<ByteArray?> ?: return null
            val format = getString("format")

            val smsText: String = pdus.mapNotNull {
                smsMessageFunction(it, format)
            }.joinToString(separator = "") { it.messageBody.orEmpty() }
            return findVerificationCodeInText(smsText)
        }

        private val smsMessageFunction: (ByteArray?, String?) -> SmsMessage? =
            if (isApiLevelAndUp(Build.VERSION_CODES.M)) {
                SmsMessage::createFromPdu
            } else {
                { byteArray, _ ->
                    @Suppress("DEPRECATION")
                    SmsMessage.createFromPdu(byteArray)
                }
            }

        fun findVerificationCodeInText(text: String): String? {
            val trimmedText = text.trim()
            val keywordIndex = findKeywordIndex(trimmedText)
            if (keywordIndex == -1) return null

            trimmedText.drop(keywordIndex + 1)
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