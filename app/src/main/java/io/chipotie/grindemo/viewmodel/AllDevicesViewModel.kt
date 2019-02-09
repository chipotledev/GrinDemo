package io.chipotie.grindemo.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import io.chipotie.grindemo.model.Device
import io.chipotie.grindemo.repository.DeviceRepository

class AllDevicesViewModel(application: Application) : AndroidViewModel(application), DeviceRepository.RetrieveDevicesCallback {

    fun retrieveAllDevices(){
        DeviceRepository.getInstance(getApplication()).allDevices(this)
    }

    var devices : MutableLiveData<ArrayList<Device>> = MutableLiveData()
    var errorDevices : MutableLiveData<Boolean> = MutableLiveData()

    override fun downloadFailed() {
        errorDevices.value = true
    }

    override fun downloadSuccess(devices: ArrayList<Device>) {
        this.devices.value = devices
    }
}