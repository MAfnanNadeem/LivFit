/*
 *  Created by Sumeet Kumar on 4/5/20 9:10 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 3/24/20 2:42 PM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.ui.ch6

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.fragment_select_muscles.button_next
import kotlinx.android.synthetic.main.fragment_select_suit.*
import life.mibo.hexa.R
import life.mibo.hexa.core.API
import life.mibo.hexa.core.Prefs
import life.mibo.hexa.models.muscle.GetSuitPost
import life.mibo.hexa.models.muscle.GetSuits
import life.mibo.hexa.models.muscle.Suit
import life.mibo.hexa.ui.base.BaseFragment
import life.mibo.hexa.ui.ch6.adapter.SliderAdapter
import life.mibo.hexa.ui.dialog.MyDialog
import life.mibo.hexa.ui.main.Navigator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SuitSelectionFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_select_suit, container, false)
    }

    private val stateBundle = Bundle()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getSuits()
        button_next?.setOnClickListener {
            onNextClicked()
        }
    }

    private fun onNextClicked() {
        // val bundle = Bundle()
        stateBundle.putSerializable("program_suit", selectedSuit)
        navigate(Navigator.SELECT_MUSCLES, stateBundle)
    }

    val dialog = lazy { MyDialog.get(requireContext()) }
    private fun getSuits() {
        //GetMuscleCollection
        dialog.value.show()
        val member = Prefs.get(context).member ?: return
        API.request.getApi().getSuits(GetSuitPost(GetSuitPost.CHANEL_6, "${member.accessToken}"))
            .enqueue(object : Callback<GetSuits> {
                override fun onFailure(call: Call<GetSuits>, t: Throwable) {
                    dialog.value.dismiss()
                    log("getMusclesApi onFailure: " + t.message)
                }

                override fun onResponse(
                    call: Call<GetSuits>,
                    response: Response<GetSuits>
                ) {
                    dialog.value.dismiss()
                    val list = ArrayList<Suit>()
                    //log("getMusclesApi onResponse: " + response.body())

                    response.body()?.data?.forEach {
                        it?.let { m ->
                            list.add(m)
                        }
                    }
                    log("getMusclesApi onResponse size: " + list.size)
                    updateSuits(list)

                    checkSession(response.body())
                }

            })
    }

    var listener = object : ViewPager.OnPageChangeListener {
        override fun onPageScrollStateChanged(state: Int) {

        }

        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {

        }

        override fun onPageSelected(position: Int) {
            updateText(position)
        }

    }

    var selectedSuit: Suit? = null
    private val allSuits = ArrayList<Suit>()
    private fun updateSuits(suits: ArrayList<Suit>) {
        allSuits.clear()
        allSuits.addAll(suits)
        log("updateSuits :: " + suits.size)
        val adapter = SliderAdapter(suits, context)
        tabDots?.setupWithViewPager(sliderViewPager)
        sliderViewPager.addOnPageChangeListener(listener)
        sliderViewPager.adapter = adapter;
        updateText(0)
    }

    private fun updateText(position: Int) {
        log("updateText1 $selectedSuit")
        log("updateText2 " + selectedSuit?.suitName)
        log("updateText3 " + selectedSuit?.suitDescription)
        if (position < allSuits.size) {
            selectedSuit = allSuits[position]
            selectedSuit?.let {
                tv_text1?.text = it.suitName
                tv_text2?.text = it.suitDescription
            }
        }

    }


    override fun onBackPressed(): Boolean {
        return super.onBackPressed()
    }

    override fun onNavBackPressed(): Boolean {
        return super.onNavBackPressed()
    }

}