package io.chipotie.grindemo.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.chipotie.grindemo.R
import io.chipotie.grindemo.databinding.ItemSaveDeviceBinding
import io.chipotie.grindemo.model.Device
import io.chipotie.grindemo.util.BindViewHolder
import io.chipotie.grindemo.util.DateFormatUtils
import io.chipotie.grindemo.util.diff.DevicesDiffCallback

class AllDevicesAdapter(private val context: Context, private val devices : ArrayList<Device>) : RecyclerView.Adapter<AllDevicesAdapter.AllDevicesViewHolder>() {

    private var originalDevices = ArrayList<Device>()

    init {
        this.originalDevices.addAll(devices)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllDevicesViewHolder {

        val layoutInflater : LayoutInflater = LayoutInflater.from(context)
        val binding : ItemSaveDeviceBinding = DataBindingUtil.inflate(layoutInflater, R.layout.item_save_device,parent, false)
        return AllDevicesViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return devices.size
    }

    override fun onBindViewHolder(holder: AllDevicesViewHolder, position: Int) {
        //Get the device
        val device = devices[position]

        val binding = holder.bindObject
        binding.adapter = this
        binding.item = device

        if(device.name != null){
            binding.tvName.text = device.name
        }else{
            binding.tvName.text = device.address
        }

        binding.tvStrength.text = device.strength
        binding.tvDate.text = device.createdAt?.let { DateFormatUtils.formateDate(it) }

    }

    fun updateData(newList: ArrayList<Device>){
        val diffResult = DiffUtil.calculateDiff(DevicesDiffCallback(originalDevices, newList))
        this.devices.clear()
        this.devices.addAll(newList)
        diffResult.dispatchUpdatesTo(this)
    }

    fun reloadSortArray(newList: ArrayList<Device>){
        this.devices.clear()
        this.devices.addAll(newList)
        notifyDataSetChanged()
    }

    class AllDevicesViewHolder(bindObject: ItemSaveDeviceBinding) : BindViewHolder<ItemSaveDeviceBinding>(bindObject)

}