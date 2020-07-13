package com.prasanna.nsd_android.viewmodel

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.prasanna.nsd_android.model.NSDHelper

/**
 * ViewModel class which handles the View operations and data trasnfer between View and Model.
 */
class MainViewModel(contextProvider: () -> Context,
                    lifeCycleProvider: () -> LifecycleOwner) : ViewModel() {
    private val result = MutableLiveData<String>()
    val scanResult = MutableLiveData<String>()

    val publishButtonValue = MutableLiveData<Boolean>()

    init {
        publishButtonValue.value = true

        result.observe(lifeCycleProvider(), Observer {
            publishButtonValue.value = false
        })

        NSDHelper.initialize(contextProvider())
    }

    /**
     * Data binding method for "Publish" button click.
     */
    fun onPublishClicked() {
        // Publish API call.
        if (publishButtonValue.value != true) {
            NSDHelper.unRegisterNSD()
            publishButtonValue.value = true
        } else {
            NSDHelper.initiateNSD(result)
        }
    }

    /**
     * Method to un-register the NSD service.
     */
    fun unRegister() {
        NSDHelper.unRegisterNSD()
    }

    /**
     * Method to disconnect the discovery.
     */
    fun disconnectDiscovery() {
        NSDHelper.stopDiscovery()
    }

    /**
     * Data binding method for "Scan" button click.
     */
    fun onScanClicked() {
        NSDHelper.discoverServices(scanResult)
    }

}