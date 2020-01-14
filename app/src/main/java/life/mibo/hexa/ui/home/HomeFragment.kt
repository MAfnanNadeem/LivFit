package life.mibo.hexa.ui.home

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_home.*
import life.mibo.hexa.R
import life.mibo.hexa.core.Prefs
import life.mibo.hexa.events.NotifyEvent
import life.mibo.hexa.models.login.Member
import life.mibo.hexa.ui.base.BaseFragment
import life.mibo.hexa.ui.base.BaseListener
import life.mibo.hexa.ui.main.Navigator
import life.mibo.hexa.ui.main.Navigator.Companion.HOME_VIEW
import life.mibo.hexa.utils.Utils
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : BaseFragment(), HomeObserver {

    interface Listener : BaseListener {
        fun onHomeItemClicked(position: Int)
    }

    private lateinit var controller: HomeController
    private lateinit var homeViewModel: HomeViewModel
    //var recyclerView: RecyclerView? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, bundle: Bundle?):
            View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        // val textView: TextView = root.findViewById(R.id.text_home)
        homeViewModel.text.observe(this, Observer {
            //  textView.text = it
        })

        log("onCreateView")
        //recyclerView = root.findViewById(R.id.hexagonRecycler) as HexagonRecyclerView
        //recyclerView = root.findViewById(R.id.hexagonRecycler)
        // setRecycler(recyclerView!!)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        log("onViewCreated")
        controller = HomeController(this@HomeFragment, this)
        //iv_dashboard_1.setGradient(intArrayOf(Color.LTGRAY, Color.GRAY, Color.DKGRAY))
        val member: Member? = Prefs.get(this.context)?.getMember(Member::class.java)
        tv_user_name.text = "${member?.firstName}  ${member?.lastName}"
        iv_user_pic.setImageDrawable(
            ContextCompat.getDrawable(
                this@HomeFragment.context!!,
                R.drawable.ic_person_black_24dp
            )
        )
        controller.getDashboard()
        navigate(HOME_VIEW, true)
        val format = SimpleDateFormat("EEE, dd MMM, yyyy")
        //controller.setRecycler(recyclerView!!)
        textView2?.text = format.format(Date())
    }

    override fun onDataRecieved(list: ArrayList<HomeItem>) {
        log("onDataReceived $list")
        getDialog()?.dismiss()
        list.forEachIndexed { i, item ->
            when (i) {
                0 -> {
                    constraintLayout2.visibility = View.VISIBLE
                    iv_dashboard_item_1.visibility = View.VISIBLE
                    item.bind(iv_dashboard_item_1, this)
                }
                1 -> {
                    log("onDataReceived when 2")
                    iv_dashboard_item_2.visibility = View.VISIBLE
                    item.bind(iv_dashboard_item_2, this)
                    iv_dashboard_item_2.visibility = View.VISIBLE
                }
                2 -> {
                    iv_dashboard_item_3.visibility = View.VISIBLE
                    item.bind(iv_dashboard_item_3, this)
                }
                3 -> {
                    constraintLayout3.visibility = View.VISIBLE
                    iv_dashboard_item_4.visibility = View.VISIBLE
                    item.bind(iv_dashboard_item_4, this)
                }
                4 -> {
                    iv_dashboard_item_5.visibility = View.VISIBLE
                    item.bind(iv_dashboard_item_5, this)
                }
                5 -> {
                    constraintLayout4.visibility = View.VISIBLE
                    iv_dashboard_item_6.visibility = View.VISIBLE
                    item.bind(iv_dashboard_item_6, this)
                }
                6 -> {
                    iv_dashboard_item_7.visibility = View.VISIBLE
                    item.bind(iv_dashboard_item_7, this)
                }
                7 -> {
                    constraintLayout5.visibility = View.VISIBLE
                    iv_dashboard_item_8.visibility = View.VISIBLE
                    iv_dashboard_item_9.visibility = View.INVISIBLE
                    iv_dashboard_item_10.visibility = View.INVISIBLE
                    item.bind(iv_dashboard_item_8, this)
                }
                8 -> {
                    item.bind(iv_dashboard_item_9, this)
                    iv_dashboard_item_9.visibility = View.VISIBLE
                }
                9 -> {
                    item.bind(iv_dashboard_item_10, this)
                    iv_dashboard_item_10.visibility = View.VISIBLE
                }
            }
        }
        log("onDataReceived when again 2 "+iv_dashboard_item_2.visibility)
//        log("onDataReceived when again 2 "+iv_dashboard_item_10.visibility)
//        constraintLayout2.visibility = View.VISIBLE
//        iv_dashboard_item_2.visibility = View.VISIBLE
//
//        val metrics = resources.displayMetrics
//        val w = metrics.widthPixels
//
//        //val m = DisplayMetrics()
//        //activity!!.windowManager.defaultDisplay.getMetrics(m)
//
//        Toasty.info(this@HomeFragment.context!!, "DPI ${metrics.density} - ${metrics.density.times(160f)}  $w ").show()
//
//        constraintLayout2.invalidate()
//        parent_constraint.invalidate()
//        //(activity as MainActivity).supportFragmentManager!!.beginTransaction().detach(this).attach(this).commit()
//        //(activity as MainActivity).navController?.
        val metrics = resources.displayMetrics
        testHexa(recyclerView.width.div(3).toInt())
    }


    override fun onItemClicked(item: HomeItem?) {
        navigate(Navigator.HOME, item)
//        when (item?.type) {
//            HomeItem.Type.HEART -> {
//
//            }
//            HomeItem.Type.WEIGHT -> {
//
//            }
//            HomeItem.Type.ADD -> {
//
//            }
//            HomeItem.Type.CALENDAR -> {
//
//            }
//            HomeItem.Type.SCHEDULE -> {
//
//            }
//            else -> {
//                Toasty.warning(context!!, "ItemClicked $item").show()
//            }
//        }
    }

    //TODO
    // this is dynamic hexagon recyclerView for dashboard, due to short time I will implement in future, align translation x-y accordingly
    //usage: testHexa(resources.displayMetrics.metrics.widthPixels.div(3))
    fun testHexa(size: Int) {
        val list = ArrayList<HomeItem>();
        for (i in 1..20
        ) {
            list.add(HomeItem(0, "$i"))
        }
        val adapter = HomeAdapter(list, size)
        val size = list.size - 1
        val bottom = Utils.dpToPixel(-26, this.context)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                rect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
            ) {
                val pos = parent.getChildAdapterPosition(view)
                if (pos == size) {
                    rect.set(0, 0, 0, 0)
                } else
                    rect.set(0, 0, 0, bottom)
                //rect.top = rect.top.minus(50)
                //view.translationY = -50f
                //super.getItemOffsets(rect, view, parent, state)
                //val smallRow_padding_top_bottom = parent.width / (mRowSize * 2);

                log(" getItemOffsets2: $rect")
            }


        })
        //recyclerView.addItemDecoration(VerticalOverlapDecorator(2, 10f, 10f))
        //recyclerView.addItemDecoration(HorizontalOverlapDecorator(2, 10f, 10f))
        recyclerView.adapter = adapter
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onPostEvent(event: NotifyEvent) {
        if (event.id == Navigator.HOME) {
            view!!.forceLayout()
            view!!.requestLayout()
            parent_constraint.invalidate()
            log("onPostEvent $event")
            EventBus.getDefault().removeStickyEvent(event)
        }
    }

    override fun onStop() {
        log("onStop")
        super.onStop()
        controller.onStop()
        navigate(HOME_VIEW, false)
        EventBus.getDefault().unregister(this)
    }

    override fun onStart() {
        log("onStart")
        super.onStart()
        EventBus.getDefault().register(this)
    }

}