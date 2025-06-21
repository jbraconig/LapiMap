package com.intic.lapi.viewmodel

import android.net.wifi.ScanResult
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ScanWifiResultViewModel: ViewModel() {
    val scan: MutableLiveData<List<ScanResult>> = MutableLiveData()
    fun setScanResult(list: List<ScanResult>){
        scan.postValue(list)
    }
}