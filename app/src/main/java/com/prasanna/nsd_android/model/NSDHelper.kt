package com.prasanna.nsd_android.model

import android.content.Context
import android.net.nsd.NsdServiceInfo
import androidx.lifecycle.MutableLiveData

/**
 * Helper class for NSD interfacing.
 */
object NSDHelper : OnResultChanged {
    private var mResult: MutableLiveData<String> = MutableLiveData()
    private var mScanningResult: MutableLiveData<String> = MutableLiveData()
    private lateinit var contextProvider: () -> Context

    private lateinit var mNSDOperation: NSDOperation

    /**
     * Method to instantiate the NSD resources.
     */
    fun initialize(context: Context) {
        mNSDOperation = NSDOperation(this, context)
    }

    /**
     * Method to initiate the registration.
     */
    fun initiateNSD(result: MutableLiveData<String>) {
        mResult = result
        mNSDOperation.initiateProcess(contextProvider())
    }

    /**
     * Method to un-register the resources.
     */
    fun unRegisterNSD() {
        mNSDOperation.unRegister()
    }

    /**
     * Method that helps in discovering the services.
     */
    fun discoverServices(scanResult: MutableLiveData<String>) {
        mScanningResult = scanResult
        mNSDOperation.discover()
    }

    /**
     * Method to stop discovery.
     */
    fun stopDiscovery() {
        mNSDOperation.stopDiscovery()
    }

    /**
     * Method which holds the result of registration.
     */
    override fun onResult(nsdServiceInfo: NsdServiceInfo) {
        // Set LiveData which is bound to the View.
        mResult.postValue(
                "Published result:- \nService name: ${nsdServiceInfo.serviceName}\nService port: ${nsdServiceInfo.port}\nService type: ${nsdServiceInfo.serviceType}")
    }

    /**
     * Method that holds the discovery results.
     */
    override fun onDiscoveryResult(nsdServiceInfo: NsdServiceInfo) {
        mScanningResult.postValue(
                "Scanned result:- \nService name: ${nsdServiceInfo.serviceName}\nService port: ${nsdServiceInfo.port}\nService type: ${nsdServiceInfo.serviceType}")
    }

    /**
     * Method helps in identifying the errors.
     */
    override fun onError() {
    }

}

/**
 * Listener for result or error.
 */
interface OnResultChanged {
    fun onResult(nsdServiceInfo: NsdServiceInfo)
    fun onDiscoveryResult(nsdServiceInfo: NsdServiceInfo)
    fun onError()
}