package com.parsdroid.verificationcodereader

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

abstract class HiltBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {}
}