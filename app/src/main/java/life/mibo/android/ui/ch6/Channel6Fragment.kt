package life.mibo.android.ui.ch6

import android.content.res.Configuration
import android.os.Bundle
import android.view.*
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_channel6.*
import life.mibo.android.R
import life.mibo.android.core.Prefs
import life.mibo.android.ui.base.BaseFragment
import life.mibo.android.ui.main.MiboEvent
import life.mibo.android.ui.main.Navigator
import life.mibo.android.utils.Utils
import life.mibo.hardware.events.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.concurrent.TimeUnit


class Channel6Fragment : BaseFragment(), ChannelObserver {

    //private lateinit var viewModel: Channel6ViewModel
    private lateinit var controller: Channel6Controller
    private lateinit var userId: String
    var recyclerView: RecyclerView? = null
    var stateBundle = Bundle()

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View? {
        log("onCreateView")
//        viewModel =
//            ViewModelProviders.of(this).get(Channel6ViewModel::class.java)
        val root = i.inflate(R.layout.fragment_channel6, c, false)
        //  val textView: TextView = root.findViewById(R.id.text_dashboard)
        //recyclerView = root.findViewById(R.id.recyclerView)
//        viewModel.text.observe(this, Observer {
//            //    textView.text = ""//it
//        })

        this.activity?.actionBar?.hide()
        controller = Channel6Controller(this@Channel6Fragment, this)

        //setRecycler(recyclerView!!)
        //retainInstance = true
        //SessionManager.getInstance().session = Session()
        setHasOptionsMenu(true)
        return root
    }

    var test = true
    var isTrainer = false
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        log("onViewCreated")
        //controller = Channel6Controller(this@Channel6Fragment)
        if (arguments != null)
            stateBundle = requireArguments()
        userId = Prefs.get(this@Channel6Fragment.activity)["user_uid"]
        //life.mibo.hardware.core.Logger.e("Channel6Fragment : stateBundle ", stateBundle)
        isTrainer = stateBundle.getBoolean("is_trainer", false)
        controller.onViewCreated(view, stateBundle)

        iv_plus?.setOnClickListener {
            controller.onMainPlusClicked()
        }

        iv_minus?.setOnClickListener {
            controller.onMainMinusClicked()
        }

        iv_play?.setOnClickListener {
            //test = !test
            //updatePlayButton(test)
            controller.onMainPlayClicked()
            //startSession(SessionManager.getInstance().userSession.user)
            //onPlayClicked()
        }

        iv_plus?.setOnLongClickListener {
            Observable.timer(2, TimeUnit.SECONDS).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).doOnComplete {
                    log("starting again device")
                    EventBus.getDefault().postSticky(SendDeviceStartEvent(userId))
                }.subscribe()

            true
        }

        iv_stop?.setOnClickListener {
            controller.onMainStopClicked()
        }

        //Utils.loadImage(hexagonImageView, "", R.drawable.ic_person_black_24dp)
        // life.mibo.hardware.core.Logger.e("Session Start ", stateBundle)


        val name: String? = stateBundle.getString("program_name", null);
        name?.let {
            activity?.title = it
        }

        if (isTrainer)
            Utils.loadImage(hexagonImageView, stateBundle.getString("member_image", ""), true)
        else Utils.loadImage(hexagonImageView, Prefs.get(context).member?.profileImg, true)

        MiboEvent.event(
            "Booster_Session",
            "Channel6Fragment isTrainer=$isTrainer",
            "ProgramName=$name  UserId=$userId"
        )
    }


    override fun updatePlayButton(isPaused: Boolean) {
        activity?.runOnUiThread {
            iv_play?.setImageBitmap(null)
            //iv_play?.background = null
           // iv_play?.scaleType = ImageView.ScaleType.CENTER
            if (isPaused) {
                //iv_play?.setBackground(AppCompatResources.getDrawable(context!!, R.drawable.ic_resume_hexa))
                iv_play?.setImageResource(R.drawable.ic_resume_hexa)
                //iv_play?.scaleType = ImageView.ScaleType.CENTER
            } else {
                iv_play?.setImageResource(R.drawable.ic_play_hexa)

            }
        }
    }

    override fun onTimerUpdate(time: Long) {
        //log("onTimerUpdate $time")
//        if (time == 0L) {
//            tv_timer?.text = "Completed"
//            controller.exerciseCompleted()
//        } else {
//            // tvTimer?.text = "${end.minus(it)} sec"
//            tv_timer?.text = String.format("%02d:%02d", time / 60, time % 60)
//        }
    }



    private var isLand = false
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        isLand = newConfig.orientation === Configuration.ORIENTATION_LANDSCAPE
        //Toasty.warning(this@DeviceScanFragment.context!!, "Configuration changes $isLand").show()
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        //setRecycler(recyclerView!!)
    }

    // EventBus
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onDevicePlayPauseEvent(event: DevicePlayPauseEvent) {
        log("onDevicePlayPauseEvent")
        EventBus.getDefault().removeStickyEvent(event)
        controller.onDevicePlayPauseEvent(event)
    }

    // When user click on main plus/minus - response
    @Subscribe(threadMode = ThreadMode.ASYNC)
    fun onGetMainLevelEvent(event: GetMainLevelEvent) {
        EventBus.getDefault().removeStickyEvent(event)
        controller.onGetMainLevelEvent(event)
        //log("onGetMainLevelEvent ${event?.level}")
    }

    // When user click on channel's plus/minus - response
    @Subscribe(threadMode = ThreadMode.ASYNC)
    fun onGetLevelsEvent(event: GetLevelsEvent) {
        EventBus.getDefault().removeStickyEvent(event)
        controller.onGetLevelsEvent(event)
    }

    // Update progress time
    @Subscribe(threadMode = ThreadMode.ASYNC)
    fun onGetProgramStatusEvent(event: ProgramStatusEvent) {
        EventBus.getDefault().removeStickyEvent(event)
        controller.onGetProgramStatusEvent(event)
    }



    override fun onStart() {
        log("onStart")
        super.onStart()
        EventBus.getDefault().register(this)
        navigate(Navigator.DRAWER_LOCK, null)
    }

    override fun onStop() {
        log("onStop")
        navigate(Navigator.DRAWER_UNLOCK, null)
        EventBus.getDefault().unregister(this)
        controller.onStop()
        super.onStop()
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        log("onCreateOptionsMenu")
        //inflater.inflate(R.menu.menu_channel_fragment, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        log("onOptionsItemSelected")
//        if(item.itemId == R.id.item_scan){
//            navigate(R.id.nav_scan, null)
//        }
        return super.onOptionsItemSelected(item)
    }

    interface Listener {
        fun onMainPlusClicked()
        fun onMainMinusClicked()
        fun onMainPlayClicked()
        fun onMainStopClicked()
        fun onStop()
        fun onViewCreated(view: View, bundle: Bundle)
    }

    override fun onNavBackPressed(): Boolean {
        return onBackPressed()
    }

    override fun onBackPressed(): Boolean {
        log("onBackPressed")
        //Toasty.info(context!!, "onBackPressed").show()
        if (controller.onBackPressed()) {
            navigate(Navigator.POST, arguments)
            return true
        }
        return false
    }
}