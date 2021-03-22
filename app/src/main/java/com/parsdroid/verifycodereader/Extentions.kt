package com.parsdroid.verifycodereader

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.view.View
import androidx.lifecycle.AndroidViewModel

fun Context.copyToClipboard(text: CharSequence, label: String = "Verification code") {
    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText(label, text)
    clipboard.setPrimaryClip(clip)
}

fun Activity.openAppInfo() {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        .setData(Uri.fromParts("package", packageName, null))
    startActivity(intent)
}

fun View.setOnClickListener(clickListener: () -> Unit) {
    setOnClickListener {
        clickListener()
    }
}

val AndroidViewModel.application
    get() = getApplication<VerifyCodeReaderApplication>()