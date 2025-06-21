package com.intic.lapi.firebase

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.intic.lapi.model.AccessPointsModel
import com.intic.lapi.model.ZonaModel
import com.intic.lapi.model.MapModel
import java.lang.Exception

const val ACCESSPOINTS_COLLECTION_NAME = "accesspoints"
const val LOCALMAP_COLLECTION_NAME = "localmap"
const val MAP_COLLECTION_NAME = "map"

class FirestoreService {
    val firestore = FirebaseFirestore.getInstance()
    val setting = FirebaseFirestoreSettings.Builder().setPersistenceEnabled(true).build()
    init {
        firestore.firestoreSettings = setting
    }

    fun getMap(mac:String,callback: Callback<MapModel>){
        firestore.collection(MAP_COLLECTION_NAME).whereEqualTo("aps.$mac",true).get()
            .addOnSuccessListener {result->
                if(result.documents.size == 0){
                    callback.onFailed(Exception("Busqueda Fallida"))
                }else{
                    for (doc in result){
                        val maps = result.toObjects(MapModel::class.java)
                        callback.onSuccess(maps[0])
                        break
                    }
                }
            }
    }

    fun getListAP(nameMap:String,callback: Callback<List<AccessPointsModel>>){
        firestore.collection(ACCESSPOINTS_COLLECTION_NAME).whereEqualTo("nameMap",nameMap).get()
            .addOnSuccessListener {result->
                for(doc in result){
                    val aps = result.toObjects(AccessPointsModel::class.java)
                    callback.onSuccess(aps)
                    break
                }
            }
    }

    fun getListLocateMap(nameMap: String,callback: Callback<List<ZonaModel>>){
        firestore.collection(LOCALMAP_COLLECTION_NAME).whereEqualTo("nameMap",nameMap).get()
            .addOnSuccessListener {result->
                for(doc in result){
                    val localmap = result.toObjects(ZonaModel::class.java)
                    callback.onSuccess(localmap)
                    break
                }
            }
    }
}