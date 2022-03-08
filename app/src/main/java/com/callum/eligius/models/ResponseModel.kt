package com.callum.eligius.models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.callum.eligius.helpers.Response
import com.callum.eligius.repository.Repository
import kotlinx.coroutines.launch

class ResponseModel(private val repository: Repository): ViewModel() {
    val myResponse: MutableLiveData<retrofit2.Response<Response>> = MutableLiveData()

    fun getBitcoin(ids: String, currency: String) {
        viewModelScope.launch {
            val response: retrofit2.Response<Response> = repository.getBitcoin(ids, currency)
            myResponse.value = response
        }
    }
}