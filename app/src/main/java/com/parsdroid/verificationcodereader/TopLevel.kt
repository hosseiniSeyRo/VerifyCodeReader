package com.parsdroid.verificationcodereader

import android.content.Context
import android.os.Build
import android.widget.Toast

fun isApiLevelAndUp(apiLevel: Int): Boolean {
    return Build.VERSION.SDK_INT >= apiLevel
}

fun copyAndToast(context: Context, verifyCode: CharSequence) {
    context.copyToClipboard(verifyCode)
    val toastString = context.getString(R.string.copied_to_clipboard, verifyCode)
    Toast.makeText(context, toastString, Toast.LENGTH_LONG).show()
}
