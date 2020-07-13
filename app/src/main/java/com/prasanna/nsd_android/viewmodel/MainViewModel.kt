package com.prasanna.nsd_android.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.prasanna.nsd_android.model.NSDHelper

class MainViewModel(private val contextProvider: () -> Context) : ViewModel() {
    val result = MutableLiveData<String>()
    fun onPublishClicked() {
        // Publish API call.
        NSDHelper.initiateNSD(result, contextProvider())
    }

    fun unRegister() {
        NSDHelper.unRegisterNSD()
    }

    fun onScanClicked() {
        // Scan API calls.
        result.value = "Scan"
    }

}