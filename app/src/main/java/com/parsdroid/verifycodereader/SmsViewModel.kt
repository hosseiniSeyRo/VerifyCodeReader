package com.parsdroid.verifycodereader

import android.util.Log
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SmsViewModel(private val smsSmsApiService: SmsApiService) : ViewModel() {

    private fun sendSmsMessage(message: String) {
//        _getPaymentStepsObjectState.value = ResponseState.Loading

        val call = smsSmsApiService.sendSmsMessage(message)
        call.enqueue(object : Callback<String> {
            override fun onResponse(
                call: Call<String>,
                response: Response<String>
            ) {
                printLog(response.toString())
                if (response.isSuccessful) {
                    if (response.body() != null) {
//                        _getPaymentStepsObjectState.value =
//                            ResponseState.Success(response.body()!!.toLocalModel())
                    }
                } else {
//                    _getPaymentStepsObjectState.value =
//                        ResponseState.Error(Exception(response.message()))
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
//                _getPaymentStepsObjectState.value = ResponseState.Error(Exception(t))
                t.message?.let { printLog(it) }
            }
        })
    }

    fun printLog(text: String) {
        val myLogTag = "RHLog"
        Log.d(myLogTag, text)
    }
}