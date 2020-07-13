package com.prasanna.nsd_android.model

import android.content.Context
import android.net.nsd.NsdServiceInfo
import androidx.lifecycle.MutableLiveData

object NSDHelper : OnResultChanged {
    private var mResult: MutableLiveData<String> = MutableLiveData()
    private var mScanningResult: MutableLiveData<String> = MutableLiveData()

    private lateinit var mNSDOperation: NSDOperation

    fun initialize(context: Context) {
        mNSDOperation = NSDOperation(this, context)
    }

    fun initiateNSD(result: MutableLiveData<String>) {
        mResult = result
        mNSDOperation.initiateProcess()
    }

    fun unRegisterNSD() {
        mNSDOperation.unRegister()
    }

    fun discoverServices(scanResult: MutableLiveData<String>) {
        mScanningResult = scanResult
        mNSDOperation.discover()
    }

    fun stopDiscovery() {
        mNSDOperation.stopDiscovery()
    }

    override fun onResult(nsdServiceInfo: NsdServiceInfo) {
        // Set LiveData which is bound to the View.
        mResult.postValue(
                "Published result:- \nService name: ${nsdServiceInfo.serviceName}\nService port: ${nsdServiceInfo.port}\nService type: ${nsdServiceInfo.serviceType}")
    }

    override fun onDiscoveryResult(nsdServiceInfo: NsdServiceInfo) {
        mScanningResult.postValue(
                "Scanned result:- \nService name: ${nsdServiceInfo.serviceName}\nService port: ${nsdServiceInfo.port}\nService type: ${nsdServiceInfo.serviceType}")
    }

    override fun onError() {
    }

}

interface OnResultChanged {
    fun onResult(nsdServiceInfo: NsdServiceInfo)
    fun onDiscoveryResult(nsdServiceInfo: NsdServiceInfo)
    fun onError()
}