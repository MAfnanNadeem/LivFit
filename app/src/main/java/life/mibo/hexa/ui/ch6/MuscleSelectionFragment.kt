/*
 *  Created by Sumeet Kumar on 3/24/20 10:05 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 3/24/20 10:01 AM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.ui.ch6

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_select_muscles.*
import life.mibo.hardware.CommunicationManager
import life.mibo.hardware.SessionManager
import life.mibo.hardware.events.ChangeColorEvent
import life.mibo.hardware.fastble.BleManager
import life.mibo.hardware.fastble.callback.BleMtuChangedCallback
import life.mibo.hardware.fastble.exception.BleException
import life.mibo.hardware.models.Device
import life.mibo.hexa.R
import life.mibo.hexa.core.API
import life.mibo.hexa.core.Prefs
import life.mibo.hexa.models.base.MemberPost
import life.mibo.hexa.models.muscle.Muscle
import life.mibo.hexa.models.muscle.MuscleCollection
import life.mibo.hexa.models.program.Program
import life.mibo.hexa.ui.base.BaseFragment
import life.mibo.hexa.ui.base.ItemClickListener
import life.mibo.hexa.ui.ch6.adapter.ChannelSelectAdapter
import life.mibo.hexa.ui.dialog.MyDialog
import life.mibo.hexa.ui.main.MiboEvent
import life.mibo.hexa.ui.main.Navigator
import life.mibo.hexa.ui.select_program.ProgramDialog
import life.mibo.hexa.utils.Toasty
import org.greenrobot.eventbus.EventBus
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.TimeUnit

class MuscleSelectionFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_select_muscles, container, false)
    }

    private var muscleAdapter: ChannelSelectAdapter? = null
    private var channelAdapter: ChannelSelectAdapter? = null
    private var colorDialog: ProgramDialog? = null

    //var isProgram = false
    var stateBundle = Bundle()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments != null)
            stateBundle = arguments!!
        loadMuscles()
        loadChannels()

        button_next?.setOnClickListener {
            onNextClicked()
        }
        getMusclesApi()
        rl_color?.setOnClickListener {
            showColors()
        }
        life.mibo.hardware.core.Logger.e("Muscle Select ", stateBundle)
        initBooster()

    }

    private fun initBooster() {
        SessionManager.getInstance().userSession.currentSessionStatus = 0
        changeColor(Color.RED)
        //updateMtu(300)
    }

    private fun onNextClicked() {
        if (channelAdapter?.list.isNullOrEmpty()) {
            toast("Please select muscles")
            return
        }
        if (checkLimit(channelAdapter?.list?.size ?: 0)) {
            log("onNextClicked " + channelAdapter?.list)
            stateBundle.putSerializable("program_channels", channelAdapter?.list)

            navigate(Navigator.SELECT_PROGRAM, stateBundle)
        }
    }

    private fun showColors() {
        if (colorDialog == null) {
            colorDialog =
                ProgramDialog(context!!, ArrayList(), object : ItemClickListener<Program> {
                    override fun onItemClicked(item: Program?, position: Int) {
                        //Toasty.info(context!!, "$position").show()

                        item?.id?.let {
                            //select_color?.visibility = View.VISIBLE
                            //circleImage?.circleColor = it
                            val drawable = select_color.background
                            if (drawable is GradientDrawable) {
                                drawable.setColor(it)
                            } else {
                                select_color.setBackgroundColor(it)
                            }
                            //val shape = ShapeDrawable()

                            changeColor(it)
                        }
                    }

                }, ProgramDialog.COLORS)
        }
        colorDialog?.showColors()
    }

    private fun changeColor(color: Int) {
        Single.fromCallable {
            val d: Device? = SessionManager.getInstance().userSession.booster
            d?.colorPalet = color
            EventBus.getDefault().postSticky(ChangeColorEvent(d, d?.uid))
            stateBundle.putInt("program_color", color)
        }.subscribeOn(Schedulers.io()).doOnError {

        }.subscribe()
    }

    private fun updateMtu(mtu: Int) {
        Single.fromCallable {
            try {
                val d = CommunicationManager.getInstance()
                    .getBle(SessionManager.getInstance().userSession.booster?.uid)
                BleManager.getInstance().setMtu(d, 300,
                    object : BleMtuChangedCallback() {
                        override fun onSetMTUFailure(exception: BleException) {
                            log("setMtu: onSetMTUFailure " + exception.message)
                        }

                        override fun onMtuChanged(mtu: Int) {
                            log("setMtu: onMtuChanged $mtu")
                        }
                    })
            } catch (e: Exception) {
                MiboEvent.log(e)
            }
        }.subscribeOn(Schedulers.io()).delay(600, TimeUnit.MILLISECONDS).doOnError {

        }.subscribe()
    }

    var deviceTye = 6;

    private fun checkLimit(size: Int): Boolean {
        if (size > deviceTye) {
            toast("You can select up-to $deviceTye muscles")
            return false
        }
        return true;
    }

    private fun toast(msg: String) {
        Toasty.snackbar(button_next, msg).show()
    }


    private fun loadMuscles() {
        val list = ArrayList<Muscle>()
//        list.add(Muscle.from(1, R.drawable.ic_channel_abdomen, true))
//        list.add(ChannelSelectAdapter.Item(2, R.drawable.ic_channel_abdomen, true))
//        list.add(ChannelSelectAdapter.Item(3, R.drawable.ic_channel_abdomen))
//        list.add(ChannelSelectAdapter.Item(4, R.drawable.ic_channel_abdomen))
//        list.add(ChannelSelectAdapter.Item(5, R.drawable.ic_channel_abdomen))
//        list.add(ChannelSelectAdapter.Item(6, R.drawable.ic_channel_abdomen))
//        list.add(ChannelSelectAdapter.Item(7, R.drawable.ic_channel_abdomen))
//        list.add(ChannelSelectAdapter.Item(8, R.drawable.ic_channel_abdomen))
//        list.add(ChannelSelectAdapter.Item(9, R.drawable.ic_channel_abdomen))
//        list.add(ChannelSelectAdapter.Item(10, R.drawable.ic_channel_abdomen))

        muscleAdapter =
            ChannelSelectAdapter(list, 2, object : ItemClickListener<Muscle> {
                override fun onItemClicked(item: Muscle?, position: Int) {
                    updateChanel(item, position)
                }

            })
        recyclerViewLeft?.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerViewLeft?.adapter = muscleAdapter


    }

    fun updateChanel(
        item: Muscle?,
        position: Int
    ) {
        if (item != null) {
            channelAdapter?.update(item)
        }
    }

    private fun loadChannels() {
        val list = ArrayList<Muscle>()
//        list.add(ChannelSelectAdapter.Item(1, R.drawable.ic_channel_abdomen))
//        list.add(ChannelSelectAdapter.Item(2, R.drawable.ic_channel_abdomen))


        channelAdapter =
            ChannelSelectAdapter(list, 1, object : ItemClickListener<Muscle> {
                override fun onItemClicked(item: Muscle?, position: Int) {

                }

            })
        recyclerView?.adapter = channelAdapter
        recyclerView?.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerView?.adapter = channelAdapter
    }

    val dialog = lazy { MyDialog.get(requireContext()) }
    private fun getMusclesApi() {
        //GetMuscleCollection
        dialog.value.show()
        val member = Prefs.get(context).member ?: return
        API.request.getApi()
            .getMuscleCollection(
                MemberPost(
                    "${member.id}",
                    "${member.accessToken}",
                    "GetMuscleCollection"
                )
            )
            .enqueue(object : Callback<MuscleCollection> {
                override fun onFailure(call: Call<MuscleCollection>, t: Throwable) {
                    dialog.value.dismiss()
                    log("getMusclesApi onFailure: " + t.message)
                }

                override fun onResponse(
                    call: Call<MuscleCollection>,
                    response: Response<MuscleCollection>
                ) {
                    dialog.value.dismiss()
                    val data = response.body();
                    val list = ArrayList<Muscle>()
                    log("getMusclesApi onResponse: " + response.body())
                    if (data != null && data.isSuccess()) {
                        data.data?.forEach {
                            it?.let { m ->
                                list.add(m)
                            }
                        }
                        log("getMusclesApi onResponse size: " + list.size)
                        muscleAdapter?.update(list)
                    } else {
                        checkSession(data)
                    }

                }

            })
    }


}