package com.callum.eligius.repository

import com.callum.eligius.api.RetrofitInstance
import com.callum.eligius.helpers.Response
import java.util.*

class Repository {

    suspend fun getBitcoin(ids: String, currency: String): retrofit2.Response<Response> {
        return RetrofitInstance.api.bitcoin(ids, currency)
    }
}