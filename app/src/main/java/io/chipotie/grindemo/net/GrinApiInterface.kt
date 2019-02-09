package io.chipotie.grindemo.net

import io.chipotie.grindemo.model.Device
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface GrinApiInterface{

    @GET("/devices")
    fun devices() : Call<ArrayList<Device>>

    @POST("/add")
    fun add(@Body requestBody: RequestBody) : Call<Device>
}