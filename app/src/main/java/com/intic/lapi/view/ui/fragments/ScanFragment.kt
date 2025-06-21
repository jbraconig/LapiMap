package com.intic.lapi.view.ui.fragments

import android.content.Context
import android.location.LocationManager
import android.net.wifi.WifiManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.intic.lapi.R
import com.intic.lapi.view.adapter.ScanAdapter
import com.intic.lapi.viewmodel.ScanWifiResultViewModel
import kotlinx.android.synthetic.main.fragment_scan.*

class ScanFragment : Fragment() {
    val handler = Handler(Looper.getMainLooper())
    val timeMillisecond = 500L//medio segundo
    private lateinit var wifiManager: WifiManager
    private lateinit var locationManager: LocationManager
    var isVisibilityProgression = false
    private lateinit var scanAdapter:ScanAdapter
    //bucle con intervalo de tiempo de medio segundo
    private val runnable = object : Runnable{
        var isOpenDialog= false
        override fun run() {
            if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !isOpenDialog) {
                isOpenDialog =  MaterialAlertDialogBuilder(requireContext()).setTitle(resources.getString(R.string.alert_wifi_title))
                    .setMessage(resources.getString(R.string.alert_gps_disabled)).setCancelable(false)
                    .setNeutralButton(resources.getString(R.string.alert_btn_closet_app)){
                            dialog,which -> requireActivity().finish()
                    }.setPositiveButton(resources.getString(R.string.alert_btn_positive)){
                            dialog, i ->
                        dialog.cancel()
                    }.show().isShowing
            }else if(wifiManager.isScanAlwaysAvailable){
                wifiManager.startScan()
                if(isVisibilityProgression && wifiManager.scanResults.size > 0){
                    rlBaseSchedule.visibility = View.GONE
                    isVisibilityProgression = false
                }
            }
            handler.postDelayed(this,timeMillisecond)
        }
    }
    private fun observerViewModel(){
        val mapViewModel = ViewModelProvider(requireActivity()).get(ScanWifiResultViewModel::class.java)
        mapViewModel.scan.observe(viewLifecycleOwner, Observer {
            scanAdapter.updateData(it)
        })
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_scan, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handler.postDelayed(runnable,timeMillisecond)
        wifiManager = requireContext().applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        wifiManager.startScan()
        locationManager = requireContext().applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        scanAdapter = ScanAdapter()
        rvScan.apply {
            layoutManager = LinearLayoutManager(view.context,LinearLayoutManager.VERTICAL,false)
            adapter = scanAdapter
        }
        if(rlBaseSchedule.visibility == 0){
            isVisibilityProgression = true
        }
        scanAdapter.updateData(wifiManager.scanResults)
        observerViewModel()
    }
    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(runnable)
    }
}