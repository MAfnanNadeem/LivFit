/*
 *  Created by Sumeet Kumar on 4/14/20 10:16 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 4/14/20 10:16 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.body_measure

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import kotlinx.android.synthetic.main.fragment_body_measurement.*
import life.mibo.android.R
import life.mibo.android.core.Prefs
import life.mibo.android.ui.base.BaseFragment
import life.mibo.android.ui.body_measure.adapter.BodyAdapter

class MeasurementFragment : BaseFragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, bundle: Bundle?):
            View? {
        return inflater.inflate(R.layout.fragment_body_measurement, container, false)
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

        viewModel = ViewModelProvider(this@MeasurementFragment).get(MeasureViewModel::class.java)

        //viewPager.adapter = PageAdapter(getPages(), activity!!.supportFragmentManager, lifecycle)
        viewPager?.setPagingEnabled(false)
        viewPager?.offscreenPageLimit = 1
        viewPager.adapter = PagerAdapter(getPages(), childFragmentManager)
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
        viewPager?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
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
            }

        })
//        viewPager?.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
//            override fun onPageScrollStateChanged(state: Int) {
//
//            }
//
//            override fun onPageScrolled(
//                position: Int,
//                positionOffset: Float,
//                positionOffsetPixels: Int
//            ) {
//
//            }
//
//            override fun onPageSelected(position: Int) {
//                frg = position.plus(1)
//                // updateButton()
//            }
//
//        })
        setHasOptionsMenu(false)
//        viewModel!!.nextButton.observe(this@MeasurementFragment, Observer { select ->
//            log("viewModel nextButton $select")
//            if (select) {
//                imageView3?.visibility = View.VISIBLE
//                tv_continue?.visibility = View.VISIBLE
//            } else {
//                tv_continue?.visibility = View.INVISIBLE
//                imageView3?.visibility = View.INVISIBLE
//            }
//        })
    }


    override fun onAttachFragment(childFragment: Fragment) {
        super.onAttachFragment(childFragment)
        log("onAttachFragment $childFragment")
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
        val male = member?.gender.equals("male", true) ?: false
        val list = ArrayList<Fragment>()
        //list.add(SummaryFragment())
        //list.add(QuestionFragment.create(1))
        //list.add(QuestionFragment.create(2))
        list.add(ProfilePicFragment.create(1))
        list.add(BMIFragment.create(if (male) 1 else 2))
        //list.add(BMIFragment.create(if (!male) 1 else 2))
        list.add(MeasureBodyFragment.create(male))
        //list.add(MeasureFragment.create(!male))
        list.add(BodyTypeFragment())
        list.add(QuestionFragment.create(1))
        list.add(QuestionFragment.create(2))
        list.add(SummaryFragment())
        list.add(QuestionFragment())
        list.add(QuestionFragment())
        list.add(QuestionFragment())
        list.add(QuestionFragment())
        return list
    }

    var frg = 1;
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
            return MeasurementFragment()
        }
    }

    class PagerAdapter(val list: ArrayList<Fragment>, fa: FragmentManager) :
        FragmentPagerAdapter(fa) {
        //constructorfm: FragmentManager) : this(fm)
        fun getItemCount(): Int = list.size

        fun createFragment(position: Int): Fragment {
            if (position < getItemCount())
                return list[position]
            return MeasurementFragment()
        }

        override fun getItem(position: Int): Fragment {
            if (position < count)
                return list[position]
            return MeasurementFragment()
        }

        override fun getCount(): Int {
            return list.size
        }
    }

    class PageFragment : BaseFragment() {
        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, bundle: Bundle?):
                View? {
            return inflater.inflate(R.layout.fragment_body_measure, container, false)
        }
    }

    class PageTransformation : ViewPager2.PageTransformer {
        override fun transformPage(page: View, position: Float) {
            val absPos = Math.abs(position)
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

    override fun onStart() {
        super.onStart()
        val a = activity
        if(a is AppCompatActivity)
            a.supportActionBar?.hide()
    }

    override fun onStop() {
        val a = activity
        if(a is AppCompatActivity)
            a.supportActionBar?.show()
        super.onStop()
    }
}