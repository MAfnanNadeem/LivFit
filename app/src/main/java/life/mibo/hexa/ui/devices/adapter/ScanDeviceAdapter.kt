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


class ScanDeviceAdapter(var list: ArrayList<Device>?, val type: Int = 0) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val ITEM = 0
    private val HEADER = 1
    private var listener: Listener? = null

    companion object {
        var RxlCount = 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == HEADER)
            return Header(
            LayoutInflater.from(parent.context).inflate(
                R.layout.list_item_devices_header,
                parent,
                false
            )
        )
        return ScanItem(
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

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        //val item = getItem(position)
        if (holder is ScanItem)
            holder.bind(getItem(position), listener)
        else if (holder is Header)
            holder.bind(getItem(position))

    }

    class Header(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var title: TextView? = itemView.findViewById(R.id.title)
        fun bind(item: Device?) {
            if (item == null)
                return

            if (item.statusConnected == 1) {
                title?.text = itemView.context.getString(R.string.connected_devices)
            } else {
                title?.text = itemView.context.getString(R.string.available_devices)
            }
        }
    }

    fun resetRxlCount() {
        RxlCount = 0
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

    fun move(from: Int, to: Int) {
        if (list?.size!! > 1 && from != to) {
            Collections.swap(list, from, to)
            notifyItemMoved(from, to)

        }
    }


    fun remove(uid: String) {
        Logger.e("ScanDevice remove device uid")
        list?.forEachIndexed { i, d ->
            if (d.uid == uid) {
                list!!.removeAt(i)
                notifyItemRemoved(i)
                return
            }
        }
    }

    fun remove(item: Device) {
        Logger.e("ScanDevice remove device")
        list?.forEachIndexed { i, d ->
            if (d.uid == item.uid) {
                list!!.removeAt(i)
                notifyItemRemoved(i)
                return
            }
        }
    }

    fun addConnectedDevice(item: Device) {
        var d = item
        for (l in list!!) {
            if (l.uid == item.uid) {
                // l = item
                d.update(item)
            }
        }
        list!!.add(d)
        notifyDataSetChanged()
    }

    fun addDevice(devices: ArrayList<Device>, rxl: Boolean) {
        list!!.clear()
        devices.forEachIndexed { index, device ->
            if (rxl) {
                device.id = "$index"
                device.name = "RXL ${index.plus(1)}"
            }
            list!!.add(device)
        }
    }

    fun addDevice(item: Device, rxl: Boolean) {
        for (l in list!!) {
            if (l.uid == item.uid) {
                l.connectionType = item.connectionType
                l.type = item.type
                return
            }
        }
        if (rxl) {
            item.id = "${list!!.size}"
            item.name = "RXL ${list!!.size.plus(1)}"
        }
        list!!.add(item)
        notifyItemInserted(list!!.size)
    }

    fun addDevice(item: Device) {
        Logger.e("ScanDevice addDevice device")
        for (l in list!!) {
            if (l.uid == item.uid) {
                Logger.e("ScanDevice addDevice already added")
                return
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
                if (device.uid == it.uid) {
                    list!![index] = it
                    notifyItemChanged(index, it)
                }
            }
        }
    }

    class ScanItem(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var view: View? = itemView.findViewById(R.id.itemView)
        var name: TextView? = itemView.findViewById(R.id.tv_deviceName)
        var address: TextView? = itemView.findViewById(R.id.tv_deviceMac)
        var image: ImageView? = itemView.findViewById(R.id.iv_device)
        var battery: TextView? = itemView.findViewById(R.id.tv_battery)
        var connect: ImageButton? = itemView.findViewById(R.id.button_connect)
        var disconnect: ImageButton? = itemView.findViewById(R.id.button_disconnect)
        var bluetooth: ImageView? = itemView.findViewById(R.id.iv_bl)
        var wifi: AppCompatImageView? = itemView.findViewById(R.id.iv_wifi)

        private fun getSignal(rssi: Int): Int {
            val MIN_RSSI = -100
            val MAX_RSSI = -55
            val numLevels = 4
            return when {
                rssi <= MIN_RSSI -> 0
                rssi >= MAX_RSSI -> numLevels - 1
                else -> {
                    val inputRange = (MAX_RSSI - MIN_RSSI).toFloat()
                    val outputRange = (numLevels - 1).toFloat()
                    ((rssi - MIN_RSSI).toFloat() * outputRange / inputRange).toInt()
                }
            }
        }

        @SuppressLint("SetTextI18n")
        fun bind(item: Device?, callback: Listener?) {
            if (item == null)
                return
            val grey = ContextCompat.getColor(itemView?.context, R.color.grey_ddd)
            battery?.text = ""
            if (item.isPod) {
                name?.text = item.name
                address?.text = item.serial
                battery?.text = ""
            } else {
                name?.text = item.name?.split("-")!![0]
                var txt = item.id
                if (txt.isNullOrBlank())
                    txt = item.ip.hostAddress
                address?.text = item.serial + " : $txt"
            }

            val isWifi =
                item.type == DeviceTypes.WIFI_STIMULATOR || item.type == DeviceTypes.RXL_WIFI
            when {
                isWifi -> {
                    wifi?.setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN)
                    bluetooth?.setColorFilter(grey, PorterDuff.Mode.SRC_IN)
                }
                item.type == DeviceTypes.BLE_STIMULATOR || item.type == DeviceTypes.RXL_BLE -> {
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

            image?.background = null
            if (item.type == DeviceTypes.RXL_WIFI || item.type == DeviceTypes.RXL_BLE) {
                image?.setImageResource(R.drawable.ic_rxl_pods_icon_200)
                Logger.e(
                    "PorterDuff.Mode.SRC_OUT $adapterPosition " + life.mibo.hexa.utils.Utils.getColorAt(
                        adapterPosition
                    )
                )
            } else if (item.type == DeviceTypes.WIFI_STIMULATOR || item.type == DeviceTypes.BLE_STIMULATOR) {
                image?.setBackgroundResource(R.drawable.ic_dashboard_booster)
            } else if (item.type == DeviceTypes.RXT_WIFI) {
                image?.setBackgroundResource(R.drawable.ic_logo_floor_wall)
            }

            view?.background = null
            if (item.statusConnected == 1) {
                disconnect?.setColorFilter(Color.RED)
                connect?.setColorFilter(grey)
                view?.background = ContextCompat.getDrawable(
                    itemView.context,
                    R.drawable.device_list_item_bg_selected
                )

                if (item.type == DeviceTypes.RXL_WIFI || item.type == DeviceTypes.RXL_BLE) {
                    image?.setImageDrawable(
                        life.mibo.hexa.utils.Utils.getColorFilterDrawable(
                            image?.context,
                            R.drawable.ic_rxl_pods_icon_200,
                            life.mibo.hexa.utils.Utils.getColorAt(item.id)
                        )
                    )
//                    image?.drawable?.setColorFilter(
//                        life.mibo.hexa.utils.Utils.getColorAt(item.id), PorterDuff.Mode.SRC_IN
//                    )
                }

                when {
                    item.batteryLevel > 20 -> {
                        battery?.visibility = View.VISIBLE
                        battery?.text = "${item.batteryLevel}"
                        battery?.setBackgroundResource(R.drawable.ic_battery_empty)
                        battery?.background?.setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_ATOP)
                    }
                    item.batteryLevel > 0 -> {
                        battery?.visibility = View.VISIBLE
                        battery?.setBackgroundResource(R.drawable.ic_battery_empty)
                        battery?.text = "${item.batteryLevel}"
                        battery?.background?.setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP)
                    }
                    else -> {
                        battery?.visibility = View.INVISIBLE
                    }
                }

//                when {
//                    item.batteryLevel > 70 -> battery?.setImageResource(R.drawable.ic_battery_80)
//                    item.batteryLevel > 50 -> battery?.setImageResource(R.drawable.ic_battery_60)
//                    item.batteryLevel > 30 -> battery?.setImageResource(R.drawable.ic_battery_40)
//                    item.batteryLevel > 15 -> battery?.setImageResource(R.drawable.ic_battery_20)
//                    // else -> battery?.setImageResource(R.drawable.ic_battery_0)
//                }
            } else {
                if (item.type == DeviceTypes.RXL_WIFI || item.type == DeviceTypes.RXL_BLE) {
                    image?.drawable?.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN)
                }

                disconnect?.setColorFilter(grey)
                connect?.setColorFilter(Color.GREEN)
                view?.background =
                    ContextCompat.getDrawable(itemView.context, R.drawable.device_list_item_bg)
                // battery?.background = null
                battery?.visibility = View.INVISIBLE

            }

            if (isWifi) {
                when (getSignal(item.signalLevel)) {
                    3 -> wifi?.setImageResource(R.drawable.ic_wifi_80)
                    2 -> wifi?.setImageResource(R.drawable.ic_wifi_60)
                    1 -> wifi?.setImageResource(R.drawable.ic_wifi_20)
                    0 -> wifi?.setImageResource(R.drawable.ic_wifi_20)
                    // else -> battery?.setImageResource(R.drawable.ic_battery_0)
                }
            }

            view?.setOnClickListener {
                //callback?.onClicked(item)
                if (item.statusConnected == 1) {
                    callback?.onClicked(item)
                }

            }
            connect?.setOnClickListener {
//                if (item.statusConnected == 1) {
//                    return@setOnClickListener
//                }
                callback?.onConnectClicked(item)
            }
            disconnect?.setOnClickListener {
                // if (item.statusConnected == 0) {
                // return@setOnClickListener
                // }
                item.statusConnected = 0
                callback?.onCancelClicked(item)
                disconnect?.setColorFilter(grey)
            }

//            Logger.e("DeviceStatusEvent batteryLevel ${item.batteryLevel} - signalLevel ${item.signalLevel} ")
//            Logger.e(
//                "DeviceStatusEvent batteryLevel levels " + WifiManager.calculateSignalLevel(
//                    item.signalLevel,
//                    4
//                )
//            )

        }
    }

    interface Listener {
        fun onConnectClicked(device: Device?)
        fun onCancelClicked(device: Device?)
        fun onClicked(device: Device?)
    }
}