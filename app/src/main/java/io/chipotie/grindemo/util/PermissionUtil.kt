package io.chipotie.grindemo.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class PermissionUtil private constructor(val context: Context){

    companion object : SingletonHolder<PermissionUtil, Context>(::PermissionUtil){
        val ACCESS_COARSE_LOCATION_REQUEST_CODE = 1000
    }

    fun areAllPermissionsForScan() : Boolean{
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    //Permission requiered for 6.0 and above
    fun requestPermission(activity: AppCompatActivity){
        ActivityCompat.requestPermissions(activity,
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
            ACCESS_COARSE_LOCATION_REQUEST_CODE)
    }
}