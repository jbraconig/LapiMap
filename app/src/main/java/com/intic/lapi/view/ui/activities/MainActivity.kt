package com.intic.lapi.view.ui.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.intic.lapi.R
import com.intic.lapi.viewmodel.ScanWifiResultViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var wifiManager: WifiManager
    private val wifiScanReceiver = object : BroadcastReceiver(){
        @RequiresApi(Build.VERSION_CODES.M)
        override fun onReceive(context: Context?, intent: Intent) {
            val success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED,false)
            if(success){
                scanSuccess()
            }else{
                scanFailure()
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        this.configNav()
    }

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val intentFilter = IntentFilter()
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        context.registerReceiver(wifiScanReceiver,intentFilter)
        return super.onCreateView(name, context, attrs)
    }
    private fun configNav(){
        NavigationUI.setupWithNavController(bnvMenu, Navigation.findNavController(this,R.id.fragContent))// permite la navegacion de los fragmentos
    }
    private fun scanSuccess(){
        val result = wifiManager.scanResults
        orderMayor(result as ArrayList<ScanResult>)
        val mapViewModel = ViewModelProvider(this).get(ScanWifiResultViewModel::class.java)
        mapViewModel.setScanResult(result)
        //Log.i("Result MainActivity","$result")
    }
    private fun orderMayor(list:ArrayList<ScanResult>){
        var tmp: ScanResult
        for(i in list.indices){
            for(j in list.indices){
                if(list[i].level > list[j].level){
                    tmp = list[j]
                    list[j] = list[i]
                    list[i] = tmp
                }
            }
        }
    }
    private fun scanFailure(){
        val result = wifiManager.scanResults
    }


}