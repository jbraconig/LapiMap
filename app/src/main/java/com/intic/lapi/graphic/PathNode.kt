package com.intic.lapi.graphic

class PathNode(val x:Int, val y: Int){
    var gCost = 0
    var hCost = 0
    var fCost = 0
    var isEnabled = true
    var cameFromNode: PathNode? = null
    fun calculateFCost(){
        fCost = gCost + hCost
    }
    override fun toString(): String {
        return if(isEnabled){
            "1"
        }else{
            "-1"
        }
    }
}