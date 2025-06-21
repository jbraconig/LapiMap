package com.intic.lapi.graphic

import android.content.Context
import android.graphics.*
import android.net.wifi.ScanResult
import android.util.Log
import com.intic.lapi.model.AccessPointsModel
import java.text.DecimalFormat
import kotlin.collections.ArrayList
import kotlin.math.abs
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.sqrt

class LocationWifi {
    private val pasoX = 10f
    private val pasoY = 10f
    private val aps = arrayListOf<AccessPointsModel>()
    private val listSumatorias = Array(3){ arrayListOf<Int>() }
    private var distancia: HashMap<String, Int> = hashMapOf()

    fun setDistancia(dis: HashMap<String, Int>) {
        distancia = dis;
    }
    fun setListAps(aps:ArrayList<AccessPointsModel>){
        this.aps.clear()
        this.aps.addAll(aps)
    }
    /*funcion a minimizar
    * 1: obtener la división entre valores obtenidos y potencias
    * 2: las sumatoria de los valores absolutos de las diferencias por pares debe ser cero
    */
    private fun calculoMinimizar(pix:Float,piy:Float,aps:ArrayList<AccessPointsModel>): Float {
        val vDistancias = Array(3){0f}
        val vDivisiones = Array(3){0f}
        var resultado = 0f
        for(i in 0 until aps.size){
            vDistancias[i] = calculoDistancia(PointF(aps[i].locationX,aps[i].locationY),PointF(pix,piy))
            vDivisiones[i] = vDistancias[i].div(aps[i].distancePixel)
        }
        for(i in vDivisiones.indices){
            var uno = 0; var dos = i + 1
            if(dos==3){
              uno = 1; dos = 2
            }
            resultado += abs(vDivisiones[uno]-vDivisiones[dos])
        }
        return resultado
    }
    //funcion que calcula la distancia entre dos puntos
    fun calculoDistancia(p1: PointF,p2:PointF): Float {
        return sqrt((p2.x-p1.x).pow(2)+(p2.y-p1.y).pow(2))
    }
    private fun puntoInicial(aps: ArrayList<AccessPointsModel>): PointF {
        val mediaP = PointF()
        for(ap in aps){
            mediaP.x += ap.locationX
            mediaP.y += ap.locationY
        }
        mediaP.x = mediaP.x.div(aps.size)
        mediaP.y = mediaP.y.div(aps.size)
        return mediaP
    }
    fun drawUbicacion(canvas: Canvas, context: Context, scale:Float): PointF {
        val paint = Paint()
        paint.color = Color.argb(127, 0, 255, 0)
        val pi = ubicacionInicial()
        //canvas.drawCircle(pi.x*scale,pi.y*scale,25f*scale,paint)
        //drawDistanciasApsUsuario(canvas,pi,scale)
        return pi
    }
    fun drawDistanciasApsUsuario(canvas: Canvas,pi:PointF,scala:Float){
        val paint = Paint()
        paint.strokeWidth = 5f
        for(ap in aps){
            paint.color = Color.BLACK
            canvas.drawLine(ap.locationX*scala,ap.locationY*scala,pi.x*scala,pi.y*scala,paint)
            paint.color = Color.RED
            paint.textSize = 20f*scala
            canvas.drawText("${ap.distancePixel}",(ap.locationX+50)*scala,ap.locationY*scala,paint)
            val formatDecimal = DecimalFormat("##,##0.00")
            canvas.drawText("${formatDecimal.format(ap.distance)} Mts.",(ap.locationX+50)*scala,(ap.locationY+50)*scala,paint)
        }
    }
    fun distanciaMetrosAPixeles(level: Int, frequency: Int): Float {
        val disMetro = calculoDistancia(PointF(distancia["xa"]?.toFloat()!!,distancia["ya"]?.toFloat()!!), PointF(distancia["xb"]?.toFloat()!!,distancia["yb"]?.toFloat()!!))// representa a un metro de distancia en pixeles
        return distanciaRSSIaMetro(level,frequency)*disMetro
    }
    fun distanciaRSSIaMetro(level:Int,frequency:Int):Float{
        val exp = (27.55 -(20* log10(frequency.toDouble())) + abs(level)) / 20.0
        return 10.0.pow(exp).toFloat()
    }
    fun sumatoria(list: List<ScanResult>,cantidad:Int): ArrayList<ScanResult> {//sumatoria de una lista de tres
        val newLis = list as ArrayList<ScanResult>
        var cont = 0
        for(i in list){
            cont++
            if(cont>3) continue
            when(cont){
                1 -> {
                    this.complementoSumatoria(cont,newLis,i.level,cantidad)
                }
                2 -> {
                    this.complementoSumatoria(cont,newLis,i.level,cantidad)
                }
                3 -> {
                    this.complementoSumatoria(cont,newLis,i.level,cantidad)
                }
            }
        }
        return newLis
    }
    private fun complementoSumatoria(cont:Int,nl:ArrayList<ScanResult>,data:Int,cantidad:Int){
        if(listSumatorias[cont-1].size == cantidad){
            val auxList = arrayListOf<Int>()
            for (c in 0 until cantidad){
                if(c == 0){
                    auxList.add(data)
                }else{
                    auxList.add(listSumatorias[cont-1][c-1])
                }
            }
            listSumatorias[cont-1].clear()
            listSumatorias[cont-1].addAll(auxList)
        }else{
            listSumatorias[cont-1].add(data)
        }
        var suma = 0
        for(number in listSumatorias[cont-1]){
            suma += number
        }
        nl[cont-1].level = suma
        nl[cont-1].level = nl[cont-1].level.div(listSumatorias[cont-1].size)
    }
    fun ubicacionInicial(): PointF {
        val pi = puntoInicial(aps)
        var minimizar = calculoMinimizar(pi.x,pi.y,aps)
        for (i in 1..100){
            var px = pi.x + pasoX
            var newMinimizar = calculoMinimizar(px,pi.x,aps)
            if(newMinimizar < minimizar){minimizar  = newMinimizar; pi.x = px}
            var py = pi.y + pasoY
            newMinimizar = calculoMinimizar(pi.x,py,aps)
            if(newMinimizar < minimizar){minimizar  = newMinimizar; pi.y = py}
            px = pi.x-pasoX
            newMinimizar = calculoMinimizar(px,pi.y,aps)
            if(newMinimizar < minimizar){minimizar  = newMinimizar; pi.x = px}
            py = pi.y-pasoY
            newMinimizar = calculoMinimizar(pi.x,py,aps)
            if(newMinimizar < minimizar){minimizar  = newMinimizar; pi.y = py}
        }
        return pi
    }
    fun locationUser():PointF{
        val p1 = FloatArray(2)
        val p2 = FloatArray(2)
        val p3 = FloatArray(2)
        val r1 = aps[0].distancePixel
        val r2 = aps[1].distancePixel
        val r3 = aps[2].distancePixel
        val ex = FloatArray(2)
        val ey = FloatArray(2)
        val p3p1 = FloatArray(2)
        var jval = 0f
        var ival = 0f
        var p3p1i = 0f
        var temp = 0f
        p1[0]=aps[0].locationX
        p1[1]=aps[0].locationY
        p2[0]=aps[1].locationX
        p2[1]=aps[1].locationY
        p3[0]=aps[2].locationX
        p3[1]=aps[2].locationY
        for(i in p1.indices){
            val t1 = p2[i]
            val t2 = p1[i]
            val t = t1 - t2
            temp += (t*t)
        }
        val d = sqrt(temp)
        for(i in p1.indices){
            val t1 = p2[i]
            val t2 = p1[i]
            val exx = (t1-t2) / sqrt(temp)
            ex[i]=exx
        }
        for(i in p3.indices){
            val t1 = p3[i]
            val t2 = p1[i]
            val t3 = t1-t2
            p3p1[i] = t3
        }
        for(i in ex.indices){
            val t1 = ex[i]
            val t2 = p3p1[i]
            ival +=(t1*t2)
        }
        for(i in p3.indices){
            val t1 = p3[i]
            val t2 = p1[i]
            val t3 = ex[i] * ival
            val t = t1-t2-t3
            p3p1i += (t*t)
        }
        for(i in p3.indices){
            val t1 = p3[i]
            val t2 = p1[i]
            val t3 = ex[i] * ival
            val eyy = (t1-t2-t3)/ sqrt(p3p1i)
            ey[i] = eyy
        }
        for(i in ey.indices){
            val t1 = ey[i]
            val t2 = p3p1[i]
            jval += (t1*t2)
        }
        val xval = (r1.pow(2) - r2.pow(2) + d.pow(2)) / (2*d)
        val yval = ((r1.pow(2) - r3.pow(2) + ival.pow(2) + jval.pow(2))/(2*jval)) - ((ival/jval)*xval)
        val x = p1[0] + (ex[0]*xval) + (ey[0]*yval)
        val y = p1[1] + (ex[1]*xval) + (ey[1]*yval)
        Log.i("Location PI","${ex[0]}, ${ey[0]}, $jval, $ival, $xval, $yval, X:$x, Y:$y")
        return PointF(x,y)
    }
}
