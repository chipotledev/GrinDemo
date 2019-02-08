package io.chipotie.grindemo.scanner.view

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import io.chipotie.grindemo.R
import io.chipotie.grindemo.scanner.viewmodel.DeviceViewModel


class ScannerActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanner)

        if (ContextCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(ACCESS_COARSE_LOCATION),
                100)
        }

        val deviceViewModel = DeviceViewModel(this)
        deviceViewModel.getDevices().observe(this, Observer<ArrayList<BluetoothDevice>>{
            devices ->

            Log.i("Devices", ":" + devices.size)

        })

    }
}
