package io.chipotie.grindemo.modules.alldevices.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.chipotie.grindemo.R
import io.chipotie.grindemo.adapter.AllDevicesAdapter
import io.chipotie.grindemo.databinding.ActivityAllDevicesBinding
import io.chipotie.grindemo.model.Device
import io.chipotie.grindemo.viewmodel.AllDevicesViewModel
import kotlin.collections.ArrayList

class AllDevicesActivity : AppCompatActivity() {

    private lateinit var binding : ActivityAllDevicesBinding

    private var allDevicesViewModel : AllDevicesViewModel? = null

    private var adapter : AllDevicesAdapter? = null

    private var currentDevices : ArrayList<Device>? = null

    private var isFilterActive = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_all_devices)

        setupRecyclerView()

        allDevicesViewModel = AllDevicesViewModel(application)

        allDevicesViewModel?.retrieveAllDevices()

        allDevicesViewModel?.devices?.observe(this, Observer<ArrayList<Device>> { devices ->
            this.currentDevices = devices
            this.binding.pbAllDevices.visibility = View.GONE
            this.adapter?.updateData(devices)
        })

    }

    private fun setupRecyclerView(){
        this.adapter =  AllDevicesAdapter(this, arrayListOf())
        this.binding.rvAllDevices.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        this.binding.rvAllDevices.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_all_devices, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.id_sort ->{
                //Sort
                isFilterActive = !isFilterActive
                if(isFilterActive) {
                    sortList()
                }else{
                    this.adapter?.reloadSortArray(ArrayList(currentDevices))
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun sortList(){
        val sortedDevices = this.currentDevices?.sortedWith(compareByDescending { it.createdAt })
        this.adapter?.reloadSortArray(ArrayList(sortedDevices))
    }
}
