package life.mibo.hexa

import android.app.Application
import android.content.Context
import coil.util.CoilLogger
import com.jakewharton.threetenabp.AndroidThreeTen
import life.mibo.hardware.MIBO

class MiboApplication : Application() {

    companion object {
        var context : Context? = null
    }

    override fun onCreate() {
        super.onCreate()
        MIBO.init(this)
        context = this
        CoilLogger.setEnabled(true)
        AndroidThreeTen.init(this)

    }
}