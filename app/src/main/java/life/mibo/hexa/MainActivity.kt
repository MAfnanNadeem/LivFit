package life.mibo.hexa

import android.os.Bundle
import android.view.Gravity
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import life.mibo.hardware.CommunicationManager
import life.mibo.hardware.core.Logger
import life.mibo.hardware.models.Device
import java.net.InetAddress

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val drawer: NavigationView = findViewById(R.id.nav_view)

        val bottomNavView: BottomNavigationView = findViewById(R.id.bottom_nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        bottomNavView.setupWithNavController(navController)
//        val drawerConfig = AppBarConfiguration(
//            setOf(
//                R.id.nav_home, R.id.nav_test1, R.id.nav_test2,
//                R.id.nav_test3, R.id.nav_share, R.id.nav_send
//            ), drawerLayout
//        )
        drawer.setNavigationItemSelectedListener {
            Snackbar.make(drawer, "item clicked " + it.itemId, Snackbar.LENGTH_LONG).show()
            when (it.itemId) {
                R.id.nav_test1 -> {
                    navController.navigate(R.id.navigation_channels)
                }
                R.id.nav_test2 -> {

                }

            }
            drawerLayout.closeDrawer(Gravity.START)
            return@setNavigationItemSelectedListener true;
        }
//        drawer.setupWithNavController(navController)

        //getScanned()
    }

    fun getScanned() {
        log("getScanning started")
        val manager =
            CommunicationManager.getInstance(object : CommunicationManager.Listener {
                override fun broadcastReceived(msg: ByteArray?, ip: InetAddress?) {
                    log("broadcastReceived " + msg)
                }

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
        manager.startDiscoveryServers(this)

        log("getScanning finished")
    }

    fun log(msg: String) {
        Logger.e("MainActivity : $msg")
    }
}
