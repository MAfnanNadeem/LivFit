package life.mibo.hexa.ui.ch6

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_channel6.*
import life.mibo.hardware.CommunicationManager
import life.mibo.hardware.SessionManager
import life.mibo.hardware.events.SendChannelsLevelEvent
import life.mibo.hardware.events.SendDevicePlayEvent
import life.mibo.hardware.events.SendDeviceStopEvent
import life.mibo.hexa.R
import life.mibo.hexa.ui.base.BaseFragment
import life.mibo.hexa.ui.ch6.adapter.Channel6Listener
import life.mibo.hexa.ui.ch6.adapter.Channel6Model
import life.mibo.hexa.ui.ch6.adapter.ChannelAdapter
import org.greenrobot.eventbus.EventBus


class Channel6Fragment : BaseFragment(), Channel6Listener {

    private lateinit var viewModel: Channel6ViewModel
    var recyclerView: RecyclerView? = null
    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View? {
        viewModel =
            ViewModelProviders.of(this).get(Channel6ViewModel::class.java)
        val root = i.inflate(R.layout.fragment_channel6, c, false)
        //  val textView: TextView = root.findViewById(R.id.text_dashboard)
        recyclerView = root.findViewById(R.id.recyclerView)
        viewModel.text.observe(this, Observer {
            //    textView.text = ""//it
        })
        this.activity?.actionBar?.hide()
        setRecycler(recyclerView!!)
        retainInstance = true

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        iv_plus?.setOnClickListener {
            onPlusClicked()
        }

        iv_minus?.setOnClickListener {
            onMinusClicked()
        }

        iv_play?.setOnClickListener {
            onPlayClicked()
        }

        iv_stop?.setOnClickListener {
            onStopClicked()
        }
    }

    private var manager: CommunicationManager? = null
    fun getManager(): CommunicationManager {
        if (manager == null)
            manager = CommunicationManager.getInstance()
        return manager!!
    }

    var list = ArrayList<Channel6Model>()
    var adapter: ChannelAdapter? = null
    fun setRecycler(view: RecyclerView) {

        if (list == null)
            list = ArrayList()
        list.clear()

        //val c = Channel6Model(1, R.drawable.ic_channel_abdomen, 1, 2)
        //c.percentChannel?.observe(this)
        list.add(Channel6Model(1, R.drawable.ic_channel_abdomen, 1, 2))
        list.add(Channel6Model(2, R.drawable.ic_channel_back_neck, 0, 0))
        list.add(Channel6Model(3, R.drawable.ic_channel_biceps, 0, 0))
        list.add(Channel6Model(4, R.drawable.ic_channel_chest, 0, 0))
        list.add(Channel6Model(5, R.drawable.ic_channel_glutes, 0, 0))
        list.add(Channel6Model(6, R.drawable.ic_channel_thighs, 0, 0))

//        viewModel.list.observe(this, Observer {
//            it.addAll(list)
//        })

        if (isLand)
            view.layoutManager = GridLayoutManager(this@Channel6Fragment.activity, 6)
        else
            view.layoutManager = GridLayoutManager(this@Channel6Fragment.activity, 1)
        adapter = ChannelAdapter(list, isLand)
        adapter?.setListener(this)
        //val manager = GridLayoutManager(this@DeviceScanFragment.activity, 1)
        //view.layoutManager = manager
        view.adapter = adapter
        //Toasty.warning(this@DeviceScanFragment.context, "")
        //Toasty.warning(this@DeviceScanFragment.context!!, "Configuration changes $isLand").show()
//        viewModel.list.observe(this, Observer {
//            log("data changed")
//            adapter?.notifyDataSetChanged()
//        })

    }

    private fun onPlayClicked() {
        Observable.fromArray(list).flatMapIterable { x -> x }
            .subscribeOn(Schedulers.computation()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : io.reactivex.Observer<Channel6Model> {
                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(t: Channel6Model) {

                    log("onPlayClicked " + SessionManager.getInstance().session)
                    sendPlaySignals()
                    //mViewMvc.updatePlayPause(SessionManager.getInstance().getSession().getUserSelected().getUserBooster().getIsStarted())
                    t.isPlay = false
                }

                override fun onError(e: Throwable) {
                    log("iv_plus onError", e)
                    e.printStackTrace()
                }

                override fun onComplete() {
                    adapter?.notifyDataSetChanged()

                }
            })
    }

