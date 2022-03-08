package com.callum.eligius.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

object RetrofitInstance {

    private val client = OkHttpClient.Builder().apply {
        addInterceptor(Interceptor())
    }.build()

    private val retrofit by lazy {
        Retrofit.Builder().baseUrl("https://api.coingecko.com/api/v3/").addConverterFactory(GsonConverterFactory.create()).build()
    }

    val api: RetrofitAPI by lazy {
        retrofit.create(RetrofitAPI::class.java)
    }
}