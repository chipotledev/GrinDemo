Grin Demo
========

Demo for hiring process of Grin

Process
--------

1. Analysis of required arquitecture.
2. Research about the best arquitecture components to solve the problem.
3. Implementation of scanner to discover bluetooth devices using LiveData and BroadcastReceiver.
    -Creation of the Model
    -Creation of the ViewModel
    -Creation of the Repository
4. Implementation of net layer to communicate the application with the web services.
5. Connect the action of the upload button with the web service.
6. Creation of the screen to see al the saved devices.
7. Implementation of internet validation and the state changes of bluetooth.

Architecture
--------

![alt text](https://github.com/chipotledev/GrinDemo/blob/master/Untitled%20Diagram.png)

The arquitecture is MVVM. The Ui communicates the view with the ViewModel. The ViewModel couple the discovery of devices from the BroadcastReceiver wit the LiveData Observer and returns to the UI. When the user triggers the upload, the ViewModel call for the repository to upload the device data.

In the following lines I show how I coupled the BroadcastReceiver with the LiveData:

```kotlin
    var devices : MutableLiveData<ArrayList<Device>> = object : MutableLiveData<ArrayList<Device>>(){
        override fun onActive() {
            context.registerReceiver(receiver, IntentFilter(BluetoothDevice.ACTION_FOUND))
            BluetoothAdapter.getDefaultAdapter()?.startDiscovery()
        }

        override fun onInactive() {
            context.unregisterReceiver(receiver)
            BluetoothAdapter.getDefaultAdapter()?.cancelDiscovery()
        }
    }
```

```kotlin
    private var receiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action
            if (BluetoothDevice.ACTION_FOUND == action) {
                val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                val strength : Int = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE).toInt()

                if(device != null) {

                    //If the bluetooth has no name
                    val name = if(device.name == null) "Unknown" else device.name

                    val deviceData = Device(
                        name,
                        device.address,
                        context!!.getString(R.string.bluetooth_strenght, strength),
                        false,
                        null
                    )
                    foundDevices[device.address] = deviceData
                    devices.value = getList(foundDevices)
                }
            }
        }
    }
```

Architecture Components
--------

* ViewModel
  - LiveData
* Repository
* View
  - DataBinding
  
Language
--------

Kotlin
