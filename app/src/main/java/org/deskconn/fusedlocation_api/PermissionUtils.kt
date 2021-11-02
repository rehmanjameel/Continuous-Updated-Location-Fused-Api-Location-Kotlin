package org.deskconn.fusedlocation_api

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PackageManagerCompat
import java.util.jar.Manifest

object PermissionUtils {

    //**** Function for asking Fine Access Location permission ****//

    fun askToAccessFineLocationPermission(activity: AppCompatActivity, requestId: Int){
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(ACCESS_FINE_LOCATION),
            requestId
        )
    }

    //**** Granted the Access Fine Location Permission ****//

    fun checkAccessFineLocationGranted(context: Context): Boolean{
        return ContextCompat.checkSelfPermission(
            context,
            ACCESS_FINE_LOCATION
        )== PackageManager.PERMISSION_GRANTED
    }

    //**** Checking the Location is enabled or not ****//

    fun isLocationEnabled(context: Context): Boolean{
        val gfgLocationManager: LocationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return gfgLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || gfgLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }


    //**** Show Dialogue Box if Gps is not enabled  ****//

    fun showGPSNotEnabledDialogue(context: Context){
        AlertDialog.Builder(context)
            .setTitle(context.getString(R.string.common_google_play_services_enable_title))
            .setMessage(context.getString(R.string.app_name))
            .setCancelable(false)
            .setPositiveButton(context.getString(R.string.common_google_play_services_enable_button)){_,_ ->
                context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }.show()
    }

}