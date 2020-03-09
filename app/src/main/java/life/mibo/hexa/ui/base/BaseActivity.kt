package life.mibo.hexa.ui.base

import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import life.mibo.hardware.core.Logger
import life.mibo.hexa.R
import life.mibo.hexa.ui.dialog.MyDialog
import life.mibo.hexa.ui.main.MainActivity
import life.mibo.hexa.ui.main.MiboApplication
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.NoSubscriberEvent
import org.greenrobot.eventbus.Subscribe
import java.util.*


abstract class BaseActivity : AppCompatActivity() {
    var DEBUG = MiboApplication.DEBUG

    fun log(msg: String) {
        Logger.e("${this.javaClass} : $msg")
    }

    fun logw(msg: String) {
        Logger.i("${this.javaClass}", msg)
    }

    @Subscribe
    public fun SubscribeTest(event: NoSubscriberEvent?){
        log("SubscribeTest bus: ${event?.eventBus} :: object: ${event?.originalEvent}")
    }

    public override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    public override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    var mDialog: MyDialog? = null

    fun getDialog(): MyDialog? {
        if (mDialog == null)
            mDialog = MyDialog.get(this)
        return mDialog
    }

    fun dismisDialog() {
        mDialog?.dismiss()
    }


    // Sample
    fun shortcutAction() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            val shortcutManager = this.getSystemService(ShortcutManager::class.java)
            val webShortcut = ShortcutInfo.Builder(this, "shortcut_web")
                .setShortLabel("catinean.com")
                .setLongLabel("Open catinean.com web site")
                .setIcon(Icon.createWithResource(this, R.drawable.ic_rxl_pods_icon))
                .setIntent(Intent(Intent.ACTION_VIEW, Uri.parse("https://catinean.com")))
                .build()

            shortcutManager?.dynamicShortcuts = Collections.singletonList(webShortcut)
        }
    }

    fun shortcutAction(type: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            val shortcutManager = this.getSystemService(ShortcutManager::class.java)
            if (type == 1) {
                val dynamicShortcut = ShortcutInfo.Builder(this, "boost_shortcut")
                    .setShortLabel("MyoBoost")
                    .setLongLabel("Wear your suit and start exercise")
                    .setIcon(Icon.createWithResource(this, R.drawable.ic_rxl_pods_icon))
                    .setIntents(
                        arrayOf(
                            Intent(Intent.ACTION_MAIN, Uri.EMPTY, this, MainActivity::class.java)
                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        )
                    )
                    .build()
                shortcutManager?.dynamicShortcuts = listOf(dynamicShortcut, dynamicShortcut)
            }
            if (type == 2) {
                val dynamicShortcut = ShortcutInfo.Builder(this, "boost_shortcut")
                    .setShortLabel("Reaction Lights")
                    .setLongLabel("Wear your suit and start exercise")
                    .setIcon(Icon.createWithResource(this, R.drawable.ic_rxl_pods_icon))
                    .setIntents(
                        arrayOf(
                            Intent(Intent.ACTION_MAIN, Uri.EMPTY, this, MainActivity::class.java)
                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        )
                    )
                    .build()
                shortcutManager?.dynamicShortcuts = listOf(dynamicShortcut, dynamicShortcut)
            }


        }
    }
}