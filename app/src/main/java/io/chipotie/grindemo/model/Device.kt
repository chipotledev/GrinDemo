package io.chipotie.grindemo.model

import android.bluetooth.BluetoothDevice
import com.google.gson.annotations.SerializedName
import java.util.*

data class Device(
    @SerializedName("name")
    val name: String?,

    @SerializedName("address")
    val address: String,

    @SerializedName("strength")
    val strength: String,

    var saved : Boolean,

    @SerializedName("created_at")
    var createdAt : Date?)