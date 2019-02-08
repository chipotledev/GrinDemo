package io.chipotie.grindemo.scanner.viewmodel

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.chipotie.grindemo.BuildConfig

class DeviceViewModel(private val context: Context){

    private val TAG = DeviceViewModel::class.java.simpleName

    private var foundDevices : ArrayList<BluetoothDevice> = arrayListOf()

    //LiveData
    private var devices : MutableLiveData<ArrayList<BluetoothDevice>> = object : MutableLiveData<ArrayList<BluetoothDevice>>(){
        override fun onActive() {
            context.registerReceiver(receiver, IntentFilter(BluetoothDevice.ACTION_FOUND))
            BluetoothAdapter.getDefaultAdapter()?.startDiscovery()

            if(BuildConfig.DEBUG){
                Log.i(TAG, "Starting discovering")
            }
        }

        override fun onInactive() {
            context.unregisterReceiver(receiver)
            BluetoothAdapter.getDefaultAdapter()?.cancelDiscovery()

            if(BuildConfig.DEBUG){
                Log.i(TAG, "Stopping discovering")
            }
        }
    }

    private var receiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action
            if (BluetoothDevice.ACTION_FOUND == action) {
                val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)

                if(device != null) {
                    foundDevices.add(device)
                    devices.value = foundDevices
                }
            }
        }
    }

    fun getDevices() : LiveData<ArrayList<BluetoothDevice>>{
        return devices
    }

}