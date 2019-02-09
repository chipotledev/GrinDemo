package io.chipotie.grindemo.modules.scanner.view

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.chipotie.grindemo.R
import io.chipotie.grindemo.adapter.DiscoveredDevicesAdapter
import io.chipotie.grindemo.databinding.ActivityScannerBinding
import io.chipotie.grindemo.model.Device
import io.chipotie.grindemo.modules.alldevices.view.AllDevicesActivity
import io.chipotie.grindemo.util.PermissionUtil
import io.chipotie.grindemo.viewmodel.DeviceViewModel
import android.net.Uri
import android.provider.Settings


class ScannerActivity : AppCompatActivity(), DiscoveredDevicesAdapter.Callback{

    private lateinit var binding : ActivityScannerBinding

    private var deviceViewModel : DeviceViewModel? = null

    private var adapter: DiscoveredDevicesAdapter? = null

    private var bluetoothStateReceiver = object: BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action
            if(action == BluetoothAdapter.ACTION_STATE_CHANGED){
                val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)

                when(state){
                    BluetoothAdapter.STATE_ON -> {
                        if(PermissionUtil.getInstance(context!!).areAllPermissionsForScan()){
                            startDiscovery()
                            renderBluetoothControls()
                        }else{
                            renderPermissionAdvice()
                        }
                    }

                    BluetoothAdapter.STATE_OFF -> {
                        if(PermissionUtil.getInstance(context!!).areAllPermissionsForScan()){
                            renderEnableBlurtoothAdvice()
                        }else{
                            renderPermissionAdvice()
                        }
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Set databinding
        binding = DataBindingUtil.setContentView(this,R.layout.activity_scanner)
        binding.view = this


        //Check for permissions
        if (!PermissionUtil.getInstance(this).areAllPermissionsForScan()){

            if(!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)){
                renderPermissionSettingsAdvice()
                return
            }

            renderPermissionAdvice()
            return
        }

        //Check bluetooh is disabled
        if(!BluetoothAdapter.getDefaultAdapter().isEnabled){
            renderEnableBlurtoothAdvice()
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
        this.binding.llControls.visibility = View.GONE
    }

    private fun renderPermissionSettingsAdvice(){
        this.binding.llPermissionsSettingsAdvice.visibility = View.VISIBLE
        this.binding.llControls.visibility = View.GONE
    }

    private fun renderEnableBlurtoothAdvice(){
        this.binding.llEnableBtAdvice.visibility = View.VISIBLE
        this.binding.llControls.visibility = View.GONE
    }

    private fun renderBluetoothControls(){
        this.binding.llControls.visibility = View.VISIBLE
        this.binding.llPermissionsAdvice.visibility = View.GONE
        this.binding.llEnableBtAdvice.visibility = View.GONE
        this.binding.llPermissionsSettingsAdvice.visibility = View.GONE
    }

    fun askPermissions(){
        PermissionUtil.getInstance(this).requestPermission(this)
    }

    fun askPermissionsFromSettings(){
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS

        val uri = Uri.fromParts("package", packageName, null)

        intent.data = uri
        startActivity(intent)
    }

    fun enableBluetooth(){
        val bluetoothIntent = Intent()
        bluetoothIntent.action = android.provider.Settings.ACTION_BLUETOOTH_SETTINGS
        startActivity(bluetoothIntent)

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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_scanner, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.menu_see_all ->{
                startActivity(Intent(this, AllDevicesActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(bluetoothStateReceiver, IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED))
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(bluetoothStateReceiver)
    }
}
