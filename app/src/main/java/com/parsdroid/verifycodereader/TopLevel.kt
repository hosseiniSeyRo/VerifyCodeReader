package com.parsdroid.verifycodereader

import android.os.Build

fun isApiLevelAndUp(apiLevel: Int): Boolean {
    return Build.VERSION.SDK_INT >= apiLevel
}