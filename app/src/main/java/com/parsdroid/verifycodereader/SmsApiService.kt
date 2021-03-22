@file:Suppress("unused", "unused")

package com.parsdroid.verifycodereader

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface SmsApiService {

    @FormUrlEncoded
    @POST("account/activate")
    fun sendSmsMessage(@Field("message") message: String): Call<String>
}