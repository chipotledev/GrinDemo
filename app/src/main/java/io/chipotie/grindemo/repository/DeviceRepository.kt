package io.chipotie.grindemo.repository

import android.content.Context
import io.chipotie.grindemo.net.GrinApiSingleton
import io.chipotie.grindemo.model.Device
import io.chipotie.grindemo.util.SingletonHolder
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import com.google.gson.Gson
import okhttp3.MediaType

class DeviceRepository private constructor(private val context: Context){

    companion object : SingletonHolder<DeviceRepository, Context>(::DeviceRepository)

    fun uploadDeviceData(device: Device, callback: UploadCallback){

        val requestString = Gson().toJson(device)
        val requestBody = RequestBody.create(MediaType.parse("application/json"), requestString)

        GrinApiSingleton.getInstance(context).grinApiInterface.add(requestBody).enqueue(object: retrofit2.Callback<Device>{

            override fun onResponse(call: Call<Device>, response: Response<Device>) {
                if (response.isSuccessful) {
                    callback.uploadSuccess(device.address)
                    return
                }

                callback.uploadFailed()
            }

            override fun onFailure(call: Call<Device>, t: Throwable) {
                callback.uploadFailed()
            }
        })
    }

    fun allDevices(callback: RetrieveDevicesCallback){
        GrinApiSingleton.getInstance(context).grinApiInterface.devices().enqueue(object: retrofit2.Callback<ArrayList<Device>>{

            override fun onResponse(call: Call<ArrayList<Device>>, response: Response<ArrayList<Device>>) {
                if (response.isSuccessful){
                    response.body()?.let { callback.downloadSuccess(it) }
                    return
                }

                callback.downloadFailed()
            }
            override fun onFailure(call: Call<ArrayList<Device>>, t: Throwable) {
                callback.downloadFailed()
            }
        })
    }

    interface UploadCallback{
        fun uploadSuccess(address: String)
        fun uploadFailed()
    }

    interface RetrieveDevicesCallback{
        fun downloadSuccess(devices: ArrayList<Device>)
        fun downloadFailed()
    }
}