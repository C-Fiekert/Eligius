package com.callum.eligius.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.callum.eligius.repository.Repository

class ResponseModelFactory(private val repository: Repository): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ResponseModel(repository) as T
    }

}