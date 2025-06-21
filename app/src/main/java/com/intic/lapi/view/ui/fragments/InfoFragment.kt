package com.intic.lapi.view.ui.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.net.wifi.WifiManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.intic.lapi.R
import kotlinx.android.synthetic.main.fragment_info.*

class InfoFragment : Fragment() {
    val handler = Handler(Looper.getMainLooper())
    val timeMillisecond = 1000L//un segundo
    private lateinit var wifiManager: WifiManager
    @SuppressLint("SetTextI18n")
    val runnable = object : Runnable {
        override fun run() {
            if(!wifiManager.isWifiEnabled){
                tvInfoSsid.text = resources.getString(R.string.label_wifi_disable)
            }else if(wifiManager.connectionInfo.frequency == -1) {
                tvInfoSsid.text = resources.getString(R.string.label_wifi_disconect)
            }else{
                val wifiInfo = wifiManager.connectionInfo
                tvInfoSsid.text = "${resources.getString(R.string.info_ssid)}: ${wifiInfo.ssid}"
                tvInfoBssid.text = "${resources.getString(R.string.info_bssid)}: ${wifiInfo.bssid}"
                tvInfoFrecuencia.text = "${resources.getString(R.string.info_frequency)}: ${wifiInfo.frequency}"
                tvInfoRssi.text = "${resources.getString(R.string.info_rssi)}: ${wifiInfo.rssi}"
                tvInfoVelocidadEnlace.text = "${resources.getString(R.string.info_link_speed)}: ${wifiInfo.linkSpeed}"
            }
            handler.postDelayed(this,timeMillisecond)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        wifiManager = view.context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        handler.postDelayed(runnable,timeMillisecond)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(runnable)
    }
    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(runnable)
    }

    override fun onResume() {
        super.onResume()
        handler.postDelayed(runnable,timeMillisecond)
    }

}