package com.intic.lapi.view.ui.fragments

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.location.LocationManager
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore.Images.Media
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.intic.lapi.R
import com.intic.lapi.graphic.MapView
import com.intic.lapi.model.ZonaModel
import com.intic.lapi.model.MapModel
import com.intic.lapi.viewmodel.AutocompleteSearchViewModel
import com.intic.lapi.viewmodel.MapViewModel
import com.intic.lapi.viewmodel.ScanWifiResultViewModel
import com.squareup.picasso.Callback
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import java.io.File
import java.io.FileOutputStream

class MapFragment : Fragment() {
    //private val wifiManager = requireContext().applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    private lateinit var pantallaMap:MapView
    val handler = Handler(Looper.getMainLooper())
    val timeMillisecond = 500L//medio segundo
    private lateinit var wifiManager: WifiManager
    private lateinit var locationManager: LocationManager
    private lateinit var mapViewModel:MapViewModel
    private lateinit var autocsViewModel: AutocompleteSearchViewModel
    var isFirstEjecution = true
    var macWifiConection = ""
    //bucle con intervalo de tiempo de medio segundo
    private val runnable = object : Runnable{
        var isOpenDialog= false
        override fun run() {
            if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !isOpenDialog) {
                isOpenDialog =  MaterialAlertDialogBuilder(requireContext()).setTitle(resources.getString(
                    R.string.alert_wifi_title))
                    .setMessage(resources.getString(R.string.alert_gps_disabled)).setCancelable(false)
                    .setNeutralButton(resources.getString(R.string.alert_btn_closet_app)){
                            _, _ -> requireActivity().finish()
                    }.setPositiveButton(resources.getString(R.string.alert_btn_positive)){
                            dialog, _ ->
                        dialog.cancel()
                    }.show().isShowing
            }else if(!wifiManager.isWifiEnabled && !isOpenDialog){
                    isOpenDialog =  MaterialAlertDialogBuilder(requireContext()).setTitle(resources.getString(
                        R.string.alert_wifi_title))
                        .setMessage(resources.getString(R.string.label_wifi_disable)).setCancelable(false)
                        .setNeutralButton(resources.getString(R.string.alert_btn_closet_app)){
                                dialog,which -> requireActivity().finish()
                        }.setPositiveButton(resources.getString(R.string.alert_btn_positive)){
                                dialog, i ->
                            isOpenDialog = false
                            dialog.cancel()
                        }.show().isShowing
                }else if(wifiManager.connectionInfo.frequency == -1 && !isOpenDialog) {
                    isOpenDialog =  MaterialAlertDialogBuilder(requireContext()).setTitle(resources.getString(
                        R.string.alert_wifi_title))
                        .setMessage(resources.getString(R.string.label_wifi_disconect)).setCancelable(false)
                        .setNeutralButton(resources.getString(R.string.alert_btn_closet_app)){
                                dialog,which -> requireActivity().finish()
                        }.setPositiveButton(resources.getString(R.string.alert_btn_positive)){
                                dialog, i ->
                            dialog.cancel()
                            isOpenDialog = false
                        }.show().isShowing
                }else if(!isOpenDialog){
                   val mac = wifiManager.connectionInfo.bssid
                    if(mac != macWifiConection){
                        macWifiConection = mac
                        mapViewModel.getMap(mac)
                    }
            }
            if(wifiManager.isScanAlwaysAvailable){
                wifiManager.startScan()
            }
            handler.postDelayed(this,timeMillisecond)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pantallaMap = MapView(requireContext().applicationContext)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return pantallaMap//inflater.inflate(R.layout.fragment_map, container, false)
    }
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        wifiManager = view.context.getApplicationContext().getSystemService(Context.WIFI_SERVICE) as WifiManager
        locationManager = view.context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        handler.postDelayed(runnable,timeMillisecond)
        val activity = requireActivity()
        mapViewModel = ViewModelProvider(activity).get(MapViewModel::class.java)
        autocsViewModel = ViewModelProvider(activity).get(AutocompleteSearchViewModel::class.java)
        observerViewModel()

    }
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun observerViewModel(){
        var contador=0
        mapViewModel.map.observe(viewLifecycleOwner, Observer<MapModel>{
            autocsViewModel.getLocateMap(it.nameMap)
            mapViewModel.getAps(it.nameMap)
            contador++
            if(contador==1){
                getMapa(it.nameMap,it.imgUrl, it.distancia)
            }
        })
        mapViewModel.listAps.observe(viewLifecycleOwner, Observer {
            pantallaMap.loadAPS(it)
        })
        contador=0
        mapViewModel.isError.observe(viewLifecycleOwner, Observer {
            contador++
            if(contador==1) {
                val msg = "${it["msg"]} \nReconectese a una red wifi autorizada para visualizar el mapa. \nLa MAC del wifi no pertenece a la lista de la zona."
                MaterialAlertDialogBuilder(requireContext()).setTitle(
                    resources.getString(
                        R.string.alert_wifi_title
                    )
                )
                    .setMessage(msg).setCancelable(false)
                    .setNeutralButton(resources.getString(R.string.alert_btn_closet_app)) { _, _ ->
                        requireActivity().finish()
                    }
                    .setPositiveButton(resources.getString(R.string.alert_btn_positive)) { dialog, _ ->
                        dialog.cancel()
                    }.show()
            }
        })
        val mapViewModel = ViewModelProvider(requireActivity()).get(ScanWifiResultViewModel::class.java)
        mapViewModel.scan.observe(viewLifecycleOwner, Observer {
            pantallaMap.loadScanAps(it)
        })
        val autocsViewModel = ViewModelProvider(requireActivity()).get(AutocompleteSearchViewModel::class.java)
        autocsViewModel.listLocateZone.observe(viewLifecycleOwner,Observer<List<ZonaModel>>(){
            pantallaMap.loadZonas(it)
        })
        autocsViewModel.location.observe(viewLifecycleOwner,Observer<String>(){
            val name = pantallaMap.focusPoint(it)
            if(name == null){
                if(!isFirstEjecution)
                    Toast.makeText(requireContext(),"No se encontro ruta disponible",Toast.LENGTH_LONG).show()
                Log.i("Location", "No se encontro la ruta")
                isFirstEjecution = false
            }else{
                Log.i("Location", it)
            }
        })
        autocsViewModel.checkLocation("")
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun getMapa(name: String, url: String, distancia: HashMap<String, Int>){
        val contectResolver = requireContext().contentResolver
        // almacenamiento local
        val path = requireActivity().filesDir
        val rutacarpeta = "/mapas/"
        val nombreMapa = "$name.png"
        val mapaImagen = File(path.path+rutacarpeta+nombreMapa)
        val uri: Uri = Uri.fromFile(mapaImagen)
        // Comprobamos si existe la carpeta
        val directorioImagenes = File(path , rutacarpeta)
        if (!directorioImagenes.exists()) directorioImagenes.mkdirs()//creamos la carpeta si no existe
        // Comprobamos si existe el mapa
        if(mapaImagen.exists()){
            val bitmapMap = Media.getBitmap(contectResolver, uri)
            pantallaMap.loadMap(bitmapMap, distancia)
            mapViewModel.processFinished()
            Log.i("IMGLOAD","Mapa ya existe")
        }else{
            Picasso.get().load(url).fetch(object :Callback{
                override fun onSuccess() {
                    Log.i("IMGLOAD","Carga completa")
                    Picasso.get().load(url).networkPolicy(NetworkPolicy.OFFLINE).into(object :Target{
                        override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
                        override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {}
                        @RequiresApi(Build.VERSION_CODES.Q)
                        override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                            Log.i("IMGLOAD","Bitmap Cargado")
                            pantallaMap.loadMap(bitmap!!, distancia)
                            // almacena la imagen en la ubicacion local
                            bitmap.compress(
                                Bitmap.CompressFormat.PNG,
                                100,
                                FileOutputStream(mapaImagen.path)
                            )
                            Log.i("IMGLOAD","Se Guardo ${mapaImagen.path}")
                            mapViewModel.processFinished()
                        }
                    })
                }
                override fun onError(e: Exception?) {}
            })
        }
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(runnable)
    }
    override fun onResume() {
        super.onResume()
        isFirstEjecution = false
    }

}


