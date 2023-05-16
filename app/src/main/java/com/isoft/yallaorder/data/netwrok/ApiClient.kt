package com.isoft.yallaorder.data.netwrok

import android.util.Log
import com.isoft.yallaorder.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class ApiClient {
//    var logging = HttpLoggingInterceptor { message ->
//        Log.d(
//            "http_tag",
//            message
//        )
//    }.apply {
//        level = HttpLoggingInterceptor.Level.BODY
//    }
//
//    private var httpClient = OkHttpClient.Builder()
//        .addInterceptor(logging).build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        //.client(httpClient)
        .build()
    val api = retrofit.create(GoogleSheetsApis::class.java)
}