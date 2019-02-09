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
import io.chipotie.grindemo.util.ConnectivityUtils


class ScannerActivity : AppCompatActivity(), DiscoveredDevicesAdapter.Callback{

    private lateinit var binding : ActivityScannerBinding

    private var deviceViewModel : DeviceViewModel? = null

    private var adapter: DiscoveredDevicesAdapter? = null

    //Receiver to listen changes on bluetooth settings
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

    //Init the live data
    private fun initUploadObservers(){
        this.deviceViewModel?.uploadedDevice?.observe(this, Observer<String> { address ->
            this.deviceViewModel?.confirmUpload(address)
            Toast.makeText(this, getString(R.string.saved), Toast.LENGTH_SHORT).show()
        })

        this.deviceViewModel?.uploadError?.observe(this, Observer<Boolean> {
            Toast.makeText(this, getString(R.string.error_saving), Toast.LENGTH_SHORT).show()
        })
    }

    //Start the discovery of bluetooth devices
    private fun startDiscovery(){
        deviceViewModel?.devices?.observe(this, Observer<ArrayList<Device>>{
                devices ->

            this.adapter?.updateData(devices)

        })
    }

    //Restar the discovery
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

    //Ask permission fot ACCESS_FINE_LOCATION nedeed for Android 6 and above
    fun askPermissions(){
        PermissionUtil.getInstance(this).requestPermission(this)
    }

    //If the user ask for not show the permission dialog again, you should go to settings
    fun askPermissionsFromSettings(){
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS

        val uri = Uri.fromParts("package", packageName, null)

        intent.data = uri
        startActivity(intent)
    }

    //Open the bluetooth settings
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

        if(!ConnectivityUtils.isInternetConnectionAvailable(this)){
            Toast.makeText(this, getString(R.string.no_internet_message), Toast.LENGTH_SHORT).show()
            return
        }

        Toast.makeText(this, getString(R.string.savind), Toast.LENGTH_SHORT).show()
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


    //Lifecycle
    override fun onResume() {
        super.onResume()
        registerReceiver(bluetoothStateReceiver, IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED))
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(bluetoothStateReceiver)
    }
}
