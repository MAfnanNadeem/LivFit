/*
 *  Created by Sumeet Kumar on 4/20/20 10:04 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 4/16/20 2:23 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.body_measure

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_body_type.*
import life.mibo.android.R
import life.mibo.android.core.Prefs
import life.mibo.android.ui.body_measure.adapter.BodyAdapter
import life.mibo.android.ui.body_measure.adapter.BodyBaseFragment
import life.mibo.android.ui.body_measure.adapter.Calculate
import kotlin.math.abs

class BodyTypeFragment : BodyBaseFragment() {


    fun create(type: Int): BodyTypeFragment {
        val frg = BodyTypeFragment()
        val arg = Bundle()
        arg.putInt("gender_type", type)
        frg.arguments = arg
        return frg
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, bundle: Bundle?):
            View? {
        return inflater.inflate(R.layout.fragment_body_type, container, false)
    }

    var adapter: BodyAdapter? = null
    private var viewModel: MeasureViewModel? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //adapter = BodyAdapter(ArrayList<BodyAdapter.Item>(), 0)
        //adapter = BodyAdapter(ArrayList<BodyAdapter.Item>(), 0)
//        viewModel = activity?.run {
//            ViewModelProviders.of(this@MeasurementFragment)[MeasureViewModel::class.java]
//        }

        viewModel = ViewModelProvider(this@BodyTypeFragment).get(MeasureViewModel::class.java)

        //viewPager.adapter = PageAdapter(getPages(), activity!!.supportFragmentManager, lifecycle)
        //viewPager?.setPagingEnabled(false)

        // viewPager.isUserInputEnabled = false;
        //viewPager?.setPageTransformer(PageTransformation())
        //tabLayout.setupWithViewPager(viewPager2)
        //controller.setRecycler(recyclerView)

        tv_skip?.setOnClickListener {
            viewPager?.currentItem = --frg
        }

        imageNext?.setOnClickListener {
            nextClicked()
        }

        viewPager?.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                frg = position.plus(1)
                // updateButton()
            }

        })
        setHasOptionsMenu(false)

        imageNext?.visibility = View.GONE
        tv_continue?.visibility = View.GONE
    }

    override fun onResume() {
        super.onResume()
        setup()
        updateNextButton(true)
        updateSkipButton(true)
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        log("setUserVisibleHint $isVisibleToUser")
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            setup()
            updateNextButton(true)
            updateSkipButton(true)
        }
    }


    var calculate: Boolean = false

    private fun setup() {
        //val member = Prefs(context).member
        // val male = member?.gender?.equals("male", true) ?: false
        val member = Prefs(context).member
        val male = member?.gender.equals("male", true) ?: false

        if (male) {
            header?.setImageResource(R.drawable.bg_body_header_male)
            header_bottom?.setImageResource(R.drawable.bg_body_header_male_bottom)
        } else {
            header?.setImageResource(R.drawable.bg_body_header_female)
            header_bottom?.setImageResource(R.drawable.bg_body_header_female_bottom)
        }

        val data = Calculate.getMeasureData()
        val chest = data.getMeasurement(1)
        val waist = data.getMeasurement(2)
        val hips = data.getMeasurement(3)
        val highHips = data.getMeasurement(4)
        val types = Calculate.calculateShape(chest, waist, hips, highHips)

        viewPager?.offscreenPageLimit = 1
        viewPager.adapter =
            PageAdapter(getDefaultPages(male, types), childFragmentManager, lifecycle)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            //tab.text = "OBJECT ${(position + 1)}"
            tab.text = ""
        }.attach()

//        Toasty.info(
//            requireContext(),
//            Calculate.getData().getString("shape_msg", "--"),
//            Toasty.LENGTH_LONG, false
//        ).show()

        if (calculate)
            title_text?.setText(R.string.your_body_type)
        else
            title_text?.setText(R.string.choose_your_body_type)

        Prefs.get(context).setJson("shape_types", types)

        viewPager?.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                Calculate.bodyShapePage = position.plus(1)

            }
        })
        //viewPager?.registerOnPageChangeCallback()
    }

    @Synchronized
    fun updateNext(enable: Boolean) {
        log("parentFragment updateNext $enable")
        if (enable) {
            imageNext.visibility = View.VISIBLE
            tv_continue.visibility = View.VISIBLE
        } else {
            tv_continue.visibility = View.INVISIBLE
            imageNext.visibility = View.INVISIBLE
        }
    }

    @Synchronized
    fun updateSkip(enable: Boolean) {
        log("parentFragment updateNext $enable")
        if (enable) {
            tv_skip.visibility = View.VISIBLE
        } else {
            tv_skip.visibility = View.INVISIBLE
        }
    }


    private fun getPages(): ArrayList<Fragment> {

        val member = Prefs(context).member
        val male = member?.gender?.equals("male", true) ?: false
        val list = ArrayList<Fragment>()
        //list.add(BodyTypeImageFragment.create(if (male) 1 else 2,))
        return list
    }


    private fun getDefaultPages(male: Boolean, types: ArrayList<Int>): ArrayList<Fragment> {
        //val member = Prefs(context).member
        //val male = member?.gender?.equals("male", true) ?: false
        val list = ArrayList<Fragment>()

        if (types.size > 0) {
            var isXAdded = false
            for (type in types) {
                when (type) {
                    1 -> {
                        if (!isXAdded) {
                            list.add(
                                BodyTypeImageFragment.create(male, BodyTypeImageFragment.TYPE_X)
                            )
                            isXAdded = true
                        }
                        Calculate.getMeasureData().shapeType = "X Type Hourglass"
                    }
                    2 -> {
                        if (!isXAdded) {
                            list.add(
                                BodyTypeImageFragment.create(male, BodyTypeImageFragment.TYPE_X)
                            )
                            isXAdded = true
                        }
                        Calculate.getMeasureData().shapeType = "X Type Bottom Hourglass"
                    }
                    3 -> {
                        if (!isXAdded) {
                            list.add(
                                BodyTypeImageFragment.create(male, BodyTypeImageFragment.TYPE_X)
                            )
                            isXAdded = true
                        }
                        Calculate.getMeasureData().shapeType = "X Type Top Hourglass"
                    }
                    4 -> {
                        list.add(
                            BodyTypeImageFragment.create(male, BodyTypeImageFragment.TYPE_O)
                        )
                        Calculate.getMeasureData().shapeType = "O Type - Apple"
                    }
                    5 -> {
                        list.add(
                            BodyTypeImageFragment.create(male, BodyTypeImageFragment.TYPE_A)
                        )
                        Calculate.getMeasureData().shapeType = "A Type - Triangle / pear"
                    }
                    6 -> {
                        list.add(
                            BodyTypeImageFragment.create(male, BodyTypeImageFragment.TYPE_V)
                        )
                        Calculate.getMeasureData().shapeType = "V Type - Inverted triangle"
                    }
                    7 -> {
                        list.add(
                            BodyTypeImageFragment.create(male, BodyTypeImageFragment.TYPE_I)
                        )
                        Calculate.getMeasureData().shapeType = "I Type - Rectangle"
                    }
                }
            }
            calculate = true
            if (list.size > 0)
                return list

        }


        if (male) {
            list.add(
                BodyTypeImageFragment.create(
                    1,
                    R.drawable.ic_body_male_a,
                    R.string.choose_your_body_type,
                    R.string.body_type_a
                )
            )
            list.add(
                BodyTypeImageFragment.create(
                    1,
                    R.drawable.ic_body_male_v,
                    R.string.choose_your_body_type,
                    R.string.body_type_v
                )
            )
            list.add(
                BodyTypeImageFragment.create(
                    1,
                    R.drawable.ic_body_male_o,
                    R.string.choose_your_body_type,
                    R.string.body_type_o
                )
            )
            list.add(
                BodyTypeImageFragment.create(
                    1,
                    R.drawable.ic_body_male_i,
                    R.string.choose_your_body_type,
                    R.string.body_type_i
                )
            )
            list.add(
                BodyTypeImageFragment.create(
                    1,
                    R.drawable.ic_body_male_x,
                    R.string.choose_your_body_type,
                    R.string.body_type_x
                )
            )
        } else {
            //FEMALE
            list.add(
                BodyTypeImageFragment.create(
                    2,
                    R.drawable.ic_body_female_a,
                    R.string.choose_your_body_type,
                    R.string.body_type_a
                )
            )
            list.add(
                BodyTypeImageFragment.create(
                    2,
                    R.drawable.ic_body_female_v,
                    R.string.choose_your_body_type,
                    R.string.body_type_v
                )
            )
            list.add(
                BodyTypeImageFragment.create(
                    2,
                    R.drawable.ic_body_female_o,
                    R.string.choose_your_body_type,
                    R.string.body_type_o
                )
            )
            list.add(
                BodyTypeImageFragment.create(
                    2,
                    R.drawable.ic_body_female_i,
                    R.string.choose_your_body_type,
                    R.string.body_type_i
                )
            )
            list.add(
                BodyTypeImageFragment.create(
                    2,
                    R.drawable.ic_body_female_x,
                    R.string.choose_your_body_type,
                    R.string.body_type_x
                )
            )
        }
        return list
    }


    var frg = 0;
    var lastFrg = 0;
    private fun nextClicked() {
        // startActivity(Intent(activity, TestActivity::class.java))
        viewPager?.currentItem = frg++
    }

    // for ViewPager2
    class PageAdapter(val list: ArrayList<Fragment>, fa: FragmentManager, lifecycle: Lifecycle) :
        FragmentStateAdapter(fa, lifecycle) {
        //constructorfm: FragmentManager) : this(fm)
        override fun getItemCount(): Int = list.size

        override fun createFragment(position: Int): Fragment {
            if (position < itemCount)
                return list[position]
            return BodyTypeImageFragment()
        }
    }

    class PageTransformation : ViewPager2.PageTransformer {
        override fun transformPage(page: View, position: Float) {
            val absPos = abs(position)
            page.apply {
                translationY = absPos * 500f
                translationX = absPos * 500f
                scaleX = 1f
                scaleY = 1f
            }
            when {
                position < -1 ->
                    page.alpha = 0.1f
                position <= 1 -> {
                    page.alpha = Math.max(0.2f, 1 - Math.abs(position))
                }
                else -> page.alpha = 0.1f
            }
        }
    }
}