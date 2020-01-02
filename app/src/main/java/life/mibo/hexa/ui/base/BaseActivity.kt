package life.mibo.hexa.ui.base

import androidx.appcompat.app.AppCompatActivity
import life.mibo.hardware.core.Logger
import life.mibo.hexa.ui.dialog.MyDialog
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.NoSubscriberEvent
import org.greenrobot.eventbus.Subscribe

abstract class BaseActivity : AppCompatActivity() {

    fun log(msg: String) {
        Logger.e("${this.javaClass} : $msg")
    }

    fun logw(msg: String) {
        Logger.i("${this.javaClass}", msg)
    }

    @Subscribe
    public fun SubscribeTest(event: NoSubscriberEvent?){
        log("SubscribeTest $event")
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
}