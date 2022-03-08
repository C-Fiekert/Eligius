package com.callum.eligius.api

import com.callum.eligius.helpers.Response
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface RetrofitAPI {
    @GET("simple/price")
    suspend fun bitcoin(
        @Query("ids") currency: String,
        @Query("vs_currencies") code: String
    ): retrofit2.Response<Response>

    @POST("coins/single")
    fun ethereum(): Response
}