package life.mibo.hexa.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import life.mibo.hardware.CommunicationManager
import life.mibo.hardware.core.Logger
import life.mibo.hardware.models.Device
import life.mibo.hexa.R
import life.mibo.hexa.adapters.ScanDeviceAdapter

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
        val textView: TextView = root.findViewById(R.id.text_home)
        recyclerView = root.findViewById(R.id.recyclerView)
        homeViewModel.text.observe(this, Observer {
            textView.text = it
        })
        setRecycler()
        getScanned()
        return root
    }

    val devicesList = ArrayList<ScanDeviceAdapter.ScanItem>();
    fun setRecycler() {
        if (recyclerView == null)
            return
        devicesList.clear()
        for (i in 1..50
        ) {
            devicesList.add(ScanDeviceAdapter.ScanItem("test $i", "$i"))
        }
        val adapter = ScanDeviceAdapter(devicesList)
        val manager = LinearLayoutManager(this@HomeFragment.activity)
        recyclerView?.layoutManager = manager
        recyclerView?.adapter = adapter
    }

    val manager = CommunicationManager.getInstance()

    fun getScanned() {
        log("getScanning started")
        val manager =
            CommunicationManager.getInstance().setListener(object : CommunicationManager.Listener {
                override fun NewConnectionStatus(getname: String?) {
                    log("NewConnectionStatus $getname")
                }

                override fun NewAlarmEvent() {
                    log("NewAlarmEvent ")
                }

                override fun NewDeviceDiscoveredEvent(s: String?) {
                    log("NewDeviceDiscoveredEvent $s")
                }

                override fun HrEvent(hr: Int, uid: String?) {
                    log("HrEvent $hr : $uid")
                }

                override fun DeviceStatusEvent(uid: String?) {
                    log("DeviceStatusEvent $uid")
                }

                override fun ChangeColorEvent(d: Device?, uid: String?) {
                    log("DeviceStatusEvent $d : $uid")
                }

                override fun GetMainLevelEvent(mainLevel: Int, uid: String?) {
                    log("GetMainLevelEvent $mainLevel : $uid")
                }

                override fun GetLevelsEvent(uid: String?) {
                    log("GetLevelsEvent  $uid")
                }

                override fun ProgramStatusEvent(
                    time: Int,
                    action: Int,
                    pause: Int,
                    currentBlock: Int,
                    currentProgram: Int,
                    uid: String?
                ) {

                }

                override fun DevicePlayPauseEvent(uid: String?) {
                    log("GetLevelsEvent  $uid")
                }

            })
        manager.startDiscoveryServers(this@HomeFragment.activity)
        log("getScanning finished")
    }

    fun log(msg: String) {
        Logger.e("HomeFragment : $msg")
    }
}