package io.chipotie.grindemo.scanner.view

import android.bluetooth.BluetoothDevice
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.chipotie.grindemo.R
import io.chipotie.grindemo.adapter.DiscoveredDevicesAdapter
import io.chipotie.grindemo.databinding.ActivityScannerBinding
import io.chipotie.grindemo.scanner.model.Device
import io.chipotie.grindemo.util.PermissionUtil
import io.chipotie.grindemo.scanner.viewmodel.DeviceViewModel


class ScannerActivity : AppCompatActivity(), DiscoveredDevicesAdapter.Callback{

    private lateinit var binding : ActivityScannerBinding

    private var deviceViewModel : DeviceViewModel? = null

    private var adapter: DiscoveredDevicesAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Set databinding
        binding = DataBindingUtil.setContentView(this,R.layout.activity_scanner)
        binding.view = this


        //Check for permissions
        if (!PermissionUtil.getInstance(this).areAllPermissionsForScan()){
            renderPermissionAdvice()
            return
        }

        deviceViewModel = DeviceViewModel(this)
        renderBluetoothControls()
        setupRecyclerView()
        initUploadObservers()
        startDiscovery()

    }

    private fun initUploadObservers(){
        this.deviceViewModel?.uploadedDevice?.observe(this, Observer<String> { address ->
            this.deviceViewModel?.confirmUpload(address)
            Toast.makeText(this, "Guardado Correctamente", Toast.LENGTH_SHORT).show()
        })

        this.deviceViewModel?.uploadError?.observe(this, Observer<Boolean> {
            Toast.makeText(this, "Error guardando el dispositivo", Toast.LENGTH_SHORT).show()
        })
    }

    private fun startDiscovery(){
        deviceViewModel?.devices?.observe(this, Observer<ArrayList<Device>>{
                devices ->

            this.adapter?.updateData(devices)

        })
    }

    fun restartDiscovery(){
        deviceViewModel?.restartDiscovering()
    }

    private fun setupRecyclerView(){
        this.adapter =  DiscoveredDevicesAdapter(this, arrayListOf(), this)
        this.binding.rvDiscoveredDevices.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        this.binding.rvDiscoveredDevices.adapter = adapter
    }

    private fun renderPermissionAdvice(){
        this.binding.llPermissionsAdvice.visibility = View.VISIBLE
    }

    private fun renderBluetoothControls(){
        this.binding.llControls.visibility = View.VISIBLE
        this.binding.llPermissionsAdvice.visibility = View.GONE
    }

    fun askPermissions(){
        PermissionUtil.getInstance(this).requestPermission(this)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            PermissionUtil.ACCESS_COARSE_LOCATION_REQUEST_CODE -> {
                if (PermissionUtil.getInstance(this).areAllPermissionsForScan()){
                    renderBluetoothControls()
                    startDiscovery()
                }
            }
        }
    }

    override fun uploadDevice(device: Device) {
        Toast.makeText(this, "Guardando dispositivo", Toast.LENGTH_SHORT).show()
        this.deviceViewModel?.uploadDeviceData(device)
    }
}
