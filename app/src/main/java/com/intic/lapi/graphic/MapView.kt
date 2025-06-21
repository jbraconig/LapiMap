package com.intic.lapi.graphic

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.net.wifi.ScanResult
import android.util.Log
import android.view.MotionEvent
import android.view.MotionEvent.INVALID_POINTER_ID
import android.view.ScaleGestureDetector
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.core.view.MotionEventCompat
import com.intic.lapi.R
import com.intic.lapi.model.AccessPointsModel
import com.intic.lapi.model.ZonaModel
import kotlin.math.max
import kotlin.math.min

class MapView(context:Context): AppCompatImageView(context) {
    private lateinit var mapBitmap: Bitmap
    private val ubicacionWifi = LocationWifi()
    private val pathF = PathFinding()
    private val listAps = arrayListOf<AccessPointsModel>()
    private val listScanResult = arrayListOf<AccessPointsModel>()
    private var listPathfinding = mutableListOf<PathNode>()
    private val listLocalZone = arrayListOf<ZonaModel>()
    private val touchPoint = Point()
    private var lastTouchX = 0f
    private var lastTouchY = 0f
    private val lastUserPoint = PointF(0f,0f)
    private var posX = 0f
    private var posY = 0f
    private val focusPoint = PointF()
    private val focusInitPoint = PointF()
    private var isMapLoader = false
    private var isEnableScan = false
    private var isEnabledPointUp = true
    private var mActivePointerid = INVALID_POINTER_ID
    private var scaleFactor = 1f
    private var isFirst = true
    private val pointInit:BitmapDrawable = ContextCompat.getDrawable(context, R.drawable.baseline_place) as BitmapDrawable
    private val bitmapInit: Bitmap = Bitmap.createScaledBitmap(pointInit.bitmap, 100.times(scaleFactor).toInt(), 100.times(scaleFactor).toInt(),false)
    private val pointEnd = ContextCompat.getDrawable(context, R.drawable.baseline_push_pin_black) as BitmapDrawable
    private val bitmapEnd: Bitmap = Bitmap.createScaledBitmap(pointEnd.bitmap, 100.times(scaleFactor).toInt(), 100.times(scaleFactor).toInt(),false)
    private val pointUser = ContextCompat.getDrawable(context, R.drawable.baseline_person_pin_circle_black) as BitmapDrawable
    private val bitmapDrawable: Bitmap = Bitmap.createScaledBitmap(pointUser.bitmap, 100.times(scaleFactor).toInt(), 100.times(scaleFactor).toInt(),false)
    private val scaleGestureListener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            val scale = detector.scaleFactor
            scaleFactor = max(0.1f, min(scaleFactor*scale,5f))
            if(scaleFactor < 5f && scaleFactor > 0.1f){
                val centerX = detector.focusX
                val centerY = detector.focusY
                //calcular la diferencia
                var diffX = centerX - posX
                var diffY = centerY - posY
                //Sacalar la diferencia
                diffX = diffX * scale - diffX
                diffY = diffY * scale - diffY
                //Actualizar el origen
                posX -= diffX
                posY -= diffY
                invalidate()
            }
            return true
        }
    }
    private val scaleDetector = ScaleGestureDetector(context, scaleGestureListener)
    fun loadMap(
        bitmap: Bitmap,
        distancia: HashMap<String, Int>
    ) {
        mapBitmap = bitmap
        isMapLoader = true
        pathF.loadMap(bitmap)
        ubicacionWifi.setDistancia(distancia)
        invalidate()
    }
    fun loadAPS(list:List<AccessPointsModel>) {
        listAps.clear()
        listAps.addAll(list)
        invalidate()
    }
    fun loadScanAps(resultScan:List<ScanResult>) {
        isEnableScan = false
        listScanResult.clear()
        val rs = ubicacionWifi.sumatoria(resultScan,5)
        for(ap in listAps){
            for(apr in rs){
                if(listScanResult.size == 3) continue
                if(apr.BSSID == ap.BSSID){
                    ap.rssi = apr.level.toFloat()
                    ap.distancePixel = ubicacionWifi.distanciaMetrosAPixeles(apr.level,apr.frequency)
                    ap.distance = ubicacionWifi.distanciaRSSIaMetro(apr.level,apr.frequency)
                    ap.frequency = apr.frequency
                    listScanResult.add(ap)
                }
            }
        }
        /*
        if(listScanResult.size == 3){
            var first = listScanResult[0]
            for(i in 1 until listScanResult.size){
                val d = ubicacionWifi.calculoDistancia(PointF(first.locationX,first.locationY), PointF(listScanResult[i].locationX,listScanResult[i].locationY))
                val d1 = d-listScanResult[i].distancePixel
                if(listScanResult[i].distancePixel > d1){
                    listScanResult[i].distancePixel = d1
                }
            }
        }*/
        isEnableScan = true
        invalidate()
    }
    fun loadZonas(list:List<ZonaModel>){
        //Log.i("ZONAS","${list.size}")
        listLocalZone.clear()
        listLocalZone.addAll(list)
        invalidate()
    }
    fun focusPoint(fp:String): ZonaModel? {
        val zone =  listLocalZone.find {fp == it.name}
        if(zone!=null){
            if(focusInitPoint.x == 0f && focusInitPoint.y == 0f){
                return null
            }else{
                focusPoint.x = zone.locationX
                focusPoint.y = zone.locationY
                val pc = PointF((width.div(2)-(focusPoint.x*scaleFactor)),height.div(2)-(focusPoint.y*scaleFactor))
                posX = pc.x; posY = pc.y
                val fip = pathF.grid.getXY(focusInitPoint.x,focusInitPoint.y)//focus init point
                val fep = pathF.grid.getXY(focusPoint.x,focusPoint.y)//focus end point
                Log.i("INIT PATH", "$fip")
                listPathfinding = pathF.findPath(fip.x.toInt(),fip.y.toInt(),fep.x.toInt(),fep.y.toInt())
                invalidate()
            }
        }
        return zone

    }
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        super.onTouchEvent(event)
        scaleDetector.onTouchEvent(event)
        when(MotionEventCompat.getActionMasked(event)){
            MotionEvent.ACTION_DOWN -> {
                MotionEventCompat.getActionIndex(event).also {
                    lastTouchX = MotionEventCompat.getX(event,it)
                    lastTouchY = MotionEventCompat.getY(event,it)
                    //Log.i("LastTouch","${PointF(lastTouchX,lastTouchY)}")
                }
                mActivePointerid = MotionEventCompat.getPointerId(event,0)
            }
            MotionEvent.ACTION_MOVE -> {
                val (x:Float,y:Float) = MotionEventCompat.findPointerIndex(event,mActivePointerid).let {
                    MotionEventCompat.getX(event,it) to MotionEventCompat.getY(event,it)
                }
                if(!scaleDetector.isInProgress){
                    posX +=x - lastTouchX
                    posY +=y - lastTouchY
                    invalidate()
                }
                lastTouchX = x
                lastTouchY = y
                isEnabledPointUp = false
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                mActivePointerid = INVALID_POINTER_ID
                if(!scaleDetector.isInProgress && isEnabledPointUp){
                    val x = (lastTouchX - posX) / scaleFactor
                    val y = (lastTouchY - posY) / scaleFactor
                    val p = pathF.grid.getXY(x,y)
                    val tp = pathF.grid.getPosition(p.x.toInt(),p.y.toInt())
                    touchPoint.x = tp.x.toInt()
                    touchPoint.y = tp.y.toInt()
                    //Log.i("ACTION_UP","$touchPoint")
                    //Log.i("Ubicacion","$p")
                    invalidate()
                }
                isEnabledPointUp = true
            }
            MotionEvent.ACTION_POINTER_UP ->{
                //Log.i("ACTION_POINTER_UP","")
                MotionEventCompat.getActionIndex(event).also { pointerIndex ->
                    MotionEventCompat.getPointerId(event,pointerIndex).takeIf {it == mActivePointerid}?.run {
                        val newPointerIndex = if(pointerIndex == 0) 1 else 0
                        lastTouchX = MotionEventCompat.getX(event,newPointerIndex)
                        lastTouchY = MotionEventCompat.getY(event,newPointerIndex)
                        val x = (lastTouchX - posX) / scaleFactor
                        val y = (lastTouchY - posY) / scaleFactor
                        val p = pathF.grid.getXY(x,y)
                        val tp = pathF.grid.getPosition(p.x.toInt(),p.y.toInt())
                        touchPoint.x = tp.x.toInt()
                        touchPoint.y = tp.y.toInt()
                        Log.i("ACTION_UP","$touchPoint")
                        Log.i("Ubicacion","$p")
                        Toast.makeText(context,"Touch: $touchPoint",Toast.LENGTH_LONG).show()
                        mActivePointerid = MotionEventCompat.getPointerId(event,newPointerIndex)
                        isEnabledPointUp = false
                    }
                }
            }
        }
        return true
    }
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {
            this.drawView(it)
        }
    }
    private fun drawView(canvas: Canvas) {
        canvas.drawColor(Color.WHITE)
        if(isFirst && isMapLoader){
            isFirst = false
            val pc = PointF((((width - mapBitmap.width)/2).toFloat()),(((height - mapBitmap.height)/2).toFloat()))
            posX = pc.x; posY = pc.y
        }
        canvas.apply {
            save()
            translate(posX, posY)
            drawMap(this)
            //drawAPTriangulation(this)
            drawPathFinding(this)
            drawUbicacion(this)
            drawZone(this)
            //drawGrid(this)
            restore()
        }
    }
    private fun drawMap(canvas: Canvas) {
        val size = 2000f
        if(isMapLoader){
            val rectF = RectF(0f,0f,size*scaleFactor,size*scaleFactor)
            val rect = Rect(0,0,size.toInt(),size.toInt())
            canvas.drawBitmap(mapBitmap,rect,rectF,null)
        }
    }
    private fun drawAPTriangulation(canvas: Canvas) {
        val paint = Paint()
        if(isEnableScan){
            val lsr = arrayListOf<AccessPointsModel>()
            lsr.addAll(listScanResult)
            if(lsr.size == 3){
                var lastAp = lsr.last()
                for(apscan in lsr){
                    paint.color = Color.argb(50, 255, 255, 50)
                    canvas.drawCircle(apscan.locationX*scaleFactor,apscan.locationY*scaleFactor,apscan.distancePixel*scaleFactor,paint)
                    paint.strokeWidth = 3f
                    paint.color = Color.BLUE
                    canvas.drawLine(lastAp.locationX*scaleFactor,lastAp.locationY*scaleFactor,apscan.locationX*scaleFactor,apscan.locationY*scaleFactor,paint)
                    lastAp = apscan
                }
            }
        }
    }
    private fun drawZone(canvas: Canvas){
        val paint = Paint()
        paint.textSize = 40f*scaleFactor
        paint.color = Color.BLUE
        val lLocalZone = arrayListOf<ZonaModel>()
        lLocalZone.addAll(listLocalZone)
        for(lz in lLocalZone){
            val ubiLabel = lz.nameLabel.length*pathF.cellSize
            canvas.drawText(lz.nameLabel,(lz.locationX-ubiLabel)*scaleFactor,lz.locationY*scaleFactor, paint)
        }
    }
    private fun drawUbicacion(canvas: Canvas) {
        if(isMapLoader && isEnableScan){
            val lsr = arrayListOf<AccessPointsModel>()
            lsr.addAll(listScanResult)
            if(lsr.size == 3){
                ubicacionWifi.setListAps(lsr)
                val paint = Paint()
                paint.color = Color.argb(127, 0, 255, 0)
                val pi = ubicacionWifi.ubicacionInicial()
                val getGridUbi = pathF.grid.getXY(pi.x,pi.y)
                pathF.grid.getGridObject(getGridUbi.x.toInt(),getGridUbi.y.toInt()).let {
                    if(it.isEnabled){
                        lastUserPoint.x = pi.x
                        lastUserPoint.y = pi.y
                        //canvas.drawCircle(pi.x*scale,pi.y*scale,25f*scale,paint)
                        canvas.drawBitmap(bitmapDrawable,(lastUserPoint.x.times(scaleFactor)).minus(bitmapDrawable.height.div(2)),(lastUserPoint.y.times(scaleFactor)).minus(bitmapDrawable.height),paint)
                        this.focusInitPoint.x = lastUserPoint.x
                        this.focusInitPoint.y = lastUserPoint.y
                    }else{
                        this.focusInitPoint.x = 0f
                        this.focusInitPoint.y = 0f
                    }
                }
            }
        }
    }
    private fun drawPathFinding(canvas: Canvas){
        if(isMapLoader && listPathfinding.size > 0){
            val paint = Paint()
            paint.color = Color.RED
            paint.strokeWidth = 10f
            var fx = listPathfinding[0].x
            var fy = listPathfinding[0].y
            val pInit = pathF.grid.getPositionCenter(fx,fy)
            canvas.drawBitmap(bitmapInit,(pInit.x.times(scaleFactor)).minus(bitmapInit.height.div(2)),(pInit.y.times(scaleFactor)).minus(bitmapInit.height),paint)
            for(i in 1 until listPathfinding.size){
                val path = listPathfinding[i]
                val first = pathF.grid.getPositionCenter(fx,fy)
                val last = pathF.grid.getPositionCenter(path.x,path.y)
                if(first.equals(last.x,last.y)) continue
                canvas.drawLine(first.x*scaleFactor,first.y*scaleFactor,last.x*scaleFactor,last.y*scaleFactor,paint)
                fx = path.x
                fy = path.y
            }
            val fp = pathF.grid.centerToGrid(focusPoint.x.toInt(),focusPoint.y.toInt())
            canvas.drawBitmap(bitmapEnd,(fp.x.times(scaleFactor)).minus(bitmapEnd.height.div(2)),(fp.y.times(scaleFactor)).minus(bitmapEnd.height),paint)
            //canvas.drawCircle(fp.x*scaleFactor,fp.y*scaleFactor,10f,paint)//final del pathfindig
        }
    }
    private fun drawGrid(canvas: Canvas){
        val paint = Paint()
        if(isMapLoader){
            val w = mapBitmap.width / pathF.cellSize
            val h = mapBitmap.height / pathF.cellSize
            for(x in 0 until w){
                for(y in 0 until h){
                   pathF.grid.getGridObject(x,y)?.let {path->
                        val poffset = PointF(path.x.toFloat(),path.y.toFloat())
                        val offset = pathF.grid.getPositionCenter(poffset.x.toInt(),poffset.y.toInt())
                        canvas.drawText(path.toString(),offset.x*scaleFactor,offset.y*scaleFactor, paint)
                    }

                }
            }
        }
    }
}