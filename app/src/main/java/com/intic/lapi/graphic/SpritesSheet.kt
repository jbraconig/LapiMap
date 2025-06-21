package com.intic.lapi.graphic

import android.graphics.Bitmap

class SpritesSheet(bitmap: Bitmap, val cellSize:Int) {
    val width = bitmap.width
    val height = bitmap.height
    val pixeles:IntArray
    init {
        pixeles = IntArray(width * height)
        bitmap.getPixels(pixeles,0,width,0,0,width,height)
    }
    fun isEnabled(x:Int,y:Int): Boolean {
        return Sprites(cellSize,x,y,this).getValue()
    }

}