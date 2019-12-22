package life.mibo.hexa.ui.home

import android.bluetooth.BluetoothDevice
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import c.tlgbltcn.library.BluetoothHelper
import c.tlgbltcn.library.BluetoothHelperListener
import life.mibo.hardware.CommunicationManager
import life.mibo.hardware.core.Logger
import life.mibo.hardware.models.Device
import life.mibo.hexa.R
import life.mibo.hexa.adapters.ScanDeviceAdapter
import life.mibo.hexa.utils.Toasty
import java.net.InetAddress

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    var recyclerView: RecyclerView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        // val textView: TextView = root.findViewById(R.id.text_home)
        recyclerView = root.findViewById(R.id.recyclerView)
        homeViewModel.text.observe(this, Observer {
            //  textView.text = it
        })
        setRecycler()
        //       getBlDevices()
        val scan: View = root.findViewById(R.id.scan);
        scan?.setOnClickListener {
            if (bluetoothHelper != null) {
                if (bluetoothHelper?.isBluetoothEnabled()!!)
                    bluetoothHelper?.enableBluetooth()
                bluetoothHelper?.startDiscovery()
            }
            communicationManager?.startScanning(this@HomeFragment.activity)
            Toasty.info(this@HomeFragment.context!!, "Bluetooth scanning").show()

        }
        return root
    }

    var bluetoothHelper: BluetoothHelper? = null
    fun getBlDevices() {
        bluetoothHelper =
            BluetoothHelper(this@HomeFragment.context!!, object : BluetoothHelperListener {
                override fun onStartDiscovery() {
                    log("bluetoothHelper onStartDiscovery")
                }

                override fun onFinishDiscovery() {
                    log("bluetoothHelper onFinishDiscovery")

                }

                override fun onEnabledBluetooth() {
                    log("bluetoothHelper onEnabledBluetooth")

                }

                override fun onDisabledBluetooh() {
                    log("bluetoothHelper onDisabledBluetooh")

                }

                override fun onDeviceFound(device: BluetoothDevice?) {
                    log("bluetoothHelper onDeviceFound " + device)
                    if (device != null) {
                        devicesList.add(ScanDeviceAdapter.ScanItem(device.name, device.address))
                        adapter?.notifyDataSetChanged()
                    }
                }

            }).setPermissionRequired(true).create()
    }

    val devicesList = ArrayList<ScanDeviceAdapter.ScanItem>()
    var adapter: ScanDeviceAdapter? = null
    fun setRecycler() {
        if (recyclerView == null)
            return
        devicesList.clear()
        for (i in 1..2
        ) {
            devicesList.add(ScanDeviceAdapter.ScanItem("test $i", "$i"))
        }
        adapter = ScanDeviceAdapter(devicesList)
        val manager = LinearLayoutManager(this@HomeFragment.activity)
        recyclerView?.layoutManager = manager
        recyclerView?.adapter = adapter
    }

    var communicationManager: CommunicationManager? = null


    fun log(msg: String) {
        Logger.e("HomeFragment : $msg")
    }

    override fun onResume() {
        super.onResume()
        bluetoothHelper?.registerBluetoothStateChanged()
    }


    override fun onStop() {
        super.onStop()
        bluetoothHelper?.unregisterBluetoothStateChanged()
    }
}