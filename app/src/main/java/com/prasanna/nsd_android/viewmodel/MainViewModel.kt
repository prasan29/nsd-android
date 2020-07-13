package com.prasanna.nsd_android.viewmodel

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.prasanna.nsd_android.model.NSDHelper

class MainViewModel(private val contextProvider: () -> Context,
                    private val lifeCycleProvider: () -> LifecycleOwner) : ViewModel() {
    val result = MutableLiveData<String>()
    val publishButtonValue = MutableLiveData<Boolean>()

    init {
        publishButtonValue.value = true

        result.observe(lifeCycleProvider(), Observer {
            publishButtonValue.value = false
        })
    }

    fun onPublishClicked() {
        // Publish API call.
        if (publishButtonValue.value != true) {
            NSDHelper.unRegisterNSD()
            publishButtonValue.value = true
        } else {
            NSDHelper.initiateNSD(result, contextProvider())
        }
    }

    fun unRegister() {
        NSDHelper.unRegisterNSD()
    }

    fun onScanClicked() {
        // Scan API calls.
//        result.value = "Scan"
    }

}