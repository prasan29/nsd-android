package com.prasanna.nsd_android.model

import android.content.Context
import android.net.nsd.NsdServiceInfo
import androidx.lifecycle.MutableLiveData

object NSDHelper : OnResultChanged {
    private var mResult: MutableLiveData<String> = MutableLiveData()
    private lateinit var mNSDOperation: NSDOperation

    fun initiateNSD(result: MutableLiveData<String>, context: Context) {
        mResult = result
        mNSDOperation = NSDOperation(this, context)
    }

    fun unRegisterNSD() {
        mNSDOperation.unRegister()
    }

    override fun onResult(nsdServiceInfo: NsdServiceInfo) {
        // Set LiveData which is bound to the View.
        mResult.postValue(
                "Published result:- \nService name: ${nsdServiceInfo.serviceName}\nService port: ${nsdServiceInfo.port}\nService type: ${nsdServiceInfo.serviceType}")
    }

    override fun onError() {
    }

}

interface OnResultChanged {
    fun onResult(nsdServiceInfo: NsdServiceInfo)
    fun onError()
}