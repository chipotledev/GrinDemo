package io.chipotie.grindemo.adapter

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import io.chipotie.grindemo.R
import io.chipotie.grindemo.databinding.ItemFoundDeviceBinding
import io.chipotie.grindemo.scanner.model.Device
import io.chipotie.grindemo.util.BindViewHolder

class DiscoveredDevicesAdapter(val context: Context, val devices : ArrayList<Device>) : RecyclerView.Adapter<DiscoveredDevicesAdapter.DiscoveredDevicesViewHolder>() {

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

        if(device.device.name != null){
            binding.tvName.text = device.device.name
        }else{
            binding.tvName.text = device.device.address
        }

        binding.tvStrength.text = context.getString(R.string.bluetooth_strenght, device.strength)
    }


    class DiscoveredDevicesViewHolder(bindObject: ItemFoundDeviceBinding) : BindViewHolder<ItemFoundDeviceBinding>(bindObject)
}