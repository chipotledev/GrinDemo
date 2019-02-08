package io.chipotie.grindemo.scanner.model

import android.bluetooth.BluetoothDevice

data class Device(val device: BluetoothDevice, val strength: Int)