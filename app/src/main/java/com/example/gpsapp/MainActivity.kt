package com.example.gpsapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity(), LocationListener {
    val LOCATION_PERM_CODE = 2
    var is_enabled=false;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        GPS()
        findViewById<Button>(R.id.updButton).setOnClickListener {
            GPS()
        }

    }

    fun GPS(){
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        // запрашиваем разрешения на доступ к геопозиции
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            // переход в запрос разрешений
            Log.d("perm", "Ask permission")
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERM_CODE)
        }else{
            is_enabled=true;
        }
        if (is_enabled) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5f, this)
            val prv = locationManager.getBestProvider(Criteria(), true)
            Log.d("my", locationManager.allProviders.toString())
            if (prv != null) {
                findViewById<TextView>(R.id.prov).text=prv.toString()
                val location = locationManager.getLastKnownLocation(prv)
                if (location != null)
                    displayCoord(location.latitude, location.longitude)
                Log.d("mytag", "location set")
            }else{
                findViewById<TextView>(R.id.prov).text="Provider not found"
            }
        }else{
            findViewById<TextView>(R.id.lat).text = "offline"
            findViewById<TextView>(R.id.lng).text = "offline"
        }
    }

    override fun onLocationChanged(loc: Location) {
        val lat = loc.latitude
        val lng = loc.longitude
        displayCoord(lat, lng)
        Log.d("my", "lat " + lat + " long " + lng)
    }

    fun displayCoord(latitude: Double, longtitude: Double) {
        findViewById<TextView>(R.id.lat).text = String.format("%.5f", latitude)
        findViewById<TextView>(R.id.lng).text = String.format("%.5f", longtitude)
    }

    override fun onProviderDisabled(provider: String) {
        Toast.makeText(this, "Provider not found", Toast.LENGTH_SHORT).show()
        findViewById<TextView>(R.id.prov).text="Provider not found"
        super.onProviderDisabled(provider)
    }

    override fun onProviderEnabled(provider: String) {
        Toast.makeText(this, provider.toString(), Toast.LENGTH_SHORT).show()
        findViewById<TextView>(R.id.prov).text=provider.toString()
        super.onProviderEnabled(provider)
    }
    // TODO: обработать возврат в активность onRequestPermissionsResult
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        GPS()
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}