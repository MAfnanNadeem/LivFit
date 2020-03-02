/*
 *  Created by Sumeet Kumar on 2/25/20 4:43 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 2/20/20 5:42 PM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.ui.rxl

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import coil.api.load
import kotlinx.android.synthetic.main.fragment_quickplay_detail.*
import life.mibo.hardware.SessionManager
import life.mibo.hardware.models.Device
import life.mibo.hexa.R
import life.mibo.hexa.models.rxl.RxlExercises
import life.mibo.hexa.models.rxl.RxlProgram
import life.mibo.hexa.ui.base.BaseFragment
import life.mibo.hexa.ui.main.Navigator
import life.mibo.hexa.utils.Constants


class RxlProgramDetailsFragment : BaseFragment() {


    private lateinit var controller: ReactionLightController
    private var code = 23456
    private var program: RxlProgram? = null

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View? {
//        val transition =
//            TransitionInflater.from(this.activity).inflateTransition(R.transition.transition_course)
//
//        sharedElementEnterTransition = androidx.transition.ChangeScroll().apply {
//            duration = 750
//        }
//        sharedElementEnterTransition = transition
//        sharedElementReturnTransition = null

        //postponeEnterTransition()
        val root = i.inflate(R.layout.fragment_quickplay_detail, c, false)
        if (Build.VERSION.SDK_INT >= 21) {
//            val item = arguments?.getSerializable(BUNDLE_DATA)
//            if (item != null && item is RXLPrograms.Program?) {
//                root.findViewById<View?>(R.id.iv_icon)?.transitionName = item.getTransitionIcon()
//                root.findViewById<View?>(R.id.tv_title)?.transitionName = item.getTransitionTitle()
//            }
        }

        //startPostponedEnterTransition()
        return root
    }

    override fun onViewCreated(root: View, savedInstanceState: Bundle?) {
        super.onViewCreated(root, savedInstanceState)
        //controller = ReactionLightController(this, this)
        navigate(Navigator.HOME_VIEW, true)
        setHasOptionsMenu(true)
        // com.onecoder.devicelib.base.control.manage
        //controller.onStart()
        //controller.getPrograms()

        btn_next?.setOnClickListener {
            nextClicked()
            // navigate(Navigator.RXL_QUICKPLAY_DETAILS_PLAY, null)
        }

        val arg = arguments
        if (arg != null) {
            program = arg.getSerializable(Constants.BUNDLE_DATA) as RxlProgram?
        }
        log("program >> $program")

        tv_title?.text = program?.name
        // activity?.title = program?.name
        activity?.title = getString(R.string.program_detail)
        tv_desc?.text = program?.description
        // iv_icon?.load(program?.urlIcon)

        if (program?.avatarBase64.isNullOrEmpty()) {
            iv_icon_giff?.setImageResource(R.drawable.ic_rxl_pods_icon)
            // image?.setColorFilter(Constants.PRIMARY)
            // setGradient(imageBg, image?.drawable)
        } else {
            val bitmap =
                life.mibo.hexa.utils.Utils.convertStringImagetoBitmap(program?.avatarBase64)
            iv_icon_giff?.load(bitmap)

        }

        tv_select_stations?.text = "${program?.workStation}"
        //tv_select_pods?.text = "${program?}"
        tv_select_lights?.text = "${program?.tapProximity?.toUpperCase()}"
        getPods()
    }

    private fun getPods() {
        //tv_desc?.text = ""
        val list = SessionManager.getInstance().userSession.devices
        if (list.size > 0) {
            val pods = ArrayList<Device>()
            list.forEach {
                if (it.isPod) {
                    pods.add(it)
                }
            }
            tv_select_pods?.text = "${pods.size}"
        } else {
            tv_select_pods?.text = "0"
        }


    }

    fun nextClicked() {
        program?.let {
            val intent = Intent(context, QuickPlayDetailsActivity::class.java)
            intent.putExtra(Constants.BUNDLE_DATA, it)
            startActivityForResult(intent, code)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        log("onActivityResult $requestCode - $resultCode - $data")
        if (requestCode == code && resultCode == 3) {
            navigate(Navigator.RXL_COURSE_CREATE, program)
        }
    }

}
