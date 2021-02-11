package com.parsdroid.verifycodereader

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build

fun copyToClipboard(context: Context, label: String, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText(label, text)
    clipboard.setPrimaryClip(clip)
}

fun isApiLevelAndUp(apiLevel: Int): Boolean {
    return Build.VERSION.SDK_INT >= apiLevel
}