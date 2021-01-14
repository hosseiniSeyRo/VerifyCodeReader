package com.parsdroid.verifycodereader

import android.content.*
import android.os.Bundle
import android.telephony.SmsMessage
import android.util.Log
import android.widget.Toast
import java.util.*

class SmsListener : BroadcastReceiver() {

    private val preferences: SharedPreferences? = null

    override fun onReceive(context: Context, intent: Intent) {

        if (intent.action == "android.provider.Telephony.SMS_RECEIVED") {
            val bundle: Bundle? = intent.extras
            val messages: Array<SmsMessage?>?
            var msgFrom: String?
            if (bundle != null) {
                try {
                    val pdus = bundle[PDUS] as Array<Any>?
                    messages = arrayOfNulls(pdus!!.size)
                    var msgBody = ""
                    messages.indices.forEach { i ->
                        messages[i] = SmsMessage.createFromPdu(pdus[i] as ByteArray)
                        msgFrom = messages[i]?.originatingAddress
                        msgBody = messages[i]?.messageBody.orEmpty()
                    }
                    Log.d(TAG, msgBody)
                    val verifyCode = getVerifyCodeFromSmsText(msgBody)
                    copyToClipboard(context, "verifyCode", verifyCode)
                    Toast.makeText(context, verifyCode + "copied to clipboard", Toast.LENGTH_SHORT)
                        .show()
                } catch (e: Exception) {
//                            Log.d("Exception caught",e.getMessage());
                }
            }
        }
    }

    private fun getVerifyCodeFromSmsText(smsText: String): String {
        val digitList = ArrayList<String>()
        val builder = StringBuilder()
        for (element in smsText) {
            val c = element
            if (Character.isDigit(c)) {
                builder.append(c)
            } else if (builder.isNotEmpty()) {
                Log.e(TAG, "digit: $builder")
                digitList.add(builder.toString())
                Log.e(TAG, "digitList: $digitList")
                builder.setLength(0)
            }
        }
        if (builder.isNotEmpty()) {
            Log.e(TAG, "digit: $builder")
            digitList.add(builder.toString())
            Log.e(TAG, "digitList: $digitList")
            builder.setLength(0)
        }
        return digitList[digitList.size - 1]
    }

    private fun copyToClipboard(context: Context, label: String, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
    }

    companion object {
        private const val PDUS = "pdus"
        private val TAG = SmsListener::class.java.simpleName
    }
}