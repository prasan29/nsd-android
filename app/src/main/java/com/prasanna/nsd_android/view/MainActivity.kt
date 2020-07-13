package com.prasanna.nsd_android.view

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import com.prasanna.nsd_android.R
import com.prasanna.nsd_android.databinding.ActivityMainBinding
import com.prasanna.nsd_android.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {
    private var mBinding: ActivityMainBinding? = null
    val contextProvider: () -> Context = { this }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mBinding!!.lifecycleOwner = this
        val lifecycleProvider: () -> LifecycleOwner = { this }
        mBinding!!.viewModel = MainViewModel(contextProvider, lifecycleProvider)
    }

    override fun onStop() {
        super.onStop()
        mBinding?.viewModel?.unRegister()
        mBinding?.viewModel?.disconnectDiscovery()
    }
}
