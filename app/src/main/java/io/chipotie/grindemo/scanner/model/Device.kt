package io.chipotie.grindemo.scanner.model

import android.bluetooth.BluetoothDevice
import com.google.gson.annotations.SerializedName

data class Device(
    @SerializedName("name")
    val name: String?,

    @SerializedName("address")
    val address: String,

    @SerializedName("strength")
    val strength: String,

    val saved : Boolean)