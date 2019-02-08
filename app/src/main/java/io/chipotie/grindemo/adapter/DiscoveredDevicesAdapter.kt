package io.chipotie.grindemo.adapter

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.chipotie.grindemo.R
import io.chipotie.grindemo.databinding.ItemFoundDeviceBinding
import io.chipotie.grindemo.scanner.model.Device
import io.chipotie.grindemo.util.BindViewHolder
import io.chipotie.grindemo.util.diff.DiscoverDevicesDiffCallback

class DiscoveredDevicesAdapter(private val context: Context, private val devices : ArrayList<Device>) : RecyclerView.Adapter<DiscoveredDevicesAdapter.DiscoveredDevicesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiscoveredDevicesViewHolder {

        val layoutInflater : LayoutInflater = LayoutInflater.from(context)
        val binding : ItemFoundDeviceBinding = DataBindingUtil.inflate(layoutInflater, R.layout.item_found_device,parent, false)
        return DiscoveredDevicesViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return devices.size
    }

    override fun onBindViewHolder(holder: DiscoveredDevicesViewHolder, position: Int) {
        //Get the device
        val device = devices[position]

        val binding = holder.bindObject

        if(device.name != null){
            binding.tvName.text = device.name
        }else{
            binding.tvName.text = device.address
        }

        binding.tvStrength.text = device.strength
    }

    fun updateData(newList: ArrayList<Device>){
        val diffResult = DiffUtil.calculateDiff(DiscoverDevicesDiffCallback(devices, newList))
        this.devices.clear()
        this.devices.addAll(newList)
        diffResult.dispatchUpdatesTo(this)
    }


    class DiscoveredDevicesViewHolder(bindObject: ItemFoundDeviceBinding) : BindViewHolder<ItemFoundDeviceBinding>(bindObject)
}