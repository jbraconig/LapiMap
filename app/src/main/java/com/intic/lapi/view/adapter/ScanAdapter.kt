package com.intic.lapi.view.adapter

import android.annotation.SuppressLint
import android.net.wifi.ScanResult
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.intic.lapi.R

class ScanAdapter():RecyclerView.Adapter<ScanAdapter.ViewHolder>(){
    var list = ArrayList<Any>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_scan,parent,false))
    override fun getItemCount() = list.size

    @RequiresApi(Build.VERSION_CODES.R)
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ScanAdapter.ViewHolder, position: Int) {
       val scan = list[position] as ScanResult
        var channel = ""
        when(scan.channelWidth){
            0 -> channel = "20MHz"
            1 -> channel = "40MHz"
            2 -> channel = "80MHz"
            3 -> channel = "160MHz"
            4 -> channel = "80MHz Plus"
        }
        holder.tvScanSSID.text = "${holder.view.resources.getString(R.string.info_ssid)}:${scan.SSID}"
        holder.tvScanBSSID.text = "${holder.view.resources.getString(R.string.info_bssid)}:${scan.BSSID}"
        holder.tvScanChannel.text = "${holder.view.resources.getString(R.string.info_channel)}:${channel}"
        holder.tvScanFrequency.text = "${holder.view.resources.getString(R.string.info_frequency)}:${scan.frequency}"
        holder.tvScanRssi.text = "${holder.view.resources.getString(R.string.info_rssi)}:${scan.level}"
    }

    fun updateData(data:List<ScanResult>){
        list.clear()
        list.addAll(data)
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val tvScanSSID = itemView.findViewById<TextView>(R.id.tvScanSsid)
        val tvScanBSSID = itemView.findViewById<TextView>(R.id.tvScanBssid)
        val tvScanChannel = itemView.findViewById<TextView>(R.id.tvScanChannel)
        val tvScanFrequency = itemView.findViewById<TextView>(R.id.tvScanfrequency)
        val tvScanRssi = itemView.findViewById<TextView>(R.id.tvScanRssi)
        val view = itemView
    }
}