package com.parsdroid.verificationcodereader

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.chibatching.kotpref.livedata.asLiveData
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val sharedPreferenceDataSource: SharedPreferenceDataSource,
    application: Application
) : AndroidViewModel(application) {

    val verificationCode: LiveData<String> = Transformations.map(
        sharedPreferenceDataSource.asLiveData(
            sharedPreferenceDataSource::verifyCode
        )
    ) {
        it ?: application.getString(R.string.no_code)
    }

    fun onCodeClicked() {
        if (verificationCode.value != application.getString(R.string.no_code)) {
            copyAndToast(application, verificationCode.value!!)
        }
    }
}