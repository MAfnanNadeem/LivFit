package life.mibo.hexa.ui.devices.adapter

import android.bluetooth.BluetoothDevice
import android.graphics.Color
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import life.mibo.hardware.core.Logger
import life.mibo.hardware.models.Device
import life.mibo.hardware.models.DeviceTypes
import life.mibo.hexa.R
import java.util.*


class ScanDeviceAdapter(var list: ArrayList<Device>?, val type: Int = 0) :
    RecyclerView.Adapter<ScanDeviceAdapter.Holder>() {
    private var listener: Listener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.list_item_devices,
                parent,
                false
            )
        )
    }

    fun setListener(l: Listener) {
        this.listener = l
    }

    override fun getItemCount(): Int {
        if (list != null)
            return list?.size!!
        return 0
    }

    private fun getItem(position: Int): Device? {
        return list?.get(position)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        //val item = getItem(position)
        holder.bind(getItem(position), listener)

    }

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name: TextView? = itemView.findViewById(R.id.tv_deviceName)
        var address: TextView? = itemView.findViewById(R.id.tv_deviceMac)
        var image: ImageView? = itemView.findViewById(R.id.iv_device)
        var connect: ImageButton? = itemView.findViewById(R.id.button_connect)
        var disconnect: ImageButton? = itemView.findViewById(R.id.button_disconnect)
        var bluetooth: ImageView? = itemView.findViewById(R.id.iv_bl)
        var wifi: AppCompatImageView? = itemView.findViewById(R.id.iv_wifi)

        fun bind(item: Device?, callback: Listener?) {
            if (item == null)
                return
            name?.text = item.name
            address?.text = item.serial + " : " + item.id
            when {
                item.type == DeviceTypes.WIFI_STIMULATOR -> {
                    wifi?.setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN)
                    bluetooth?.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN)
                }
                item.type == DeviceTypes.BLE_STIMULATOR -> {
                    wifi?.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN)
                    bluetooth?.setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN)
                }
                item.type == DeviceTypes.SCALE -> {
                    wifi?.setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN)
                    bluetooth?.setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN)
                }
                else -> {
                    wifi?.setColorFilter(Color.GRAY)
                    bluetooth?.setColorFilter(Color.GRAY)
                }
            }
            if (item.isStarted) {
//                if (item.isWifi) {
//                    //wifi?.setColorFilter(Color.GREEN)
//                    //bluetooth?.setColorFilter(Color.GRAY)
//                    disconnect?.setColorFilter(Color.RED)
//                    connect?.setColorFilter(Color.GREEN)
//                } else {
//                    //wifi?.setColorFilter(Color.GRAY)
//                    //bluetooth?.setColorFilter(Color.BLUE)
//                    disconnect?.setColorFilter(Color.RED)
//                    connect?.setColorFilter(Color.GREEN)
//                }
            } else {
                //wifi?.setColorFilter(Color.GRAY)
                //bluetooth?.setColorFilter(Color.GRAY)
                disconnect?.setColorFilter(Color.GRAY)
                connect?.setColorFilter(Color.GRAY)
            }
            connect?.setOnClickListener {
                callback?.onConnectClicked(item)
            }
            disconnect?.setOnClickListener {
                item.statusConnected = 0
                disconnect?.setColorFilter(Color.GRAY)
                callback?.onCancelClicked(item)
            }

            if (item.statusConnected == 1) {
                disconnect?.setColorFilter(Color.RED)
            } else {
                disconnect?.setColorFilter(Color.GRAY)
            }
        }
    }

    fun getRandomColor(): Int {
        val rnd = Random()
        return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
    }

    fun addDevice(ble: BluetoothDevice) {
        Logger.e("ScanDevice addDevice ")
        var contain = false
        for (l in list!!) {
            if (l.serial == ble.address) {
                contain = true
                break;
            }
        }
        if (!contain) {
            list!!.add(Device(ble.name, ble.address, ble.address, DeviceTypes.BLE_STIMULATOR))
            notifyItemInserted(list!!.size)
        }
    }

    fun addDevice(item: ScanItem) {
//        Logger.e("ScanDevice addDevice ")
//        var contain = false
//        for (l in list!!) {
//            if (l.address == item.address) {
//                contain = true
//                break;
//            }
//        }
//        if (!contain) {
//            list!!.add(item)
//            notifyItemInserted(list!!.size)
//        }
    }

    fun addDevice(item: Device) {
        Logger.e("ScanDevice addDevice device")
        for (l in list!!) {
            if (l.serial == item.serial) {
                return;
            }
        }
        list!!.add(item)
        notifyItemInserted(list!!.size)
    }

    fun addDevices(l: ArrayList<Device>) {
        Logger.e("ScanDevice addDevices list $l")
        this.list?.addAll(l)
        notifyDataSetChanged()
    }

    data class ScanItem(
        val name: String?,
        val address: String? = "",
        var type: Int = 0,
        var image: Int = 0
    ) {
        var isBluetooth = false
        var isWifi = false
        var isConnected = false
        var device: Device? = null

        constructor(d: Device) : this(d.name, d.serial, d.type.ordinal) {
            this.device = d
        }
    }

    interface Listener {
        fun onConnectClicked(device: Device?)
        fun onCancelClicked(device: Device?)
    }
}