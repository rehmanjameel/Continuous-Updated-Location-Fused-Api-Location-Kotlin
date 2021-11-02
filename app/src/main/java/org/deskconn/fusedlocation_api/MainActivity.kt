package org.deskconn.fusedlocation_api

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationListener
import android.location.LocationRequest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY

class MainActivity : AppCompatActivity() {

    var mLocationService: LocationService = LocationService()
    lateinit var mServiceIntent: Intent
    lateinit var mActivity: Activity

    private val LOCATION_PERMISSION_REQ_CODE = 1000
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var latitudeTextView: TextView
    private lateinit var longitudeTextView: TextView
    private lateinit var currentLocationButton: Button
    private lateinit var serviceButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mActivity = this

        latitudeTextView = findViewById(R.id.latitudeTextView)
        longitudeTextView = findViewById(R.id.longitudeTextView)
        currentLocationButton = findViewById(R.id.currentLocationButton)
        serviceButton = findViewById(R.id.serviceButton)

            when{
                PermissionUtils.checkAccessFineLocationGranted(this) -> {
                    when{
                        PermissionUtils.isLocationEnabled(this) -> {
                            setLocationListener()

                        }
                        else -> {
                            PermissionUtils.showGPSNotEnabledDialogue(mActivity)
                        }
                    }
                }
                else -> {
                    PermissionUtils.askToAccessFineLocationPermission(this,
                        LOCATION_PERMISSION_REQ_CODE)
                }
            }

        currentLocationButton.setOnClickListener {
            if (PermissionUtils.isLocationEnabled(this)){
                setLocationListener()
            }
            else{
                PermissionUtils.showGPSNotEnabledDialogue(this)
            }
        }

        serviceButton.setOnClickListener {
            mLocationService = LocationService()
            mServiceIntent = Intent(this, mLocationService.javaClass)
            if (!PermissionUtils.isMyServiceRunning(mLocationService.javaClass, mActivity)) {
                startService(mServiceIntent)
                Toast.makeText(
                    mActivity,
                    getString(R.string.service_start_successfully),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    mActivity,
                    getString(R.string.service_already_running),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    // getting location every 5 secs, for something very accurate
    /*val gfgLocationRequest = LocationRequest().setInterval(5000).setFastestInterval(5000)
        .setPriority(LocationRequest.QUALITY_HIGH_ACCURACY)*/

    private fun setLocationListener(){
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        val locationRequest = LocationRequest().setInterval(500).setFastestInterval(100)
            .setPriority(LocationRequest.QUALITY_HIGH_ACCURACY)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }

        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            object : LocationCallback(){
                override fun onLocationResult(locationResult: LocationResult){
                    super.onLocationResult(locationResult)
                    for (location in locationResult.locations){
                        latitudeTextView.text = "Latitude is: ${location.latitude}"
                        longitudeTextView.text = "Longitude is: ${ location.longitude }"
                     }
                }
            },
            Looper.getMainLooper()
        )
    }



    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            // Location Permission
            LOCATION_PERMISSION_REQ_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    when {
                        PermissionUtils.isLocationEnabled(this) ->{
                            // Setting things up
                        }
                        else -> {
                            PermissionUtils.showGPSNotEnabledDialogue(this)
                        }
                    }
                } else {
                    Toast.makeText(this, getString(R.string.common_google_play_services_enable_text), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    //**** Service Code here ****//
    override fun onDestroy() {
        if (::mServiceIntent.isInitialized) {
            stopService(mServiceIntent)
        }
        super.onDestroy()
    }

}