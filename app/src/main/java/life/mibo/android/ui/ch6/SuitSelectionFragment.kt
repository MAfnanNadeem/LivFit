/*
 *  Created by Sumeet Kumar on 4/5/20 9:10 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 3/24/20 2:42 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.ch6

//import life.mibo.android.ui.ch6.adapter.SliderAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import coil.api.load
import kotlinx.android.synthetic.main.fragment_select_suit.*
import life.mibo.android.R
import life.mibo.android.core.API
import life.mibo.android.core.Prefs
import life.mibo.android.models.muscle.GetSuitPost
import life.mibo.android.models.muscle.GetSuits
import life.mibo.android.models.muscle.Suit
import life.mibo.android.ui.base.BaseFragment
import life.mibo.android.ui.ch6.adapter.SliderAdapter
import life.mibo.android.ui.dialog.MyDialog
import life.mibo.android.ui.main.Navigator
import life.mibo.hardware.core.Logger
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

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
        activity?.runOnUiThread {
            allSuits.clear()
            allSuits.addAll(suits)
            log("updateSuits :: " + suits.size)
            val adapter = SliderAdapter(suits, context)
            tabDots?.setupWithViewPager(sliderViewPager)
            sliderViewPager.addOnPageChangeListener(listener)
            sliderViewPager.adapter = adapter;
            updateText(0)
            adapter.notifyDataSetChanged()
            button_next?.isEnabled = true
        }
    }

    private fun updateText(position: Int) {
        log("updateText1 $selectedSuit")
        log("updateText2 " + selectedSuit?.name)
        log("updateText3 " + selectedSuit?.description)
        if (position < allSuits.size) {
            selectedSuit = allSuits[position]
            selectedSuit?.let {
                tv_text1?.text = it.name
                tv_text2?.text = it.description
            }
        }

    }


    override fun onBackPressed(): Boolean {
        return super.onBackPressed()
    }

    override fun onNavBackPressed(): Boolean {
        return super.onNavBackPressed()
    }

    class SlideAdapter(var list: ArrayList<Suit>, manager: FragmentManager) :
        FragmentStatePagerAdapter(manager) {

        override fun getItem(position: Int): Fragment {
            return SuitFragment.create(list[position])
        }

        override fun isViewFromObject(view: View, any: Any): Boolean {
            return Objects.equals(view, any)
        }

        override fun getCount(): Int {
            return list.size
        }

    }

    class SuitFragment() : BaseFragment() {

        companion object {
            fun create(suit: Suit): SuitFragment {
                Logger.e("SuitFragment Image create")
                var frg = SuitFragment()
                val bundle = Bundle()
                bundle.putSerializable("frg_suit", suit)
                frg.arguments = bundle
                return frg
            }
        }

        var imageView: ImageView? = null
        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            log("Image :: onCreateView")
            val view = inflater.inflate(R.layout.fragment_select_suit_image, container, false)
            imageView = view.findViewById(R.id.imageView)
            return view

        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            ;
            log("Image :: onViewCreated")
            val suit = arguments?.getSerializable("frg_suit")
            if (suit is Suit) {
                imageView!!.load(suit.image)
                log("Image :: loaded " + suit.image)
            }

        }
    }

}