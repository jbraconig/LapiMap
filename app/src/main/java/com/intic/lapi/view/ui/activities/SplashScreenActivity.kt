package com.intic.lapi.view.ui.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.intic.lapi.R
import kotlinx.android.synthetic.main.activity_splash_screen.*

class SplashScreenActivity : AppCompatActivity() {
    private val REQUEST_ACCESS_FINE = 0
    private lateinit var alert: MaterialAlertDialogBuilder
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        val intent = Intent(this, MainActivity::class.java)
        alert = MaterialAlertDialogBuilder(this).setTitle(resources.getString(R.string.alert_wifi_title))
            .setMessage(resources.getString(R.string.alert_gps_msg)).setCancelable(false)
            .setNeutralButton(resources.getString(R.string.alert_btn_closet_app)){
                    dialog,which -> finish()
            }.setPositiveButton(resources.getString(R.string.alert_btn_positive)){
                    dialog, i ->
                dialog.cancel()
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),REQUEST_ACCESS_FINE)
            }

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
                alert.show()
            }else{
                alert.show()
            }
        }else{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                //Verifica permisos para Android 6.0+
                val permissionCheck: Int = ContextCompat.checkSelfPermission(
                    this.applicationContext, Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                    Log.i("Mensaje", "No se tiene permiso para leer.")
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        225
                    )
                } else {
                    Log.i("Mensaje", "Se tiene permiso para leer!")
                }
            }
            Log.i("Permisos","Ya cuenta con permiso GPS")
            val animacion = AnimationUtils.loadAnimation(this, R.anim.animacion)
            ivLogo.startAnimation(animacion)
            animacion.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationRepeat(animation: Animation?) {}
                override fun onAnimationEnd(animation: Animation?) {
                    startActivity(intent)
                    finish()
                }
                override fun onAnimationStart(animation: Animation?) {}
            })
        }


    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == REQUEST_ACCESS_FINE){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,resources.getString(R.string.toast_permisos), Toast.LENGTH_LONG).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }else{
                alert.show()
            }

        }
    }
}