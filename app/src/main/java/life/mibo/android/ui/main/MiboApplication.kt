/*
 *  Created by Sumeet Kumar on 1/14/20 4:45 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/13/20 8:49 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.main

//import com.facebook.drawee.backends.pipeline.Fresco
///import leakcanary.AppWatcher
import android.app.Application
import android.content.Context
import com.danikula.videocache.HttpProxyCacheServer
import com.jakewharton.threetenabp.AndroidThreeTen
import life.mibo.android.ui.fit.fitbit.Fitbit
import life.mibo.hardware.MIBO
import life.mibo.hardware.core.Logger
import life.mibo.hardware.encryption.MCrypt
import life.mibo.hardware.fastble.BleManager
import life.mibo.hardware.fastble.scan.BleScanRuleConfig
import okhttp3.Cache


class MiboApplication : Application() {

    companion object {
        var context: Context? = null

        val DEBUG = life.mibo.android.BuildConfig.DEBUG

        //val DEBUG = false
        val DEV_SERVER = true

        //val DEBUG = false
        val RELEASE = false
        val TEST = false
        val SCAN_TIME: Long = 15000L

        fun isRelease() = true

        private var proxy: HttpProxyCacheServer? = null

        // todO
        // check fitbit key/email owner for final release


        fun getProxy(context: Context?): HttpProxyCacheServer? {
            return if (proxy == null) newProxy(context)
                .also { proxy = it } else proxy
        }

        private fun newProxy(c: Context?): HttpProxyCacheServer? {
            return HttpProxyCacheServer(c)
        }

        private const val cacheSize: Long = 50 * 1024 * 1024 // 50MB
        fun getCache(): Cache? {
            context?.cacheDir?.let {
                Logger.e("MiboApplication Cache not null")
                return Cache(it, cacheSize)
            }
            return null
        }


    }

   // private var mFirebaseAnalytics: FirebaseAnalytics? = null

    override fun onCreate() {
        super.onCreate()
        MIBO.init(this)
        context = this
        //CoilLogger.setEnabled(true)
        AndroidThreeTen.init(this)
        MiboEvent.init(applicationContext)
        Logger.DEBUG = DEBUG
        //AppWatcher.config = AppWatcher.config.copy(watchFragmentViews = false)
        initBle()
        Fitbit.setup(this)
        //Fresco.initialize(this);

        //test()
    }


    private fun initBle() {
        BleManager.getInstance()
            .init(context!!.applicationContext as Application)
        val scanRuleConfig =
            BleScanRuleConfig.Builder()
                .setDeviceName(true, "MBRXL", "MIBO", "HW", "MI SCALE", "WS806", "")
                .setAutoConnect(true)
                .setScanTimeOut(SCAN_TIME)
                .build()
        BleManager.getInstance().initScanRule(scanRuleConfig)
    }

    fun setHandler() {
        Thread.setDefaultUncaughtExceptionHandler { thread, e ->
            Logger.save(e)
        }
    }

    fun test() {
        Logger.e("encrypt ---------------------------------")
        val crypt = MCrypt()
//        val temp1 = crypt.encrypt("sumeet")
//        //val temp2 = crypt.encrypt2("sumeet")
//        //Logger.e("encrypt :: ${temp2?.contentToString()}")
//        Logger.e("encrypt :: $temp1")
//        val temp2 = crypt.decrypt(temp1)
//        Logger.e("decrypt :: ${temp2?.contentToString()}")
//        Logger.e("decrypt :: " + String(temp2))

       // val dec = "f60cbba5479ca4ffdec34dc504cab62c"
        val dec = "7824605c2f4f4a2838ee9210b48895be"
        val n = String(crypt.decrypt(dec))
        Logger.e("decrypt1 :: $dec")
        Logger.e("decrypt2 :: $n")
        val n2 = crypt.encrypt(n)
        Logger.e("decrypt3 :: $n2")
        Logger.e("decrypt4 :: ${String(crypt.decrypt(n2))}")
    }

//    fun pageEvent(userName: String, userId: String, pageName: String) {
//        val bundle = Bundle()
//        bundle.putString("user_id", userId)
//        bundle.putString("user_name", userName)
//        bundle.putString("page_name", pageName)
//       // mFirebaseAnalytics?.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
//    }
//
//    fun loginEvent(userName: String, userId: String) {
//        val bundle = Bundle()
//        bundle.putString("user_id", userId)
//        bundle.putString("user_name", userName)
//        mFirebaseAnalytics?.logEvent(FirebaseAnalytics.Event.LOGIN, bundle)
//    }
//
//    fun registerEvent(userName: String, userId: String) {
//        val bundle = Bundle()
//        bundle.putString("user_id", userId)
//        bundle.putString("user_name", userName)
//        mFirebaseAnalytics?.logEvent(FirebaseAnalytics.Event.SIGN_UP, bundle)
//    }

    fun extractLogToFile() : String {

//        val manager = packageManager;
//        var info : PackageInfo = null;
//        try {
//            info = manager.getPackageInfo (this.packageName, 0);
//        } catch (e2: Exception) {
//        }
//        var model = Build.MODEL;
//        if (!model.startsWith(Build.MANUFACTURER))
//            model = Build.MANUFACTURER + " " + model;
//
//        // Make file name - file must be saved to external storage or it wont be readable by
//        // the email app.
//        var path = Environment.getExternalStorageDirectory() + "/" + "MyApp/";
//        var fullName = path + "";
//
//        // Extract to file.
//        var file = File (fullName);
//        var reader : InputStreamReader = null;
//        var writer: FileWriter  = null;
//        try
//        {
//            // For Android 4.0 and earlier, you will get all app's log output, so filter it to
//            // mostly limit it to your app's output.  In later versions, the filtering isn't needed.
//            var cmd = (Build.VERSION.SDK_INT <= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) ?:
//            "logcat -d -v time MyApp:v dalvikvm:v System.err:v *:s" + "logcat -d -v time";
//
//            // get input stream
//            Process process = Runtime.getRuntime().exec(cmd);
//            reader = new InputStreamReader (process.getInputStream());
//
//            // write output stream
//            writer = new FileWriter (file);
//            writer.write ("Android version: " +  Build.VERSION.SDK_INT + "\n");
//            writer.write ("Device: " + model + "\n");
//            writer.write ("App version: " + (info == null ? "(null)" : info.versionCode) + "\n");
//
//            char[] buffer = new char[10000];
//            do
//            {
//                int n = reader.read (buffer, 0, buffer.length);
//                if (n == -1)
//                    break;
//                writer.write (buffer, 0, n);
//            } while (true);
//
//            reader.close();
//            writer.close();
//        }
//        catch (IOException e)
//        {
//            if (writer != null)
//                try {
//                    writer.close();
//                } catch (IOException e1) {
//                }
//            if (reader != null)
//                try {
//                    reader.close();
//                } catch (IOException e1) {
//                }
//
//            // You might want to write DialogListener failure message to the log here.
//            return null;
//        }
//
//        return fullName;
        return ""
    }
}