package com.parsdroid.verificationcodereader

import android.content.Context
import com.chibatching.kotpref.KotprefModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedPreferenceDataSource @Inject constructor(
    @ApplicationContext context: Context
) : KotprefModel(context) {

    var verifyCode by nullableStringPref()
}