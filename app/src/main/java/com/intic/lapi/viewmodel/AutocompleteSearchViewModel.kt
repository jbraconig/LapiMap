package com.intic.lapi.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.intic.lapi.firebase.Callback
import com.intic.lapi.firebase.FirestoreService
import com.intic.lapi.model.ZonaModel
import java.lang.Exception

class AutocompleteSearchViewModel: ViewModel() {
    private val firestoreService = FirestoreService()
    val listLocate: MutableLiveData<List<String>> = MutableLiveData()
    val listLocateZone: MutableLiveData<List<ZonaModel>> = MutableLiveData()
    val location: MutableLiveData<String> = MutableLiveData()
    val isLoading = MutableLiveData<Boolean>()

    fun getLocateMap(nameMap:String){
        firestoreService.getListLocateMap(nameMap, object :Callback<List<ZonaModel>>{
            override fun onSuccess(result: List<ZonaModel>?) {
                val newResultList = arrayListOf<String>()
                if (result != null) {
                    for(local in result){
                        newResultList.add(local.name)
                    }
                    listLocate.postValue(newResultList)
                    listLocateZone.postValue(result)
                }
            }

            override fun onFailed(exception: Exception) {
                processFinished()
            }
        })
    }
    fun checkLocation(name:String){
        location.value = name
    }
    fun processFinished(){
        isLoading.value = true
    }

}