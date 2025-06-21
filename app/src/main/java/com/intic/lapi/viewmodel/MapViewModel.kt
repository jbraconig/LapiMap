package com.intic.lapi.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.intic.lapi.firebase.Callback
import com.intic.lapi.firebase.FirestoreService
import com.intic.lapi.model.AccessPointsModel
import com.intic.lapi.model.MapModel
import java.lang.Exception

class MapViewModel: ViewModel() {
    private val firestoreService = FirestoreService()
    val map: MutableLiveData<MapModel> = MutableLiveData()
    val listAps:MutableLiveData<List<AccessPointsModel>> = MutableLiveData()
    val isLoading = MutableLiveData<Boolean>()
    val isError = MutableLiveData<Map<String,String>>()
    fun getMap(mac:String){
        firestoreService.getMap(mac, object : Callback<MapModel> {
            override fun onSuccess(result: MapModel?) {
                if (result != null) {
                    map.value = result
                }
            }
            override fun onFailed(exception: Exception) {
                Log.i("MAPA", "Error: $exception")
                val fail = mutableMapOf<String,String>()
                fail["msg"] = exception.toString()
                fail["error"] = 0.toString()
                isError.value = fail
                processFinished()
            }
        })
    }
    fun getAps(nameMap:String){
        firestoreService.getListAP(nameMap,object :Callback<List<AccessPointsModel>>{
            override fun onSuccess(result: List<AccessPointsModel>?) {
                listAps.postValue(result)
            }
            override fun onFailed(exception: Exception) {}
        })
    }
    fun processFinished(){
        isLoading.value = true
    }
}