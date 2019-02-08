package io.chipotie.grindemo.util.diff

import androidx.recyclerview.widget.DiffUtil
import io.chipotie.grindemo.scanner.model.Device

class DiscoverDevicesDiffCallback(val oldList: ArrayList<Device>, val newList: ArrayList<Device>) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].device.address == newList[newItemPosition].device.address
    }

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

}