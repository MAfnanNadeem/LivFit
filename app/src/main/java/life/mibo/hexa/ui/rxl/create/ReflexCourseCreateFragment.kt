/*
 *  Created by Sumeet Kumar on 1/26/20 8:55 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/26/20 8:29 AM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.ui.rxl.create

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.transition.TransitionInflater
import coil.api.load
import kotlinx.android.synthetic.main.fragment_rxl_create.*
import life.mibo.hexa.R
import life.mibo.hexa.core.API
import life.mibo.hexa.core.Prefs
import life.mibo.hexa.core.toIntOrZero
import life.mibo.hexa.models.base.ResponseData
import life.mibo.hexa.models.rxl.RxlExercises
import life.mibo.hexa.models.rxl.RxlProgram
import life.mibo.hexa.models.rxl.SaveRXLProgram
import life.mibo.hexa.models.rxl.SaveRxlExercise
import life.mibo.hexa.ui.base.BaseFragment
import life.mibo.hexa.ui.main.MiboEvent
import life.mibo.hexa.ui.main.Navigator
import life.mibo.hexa.ui.rxl.impl.CourseCreateImpl
import life.mibo.hexa.ui.rxl.impl.CreateCourseAdapter
import life.mibo.hexa.ui.rxl.impl.ReflexDialog
import life.mibo.hexa.utils.Constants
import life.mibo.hexa.utils.Toasty
import life.mibo.hexa.utils.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ReflexCourseCreateFragment : BaseFragment(), CourseCreateImpl.Listener {

    companion object {
        const val DATA = Constants.BUNDLE_DATA
        const val DATA_PROGRAM = "course_program"
    }

    lateinit var viewImpl: CourseCreateImpl
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //val transition = TransitionInflater.from(this.activity).inflateTransition(android.R.transition.move)
        val transition =
            TransitionInflater.from(this.activity).inflateTransition(R.transition.transition_course)

        sharedElementEnterTransition = androidx.transition.ChangeScroll().apply {
            duration = 750
        }
        sharedElementEnterTransition = transition
        sharedElementReturnTransition = null
        //androidx.transition.ChangeImageTransform
//        sharedElementEnterTransition = ChangeBounds().apply {
//            duration = 750
//            enterTransition = transition
//            exitTransition = transition
//        }

//        sharedElementReturnTransition = ChangeBounds().apply {
//            duration = 750
//        }
        postponeEnterTransition()
        val root = inflater.inflate(R.layout.fragment_rxl_create, container, false)
        if (Build.VERSION.SDK_INT >= 21) {
            val item = arguments?.getSerializable(DATA)
            if (item != null && item is CreateCourseAdapter.Course) {
                root.findViewById<View?>(R.id.iv_icon_giff)?.transitionName = item.getTransitionIcon()
                root.findViewById<View?>(R.id.tv_title)?.transitionName = item.getTransitionTitle()
            }
        }

        startPostponedEnterTransition()
        return root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val data = arguments?.getSerializable(DATA)
        if (data is CreateCourseAdapter.Course) {
            tv_title?.text = data?.title
            iv_icon_giff?.setImageResource(data.icon)
//            ViewCompat.setTransitionName(tv_title!!, data?.transitionTitle)
//            ViewCompat.setTransitionName(tv_title!!, data?.transitionTitle)
//            ViewCompat.setTransitionName(title!!, "title_$adapterPosition")
        }

        viewImpl = CourseCreateImpl(requireContext())

        tv_select_stations?.setOnClickListener {
            viewImpl.showDialog(CourseCreateImpl.Type.STATIONS)
        }
        tv_select_cycles?.setOnClickListener {
            viewImpl.showDialog(CourseCreateImpl.Type.CYCLES)
        }
        tv_select_pods?.setOnClickListener {
            viewImpl.showDialog(CourseCreateImpl.Type.PODS)
        }
        tv_select_lights?.setOnClickListener {
            viewImpl.showDialog(CourseCreateImpl.Type.LIGHT_LOGIC)
        }
        tv_select_players?.setOnClickListener {
            viewImpl.showDialog(CourseCreateImpl.Type.PLAYERS)
        }
        tv_select_delay?.setOnClickListener {
            viewImpl.showDialog(CourseCreateImpl.Type.DELAY)
        }
        tv_select_duration?.setOnClickListener {
            viewImpl.showDialog(CourseCreateImpl.Type.DURATION)
        }
        tv_select_action?.setOnClickListener {
            viewImpl.showDialog(CourseCreateImpl.Type.ACTION)
        }
        et_course_structure?.setOnClickListener {
            viewImpl.showDialog(CourseCreateImpl.Type.STRUCTURE)
        }
        viewImpl.listener = this


        navigate(Navigator.HOME_VIEW, true)
        if (data == null) {
            val program = arguments?.getSerializable(DATA_PROGRAM)
            if (program is RxlProgram) {
                tv_title?.text = program.logicType()
                //loadImage(program.image)
                et_course_structure?.text = program.category
                et_course_desc?.setText(program.description)
                tv_select_stations?.text = "${program.workStation}"
                tv_select_cycles?.text = "${program.cycle}"
                tv_select_pods?.text = "${program.pods}"
                tv_select_lights?.text = "${program.logicType()}"
                tv_select_delay?.text = "${program.pause} sec"
                tv_select_duration?.text = "${program.totalDuration} sec"
                tv_select_players?.text = "${program.players}"
                tv_select_action?.text = "${program.action} sec"
                avatarBase64 = program?.avatarBase64 ?: ""
            } else {
                initTitles()
            }
        } else {
            initTitles()
        }
        btn_save?.setOnClickListener {
            validate()
        }

        et_course_desc?.setOnTouchListener { v, event ->

            if (et_course_desc.hasFocus()) {
                v.parent.requestDisallowInterceptTouchEvent(true)
                when (event.action and MotionEvent.ACTION_MASK) {
                    MotionEvent.ACTION_SCROLL -> {
                        v.parent.requestDisallowInterceptTouchEvent(false)
                        return@setOnTouchListener true
                    }
                }
            }
            return@setOnTouchListener false
        }

      //  if(tv_select_lights.equals("sequence"))
     //  viewImpl.createSequenceList(recyclerViewSequence)
    }

    private fun loadImage(images: String?) {
        if (images == null)
            return
        try {
            val img = images.split(",")
            iv_icon_giff?.load(img[0].replace("[", ""))
        } catch (e: Exception) {
            iv_icon_giff?.setImageResource(R.drawable.ic_rxl_pods_icon)
        }
    }

    private var checked = 1

    private fun initTitles() {
        tv_select_stations?.text = viewImpl.getTitle(CourseCreateImpl.Type.STATIONS)
        tv_select_cycles?.text = viewImpl.getTitle(CourseCreateImpl.Type.CYCLES)
        tv_select_pods?.text = viewImpl.getTitle(CourseCreateImpl.Type.PODS)
        tv_select_lights?.text = viewImpl.getTitle(CourseCreateImpl.Type.LIGHT_LOGIC)
        tv_select_delay?.text = viewImpl.getTitle(CourseCreateImpl.Type.DELAY)
        tv_select_duration?.text = viewImpl.getTitle(CourseCreateImpl.Type.DURATION)
        tv_select_players?.text = viewImpl.getTitle(CourseCreateImpl.Type.PLAYERS)
        tv_select_action?.text = viewImpl.getTitle(CourseCreateImpl.Type.ACTION)

        radio_group?.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radio_start_sensor -> {
                    //fluidSlider?.visibility = View.VISIBLE
                    //Utils.slideUp(fluidSlider)
                    nestedScrollView?.post {
                        nestedScrollView?.fullScroll(View.FOCUS_DOWN)
                    }
                    checked = 3
                }
                R.id.radio_start_now -> {
                    //Utils.slideDown(fluidSlider)
                    //fluidSlider?.visibility = View.GONE
                    checked = 1
                }
                R.id.radio_start_tap -> {
                    checked = 2
                    //fluidSlider?.visibility = View.GONE
                    //Utils.slideDown(fluidSlider)
                }

            }

        }
    }

    override fun onDialogItemSelected(item: ReflexDialog.Item, type: Int) {
        dialogItemSelected(item.title, type)
    }

    private fun dialogItemSelected(title: String, type: Int) {
        when (type) {
            CourseCreateImpl.Type.STATIONS.type -> {
                tv_select_stations?.text = title
            }
            CourseCreateImpl.Type.CYCLES.type -> {
                tv_select_cycles?.text = title
            }
            CourseCreateImpl.Type.PODS.type -> {
                tv_select_pods?.text = title
            }
            CourseCreateImpl.Type.LIGHT_LOGIC.type -> {
                tv_select_lights?.text = title
            }
            CourseCreateImpl.Type.PLAYERS.type -> {
                tv_select_players?.text = title
            }
            CourseCreateImpl.Type.DELAY.type -> {
                tv_select_delay?.text = title?.replace("seconds", "sec")
            }
            CourseCreateImpl.Type.DURATION.type -> {
                tv_select_duration?.text = title?.replace("seconds", "sec")
            }
            CourseCreateImpl.Type.ACTION.type -> {
                tv_select_action?.text = title?.replace("seconds", "sec")
            }
            CourseCreateImpl.Type.STRUCTURE.type -> {
                et_course_structure?.text = title
            }
        }
    }

    fun type(type: CourseCreateImpl.Type) {
        when (type) {
            CourseCreateImpl.Type.STATIONS -> {

            }
            CourseCreateImpl.Type.CYCLES -> {

            }
            CourseCreateImpl.Type.PODS -> {

            }
            CourseCreateImpl.Type.LIGHT_LOGIC -> {

            }
            CourseCreateImpl.Type.PLAYERS -> {

            }
            CourseCreateImpl.Type.DELAY -> {

            }
            CourseCreateImpl.Type.DURATION -> {

            }
        }
    }

    private fun showError(error: Int, editText: EditText?) {
        showError(context?.getString(error), editText)
    }

    private fun showError(error: String?, editText: TextView?) {
        editText?.error = error
        editText?.addTextChangedListener(object : TextWatcher {

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun afterTextChanged(s: Editable) {
                if (editText?.error != null)
                    editText?.error = null
            }
        })
    }

    private fun validate() {
        val member = Prefs.get(context).member ?: return

        if (Utils.isEmpty(et_course_name?.text)) {
            showError("Enter Course Name", et_course_name)
            return
        }
        if (Utils.isEmpty(et_course_structure?.text)) {
            showError("Enter Course Structure", et_course_structure)
            return
        }
        if (Utils.isEmpty(et_course_desc?.text)) {
            showError("Enter Course Description", et_course_desc)
            return
        }

        val images = arrayOf(
            "http://test.mibo.world/assets/images/rxl.jpg",
            "http://test.mibo.world/assets/images/rxl.jpg",
            "http://test.mibo.world/assets/images/rxl.jpg",
            "http://test.mibo.world/assets/images/rxl.jpg"
        )

        saveExerciseProgram(member.id(), member.accessToken)

//        val data = Data(
//            "no",
//            getInt(tv_select_action.text),
//            getInt(tv_select_cycles.text),
//            getInt(tv_select_delay.text),
//            et_course_desc?.text.toString(),
//            images.contentToString(),
//            tv_select_lights?.text?.toString(),
//            member.id(),
//            et_course_name.text?.toString(),
//            getInt(tv_select_players.text),
//            getInt(tv_select_pods.text),
//            tv_title.text?.toString(),
//            checked,
//            "",
//            et_course_structure?.text.toString(),
//            getInt(tv_select_duration.text),
//            "",
//            getInt(tv_select_stations.text)
//        )
//
//        val program = SaveRXLProgram(data, member.accessToken)
//        saveProgram(program)

    }

    private fun saveExerciseProgram(
        memberId: String,
        token: String?,
        tap: String = "TAP",
        proximity: Int = 0
    ) {
        val program = SaveRxlExercise.Program(
            "no",
            getInt(tv_select_action.text),
            avatarBase64,
            "",
            memberId,
            getInt(tv_select_cycles.text),
            0,
            et_course_desc?.text.toString(),
            memberId,
            et_course_name.text?.toString(),
            "",
            "",
            getInt(tv_select_players.text),
            getInt(tv_select_pods.text),
            "",
            tv_select_delay.text?.toString(),
            "$proximity",
            "$tap",
            "$tap",
            getInt(tv_select_duration.text),
            "",
            getType(),
            getInt(tv_select_stations.text)
        )

        val data = SaveRxlExercise(program, token)
        saveProgram(data)
    }

    private var avatarBase64 = ""

    fun getInt(text: CharSequence): Int = text.toString().toIntOrZero()
    private fun getType(): Int = RxlExercises.getType(tv_select_lights?.text?.toString())

    private fun saveProgram(program: SaveRXLProgram?) {
        if (program == null) {
            return
        }
        getDialog()?.show()
        API.request.getApi()
            .saveRXLProgram(program)
            .enqueue(object : Callback<ResponseData> {

                override fun onFailure(call: Call<ResponseData>, t: Throwable) {
                    getDialog()?.dismiss()
                    t.printStackTrace()
                    Toasty.error(context!!, R.string.unable_to_connect).show()
                    MiboEvent.log(t)
                    t.printStackTrace()
                }

                override fun onResponse(
                    call: Call<ResponseData>,
                    response: Response<ResponseData>
                ) {
                    getDialog()?.dismiss()

                    val data = response.body()
                    if (data != null) {
                        if (data.status.equals("success", true)) {
                            data.data?.message?.let {
                                Toasty.info(context!!, it).show()
                            }
                            try {
                                Prefs.get(context).set("rxl_saved", true)
                            }
                            catch (e: Exception){
                                MiboEvent.log(e)
                            }

                        } else if (data.status.equals("error", true)) {
                            checkSession(data)
                        }
                    } else {

                    }
                }
            })
    }

    private fun saveProgram(program: SaveRxlExercise?) {
        if (program == null) {
            return
        }
        getDialog()?.show()
        API.request.getApi()
            .saveRXLExerciseProgram(program)
            .enqueue(object : Callback<ResponseData> {

                override fun onFailure(call: Call<ResponseData>, t: Throwable) {
                    getDialog()?.dismiss()
                    t.printStackTrace()
                    Toasty.error(context!!, R.string.unable_to_connect).show()
                    MiboEvent.log(t)
                    t.printStackTrace()
                }

                override fun onResponse(
                    call: Call<ResponseData>,
                    response: Response<ResponseData>
                ) {
                    getDialog()?.dismiss()

                    val data = response.body()
                    if (data != null) {
                        if (data.status.equals("success", true)) {
                            data.data?.message?.let {
                                Toasty.info(context!!, it).show()
                            }
                            try {
                                Prefs.get(context).set("rxl_saved", true)
                            } catch (e: Exception) {
                                MiboEvent.log(e)
                            }
                            navigate(Navigator.RXL_HOME, null)

                        } else if (data.status.equals("error", true)) {
                            checkSession(data)
                        }
                    } else {

                    }
                }
            })
    }

    override fun onStop() {
        super.onStop()
    }
}
