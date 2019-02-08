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
import io.chipotie.grindemo.R
import io.chipotie.grindemo.scanner.model.Device

class DeviceViewModel(private val context: Context){

    private val TAG = DeviceViewModel::class.java.simpleName

    private var foundDevices : HashMap<String, Device> = HashMap()

    //LiveData
    private var devices : MutableLiveData<ArrayList<Device>> = object : MutableLiveData<ArrayList<Device>>(){
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
                val strength : Int = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE).toInt()

                if(device != null) {
                    val deviceData = Device(device.name, device.address, context!!.getString(R.string.bluetooth_strenght, strength), false)
                    foundDevices[device.address] = deviceData
                    devices.value = getList(foundDevices)
                }
            }
        }
    }

    private fun getList(devices: HashMap<String, Device>) : ArrayList<Device>{

        val newList : ArrayList<Device> = arrayListOf()
        val keys = devices.keys
        for (key in keys){
            devices[key]?.let { newList.add(it) }
        }

        return newList
    }

    fun getDevices() : LiveData<ArrayList<Device>>{
        return devices
    }

    fun restartDiscovering(){
        BluetoothAdapter.getDefaultAdapter()?.cancelDiscovery()

        if(BuildConfig.DEBUG){
            Log.i(TAG, "Stopping discovering")
        }

        BluetoothAdapter.getDefaultAdapter()?.startDiscovery()

        if(BuildConfig.DEBUG){
            Log.i(TAG, "Starting discovering")
        }
    }

}