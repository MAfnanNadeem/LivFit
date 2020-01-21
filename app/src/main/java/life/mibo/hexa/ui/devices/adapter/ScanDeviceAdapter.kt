package life.mibo.hexa.ui.devices.adapter

import android.annotation.SuppressLint
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
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import life.mibo.hardware.core.Logger
import life.mibo.hardware.models.Device
import life.mibo.hardware.models.DeviceTypes
import life.mibo.hexa.R
import java.util.*
import kotlin.math.log


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
        var battery: ImageView? = itemView.findViewById(R.id.iv_battery)
        var connect: ImageButton? = itemView.findViewById(R.id.button_connect)
        var disconnect: ImageButton? = itemView.findViewById(R.id.button_disconnect)
        var bluetooth: ImageView? = itemView.findViewById(R.id.iv_bl)
        var wifi: AppCompatImageView? = itemView.findViewById(R.id.iv_wifi)

        @SuppressLint("SetTextI18n")
        fun bind(item: Device?, callback: Listener?) {
            if (item == null)
                return
            val grey = ContextCompat.getColor(itemView?.context, R.color.grey_ddd)

            try {
                name?.text = item.name?.split("-")!![0]
            } catch (e: Exception) {
                name?.text = item.name
            }
            var txt = item.id
            if (txt.isNullOrBlank())
                txt = item.ip.hostAddress
            address?.text = item.serial + " : $txt"
            when {
                item.type == DeviceTypes.WIFI_STIMULATOR -> {
                    wifi?.setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN)
                    bluetooth?.setColorFilter(grey, PorterDuff.Mode.SRC_IN)
                }
                item.type == DeviceTypes.BLE_STIMULATOR -> {
                    wifi?.setColorFilter(grey, PorterDuff.Mode.SRC_IN)
                    bluetooth?.setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN)
                }
                item.type == DeviceTypes.SCALE -> {
                    wifi?.setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN)
                    bluetooth?.setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN)
                }
                else -> {
                    wifi?.setColorFilter(grey)
                    bluetooth?.setColorFilter(grey)
                }
            }
            if (item.isStarted) {
//                if (item.isWifi) {
//                    //wifi?.setColorFilter(Color.GREEN)
//                    //bluetooth?.setColorFilter(grey)
//                    disconnect?.setColorFilter(Color.RED)
//                    connect?.setColorFilter(Color.GREEN)
//                } else {
//                    //wifi?.setColorFilter(grey)
//                    //bluetooth?.setColorFilter(Color.BLUE)
//                    disconnect?.setColorFilter(Color.RED)
//                    connect?.setColorFilter(Color.GREEN)
//                }
            } else {
                //wifi?.setColorFilter(grey)
                //bluetooth?.setColorFilter(grey)
                disconnect?.setColorFilter(grey)
                connect?.setColorFilter(grey)
            }
            connect?.setOnClickListener {
                callback?.onConnectClicked(item)
            }
            disconnect?.setOnClickListener {
                item.statusConnected = 0
                disconnect?.setColorFilter(grey)
                callback?.onCancelClicked(item)
            }

            if (item.statusConnected == 1) {
                disconnect?.setColorFilter(Color.RED)
                connect?.setColorFilter(grey)
            } else {
                disconnect?.setColorFilter(grey)
                connect?.setColorFilter(Color.GREEN)
            }
            image?.background = null
            battery?.background = null
            if (item.type == DeviceTypes.RXL_WIFI) {
                image?.setBackgroundResource(R.drawable.ic_rxl_pods_icon)
            } else if (item.type == DeviceTypes.WIFI_STIMULATOR) {
                image?.setBackgroundResource(R.drawable.ic_dashboard_booster)
            }

            when {
                item.batteryLevel > 70 -> battery?.setImageResource(R.drawable.ic_battery_80)
                item.batteryLevel > 50 -> battery?.setImageResource(R.drawable.ic_battery_60)
                item.batteryLevel > 30 -> battery?.setImageResource(R.drawable.ic_battery_40)
                item.batteryLevel > 15 -> battery?.setImageResource(R.drawable.ic_battery_20)
               // else -> battery?.setImageResource(R.drawable.ic_battery_0)
            }

            Logger.e("DeviceStatusEvent batteryLevel ${item.batteryLevel}")
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

    fun updateDevice(device: Device?) {
        device?.let {
            list?.forEachIndexed { index, device ->
                if(device.uid == it.uid)
                {
                    list!![index] = it
                    notifyItemChanged(index, it)
                }
            }
        }
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