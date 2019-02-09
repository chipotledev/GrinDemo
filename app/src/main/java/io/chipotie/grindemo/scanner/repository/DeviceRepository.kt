package io.chipotie.grindemo.scanner.repository

import android.content.Context
import android.util.Log
import io.chipotie.grindemo.net.GrinApiSingleton
import io.chipotie.grindemo.scanner.model.Device
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

    interface UploadCallback{
        fun uploadSuccess(address: String)
        fun uploadFailed()
    }
}