    private fun sendPlaySignals() {
        log("sendPlaySignals starting ")
        if (SessionManager.getInstance().session.userSelected.userBooster.isStarted) {
            log("sendPlaySignals stopping session ")
            EventBus.getDefault()
                .postSticky(SendDeviceStopEvent(SessionManager.getInstance().session.userSelected.userBooster.uid))
            SessionManager.getInstance().session.userSelected.mainLevel = 0
            SessionManager.getInstance()
                .session.userSelected.userBooster.isStarted = false
        } else {
            if (SessionManager.getInstance().session.currentSessionStatus != 1 && SessionManager.getInstance().session.userSelected.isActive) {
                log("sendPlaySignals starting session ")
                EventBus.getDefault()
                    .postSticky(SendDevicePlayEvent(SessionManager.getInstance().session.userSelected.userBooster.uid))
                SessionManager.getInstance()
                    .session.userSelected.userBooster.isStarted = true
            }
        }

//        if (SessionManager.getInstance().session.currentSessionStatus == 0 || SessionManager.getInstance().session.currentSessionStatus == 3) {
//            log("sendPlaySignals session not starting ")
//        } else {
//            if (SessionManager.getInstance().session.userSelected.userBooster.isStarted) {
//                log("sendPlaySignals stopping session ")
//                EventBus.getDefault()
//                    .postSticky(SendDeviceStopEvent(SessionManager.getInstance().session.userSelected.userBooster.uid))
//                SessionManager.getInstance().session.userSelected.mainLevel = 0
//                SessionManager.getInstance()
//                    .session.userSelected.userBooster.isStarted = false
//            } else {
//                if (SessionManager.getInstance().session.currentSessionStatus != 1 && SessionManager.getInstance().session.userSelected.isActive) {
//                    log("sendPlaySignals starting session ")
//                    EventBus.getDefault()
//                        .postSticky(SendDevicePlayEvent(SessionManager.getInstance().session.userSelected.userBooster.uid))
//                    SessionManager.getInstance()
//                        .session.userSelected.userBooster.isStarted = true
//                }
//            }
//            // SessionManager.getInstance().getSession().getUserSelected().setActive(true);
//        }
    }

    private fun onStopClicked() {
        Observable.fromArray(list).flatMapIterable { x -> x }
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : io.reactivex.Observer<Channel6Model> {
                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(t: Channel6Model) {
                    t.isPlay = true
                }

                override fun onError(e: Throwable) {
                    log("iv_plus onError", e)
                    e.printStackTrace()
                }

                override fun onComplete() {
                    adapter?.notifyDataSetChanged()
                }
            })
    }

    private fun onPlusClicked() {
        Observable.fromArray(list).flatMapIterable { x -> x }
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : io.reactivex.Observer<Channel6Model> {
                override fun onSubscribe(d: Disposable) {
                    log("iv_plus onSubscribe")
                }

                override fun onNext(t: Channel6Model) {
                    log("iv_plus onNext")
//                        t.percentChannel?.observe(this@Channel6Fragment, Observer {
//                            it.plus(1)
//                        })
                    t.incChannelPercent()
                }

                override fun onError(e: Throwable) {
                    log("iv_plus onError", e)
                    e.printStackTrace()
                }

                override fun onComplete() {
                    log("iv_plus onComplete")
                    adapter?.notifyDataSetChanged()
                }
            })
    }

    private fun onMinusClicked() {
        Observable.fromArray(list).flatMapIterable { x -> x }
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : io.reactivex.Observer<Channel6Model> {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onNext(t: Channel6Model) {
                    t.decChannelPercent()
                }

                override fun onError(e: Throwable) {
                    log("iv_plus onError", e)
                    e.printStackTrace()
                }

                override fun onComplete() {
                    adapter?.notifyDataSetChanged()

                }
            })
    }

    override fun onClick(data: Channel6Model) {

    }

    override fun onPlusClicked(data: Channel6Model) {
        onMusclePlusClicked(data.id)
    }

    override fun onMinusClicked(data: Channel6Model) {
        onMuscleMinusClicked(data.id)
    }

    override fun onPlayPauseClicked(data: Channel6Model, isPlay: Boolean) {
        onMuscleStopClicked(data.id)
    }

    fun onMuscleMinusClicked(id: Int) {
        SessionManager.getInstance().session.userSelected.decrementChannelLevelUserSelected(id)

        EventBus.getDefault().postSticky(
            SendChannelsLevelEvent(
                SessionManager.getInstance().session.userSelected.currentChannelLevels,
                SessionManager.getInstance().session.userSelected.userBooster.uid
            )
        )
        log("onMuscleMinusClicked Minus group $id")
    }

    fun onMusclePlusClicked(id: Int) {
        SessionManager.getInstance().session.userSelected.incrementChannelLevelUserSelected(id)

        EventBus.getDefault().postSticky(
            SendChannelsLevelEvent(
                SessionManager.getInstance().session.userSelected.currentChannelLevels,
                SessionManager.getInstance().session.userSelected.userBooster.uid
            )
        )
        log("onMusclePlusClicked Plus group $id")
    }

    fun onMuscleStopClicked(id: Int) {
        SessionManager.getInstance().session.userSelected.currentChannelLevels[id - 1] = 0
        EventBus.getDefault().postSticky(
            SendChannelsLevelEvent(
                SessionManager.getInstance().session.userSelected.currentChannelLevels,
                SessionManager.getInstance().session.userSelected.userBooster.uid
            )
        )
        log("onMuscleStopClicked Stop group $id")
    }

    private var isLand = false

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        isLand = newConfig.orientation === Configuration.ORIENTATION_LANDSCAPE
        //Toasty.warning(this@DeviceScanFragment.context!!, "Configuration changes $isLand").show()
        setRecycler(recyclerView!!)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        //setRecycler(recyclerView!!)
    }

    interface Listener {
        fun onMainPlusClicked()
        fun onMainMinusClicked()
        fun onMainPlayClicked()
        fun onMainStopClicked()
    }
}