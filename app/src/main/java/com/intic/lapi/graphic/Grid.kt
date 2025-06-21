package com.intic.lapi.graphic

import android.graphics.PointF
import kotlin.math.floor


class Grid<T>(width: Int,height: Int, private val cellSize:Int,createGridObject:(Int, Int)->T) {
    val w = (width / cellSize)
    val h = (height / cellSize)
    private val gridArray = Array(w){ arrayListOf<T>()}
    private val TObject:T
    init {
        for(x in 0 until w){
            for(y in 0 until h){
                this.gridArray[x].add(createGridObject(x,y))
            }
        }
        TObject = createGridObject(-1,-1)
    }

    fun getPosition(x:Int,y:Int): PointF {
        return PointF((x* cellSize).toFloat(), (y* cellSize).toFloat())
    }
    fun getPositionCenter(x:Int,y:Int): PointF {
        val position = getPosition(x,y)
        return centerToGrid(position.x.toInt(),position.y.toInt())
    }

    fun getXY(x: Float,y:Float): PointF {
        val nx = floor((x/cellSize))
        val ny = floor((y/cellSize))
        return PointF(nx, ny)
    }

    fun setGridObject(x:Int,y:Int,value:T){
        if(x >= 0 && y >=0 && x < w && y < h)
            gridArray[x].add(value)
    }
    fun setGridObject(pointF: PointF,value:T){
        val p= getXY(pointF.x,pointF.y)
        setGridObject(p.x.toInt(),p.y.toInt(),value)
    }
    fun getGridObject(x: Int, y:Int): T {
        return if(x >= 0 && y >=0 && x < w && y < h){
            gridArray[x][y]
        }else{
            TObject
        }
    }

    fun centerToGrid(pX:Int,pY:Int): PointF {
        val x = (((pX / cellSize) * cellSize) + (cellSize / 2)).toFloat()
        val y = (((pY / cellSize) * cellSize) + (cellSize / 2)).toFloat()
        return PointF(x,y)
    }
}