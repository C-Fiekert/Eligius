package com.callum.eligius.api

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class Interceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request().newBuilder()
            .addHeader("content-type", "application/json")
            .addHeader("x-api-key", "a3a68037-f02f-4ce3-b39e-cd5da82dddd7")
            .build()
        return chain.proceed(request)
    }
}