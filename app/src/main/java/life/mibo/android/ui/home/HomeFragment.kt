package life.mibo.android.ui.home

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.gms.location.LocationServices
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_home_new.*
import life.mibo.android.R
import life.mibo.android.core.Prefs
import life.mibo.android.core.YahooWeather
import life.mibo.android.events.NotifyEvent
import life.mibo.android.models.login.Member
import life.mibo.android.ui.base.BaseFragment
import life.mibo.android.ui.base.BaseListener
import life.mibo.android.ui.base.ItemClickListener
import life.mibo.android.ui.main.MiboEvent
import life.mibo.android.ui.main.Navigator
import life.mibo.android.ui.main.Navigator.Companion.HOME_VIEW
import life.mibo.android.ui.main.RememberMeDialog
import life.mibo.android.utils.Toasty
import life.mibo.android.utils.Utils
import life.mibo.hardware.SessionManager
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class HomeFragment : BaseFragment(), HomeObserver {

    interface Listener : BaseListener {
        fun onHomeItemClicked(position: Int)
    }

    private lateinit var controller: HomeController
    private lateinit var homeViewModel: HomeViewModel
    var recyclerView: RecyclerView? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, bundle: Bundle?):
            View? {
        val root = inflater.inflate(R.layout.fragment_home_new, container, false)
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        // val textView: TextView = root.findViewById(R.id.text_home)
        homeViewModel.text.observe(viewLifecycleOwner, Observer {
            //  textView.text = it
        })

        log("onCreateView")
        //recyclerView = root.findViewById(R.id.hexagonRecycler) as HexagonRecyclerView
        recyclerView = root.findViewById(R.id.recyclerView)
        // setRecycler(recyclerView!!)
        //Crashlytics.getInstance().crash()
        retainInstance = true
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        log("onViewCreated")
        controller = HomeController(this@HomeFragment, this)
        //iv_dashboard_1.setGradient(intArrayOf(Color.LTGRAY, Color.GRAY, Color.DKGRAY))
        val member: Member? = Prefs.get(this.context)?.member
        tv_user_name.text = "${member?.firstName}  ${member?.lastName}"
        isMember = member?.isMember() ?: true
//        iv_user_pic.setImageDrawable(
//            ContextCompat.getDrawable(
//                this@HomeFragment.context!!,
//                R.drawable.ic_person_black_24dp
//            )
//        )
        controller.getDashboard(!member!!.isMember())
        navigate(HOME_VIEW, true)
        val format = SimpleDateFormat("EEE, dd MMM, yyyy")
        //controller.setRecycler(recyclerView!!)
        textView2?.text = format.format(Date())

        iv_user_pic?.setOnClickListener {
            navigate(Navigator.HOME, HomeItem(HomeItem.Type.PROFILE))
        }
        loadImage(iv_user_pic, R.drawable.ic_user_test, member?.profileImg)

        SessionManager.getInstance().userSession.isBooster = false;
        SessionManager.getInstance().userSession.isRxl = false;
        setBottomView()
        checkIntro()
        weather()
        if (!isMember)
            tv_item_fab?.setImageResource(R.drawable.ic_home_black_24dp)
    }

    fun checkBody() {

    }

    private var isMember = false
    fun setBottomView() {
        ib_item_1?.setOnClickListener {
            navigateTo(HomeItem(HomeItem.Type.PROFILE))
        }
        ib_item_2?.setOnClickListener {
            navigateTo(HomeItem(HomeItem.Type.MEASURE))
            // navigateTo(Navigator.BODY_MEASURE_SUMMARY, null)
            //drawerItemClicked(R.id.navigation_bio_summary)
        }
        ib_item_3?.setOnClickListener {
            navigateTo(HomeItem(HomeItem.Type.MY_ACCOUNT))
            //drawerItemClicked(R.id.navigation_account)
        }
        ib_item_4?.setOnClickListener {
            navigateTo(HomeItem(HomeItem.Type.SERVICES))
            //navigate(navigation_search_trainer)
        }
        tv_item_fab?.setOnClickListener {
            if (isMember)
                navigateTo(HomeItem(HomeItem.Type.CENTER_BUTTON))
        }
    }

    private fun checkIntro() {

        try {
            val pwd = Prefs.get(context).get("skip_pwd_")
            val prefs = Prefs.getEncrypted(context)
            val isLogin = prefs.get("login_enable", "false", true)
            if (!java.lang.Boolean.parseBoolean(isLogin) && !java.lang.Boolean.parseBoolean(pwd)) {
                RememberMeDialog().show(childFragmentManager, "RememberMeDialog")
                return
            }
        } catch (e: java.lang.Exception) {

        }

        val skip = Prefs.get(context).get("profile_skipped", false)
        if (skip)
            return
        val url = Prefs.get(this.context).member?.profileImg
        if (url.isNullOrEmpty() || !url.startsWith("http")) {
            Single.just("test").delay(3, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread())
                .doOnError {

                }.doOnSuccess {
                    //navigate(Navigator.BODY_MEASURE, null)
                    onItemClicked(HomeItem(HomeItem.Type.PROFILE_UPLOAD))
                }.subscribe()
            return
        }

//        val done = Prefs.getTemp(context).get("body_measure")?.toLowerCase()
//        if (done == "done" || done == "skip") {
//            return
//        }
//        Single.just("test").delay(3, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread())
//            .doOnError {
//
//            }.doOnSuccess {
//                //navigate(Navigator.BODY_MEASURE, null)
//                onItemClicked(HomeItem(HomeItem.Type.PROGRAMS))
//            }.subscribe()
    }

    private fun videoBg() {
        try {
            val uri =
                Uri.parse("android.resource://" + context?.packageName + "/" + R.raw.login_video)
//            videoView.setVideoURI(uri)
//            videoView.start()
//            videoView?.setOnPreparedListener {
//                it.isLooping = true
//            }
        } catch (e: Exception) {
            MiboEvent.log(e)
        }
    }


    fun weather(){
        log("weather fusedLocationClient--------------")
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location : Location? ->
                log("weather fusedLocationClient: $location")
               // YahooWeather.load(location?.latitude, location?.longitude)
                YahooWeather.OpenApiWeather(
                    context,
                    location?.latitude,
                    location?.longitude,
                    object : ItemClickListener<String> {
                        override fun onItemClicked(item: String?, position: Int) {
                            log("onItemClicked $item")
                            activity?.runOnUiThread {
                                adapter?.updateWeather(item)
                            }
                        }

                    })

            }
    }

    private fun loadImage(iv: ImageView?, defaultImage: Int, url: String?) {
        if (url == null) {
            if (iv != null)
                Glide.with(this).load(defaultImage).error(defaultImage).fallback(defaultImage)
                    .into(iv)
            return
        }
        url?.let {
            if (iv != null)
                Glide.with(this).load(it).error(defaultImage).fallback(defaultImage).into(iv)
        }
    }
    private fun loadImage(iv: ImageView, defaultImage: Int) {
        Maybe.fromCallable {
            log("loadImage fromCallable")
            var bitmap: Bitmap? = null
            val img = Prefs.get(this.context).member?.imageThumbnail
            bitmap = if (!img.isNullOrEmpty())
                Utils.base64ToBitmap(img)
            else
                BitmapFactory.decodeResource(resources, defaultImage)
            // else
            //   bitmap = Utils.base64ToBitmap(Utils.testUserImage())
            bitmap
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).doOnSuccess {
            log("loadImage doOnSuccess $it")
            if (it != null)
                iv.setImageBitmap(it)
            else
                iv.setImageResource(defaultImage)
        }.doOnError {

        }.subscribe()
    }

    override fun onDataReceived(list: ArrayList<HomeItem>) {
        log("onDataReceived $list")
        getDialog()?.dismiss()

//        list.forEachIndexed { i, item ->
//            when (i) {
//                0 -> {
//                    constraintLayout2.visibility = View.VISIBLE
//                    iv_dashboard_item_1.visibility = View.VISIBLE
//                    item.bind(iv_dashboard_item_1, this)
//                }
//                1 -> {
//                    log("onDataReceived when 2")
//                    iv_dashboard_item_2.visibility = View.VISIBLE
//                    item.bind(iv_dashboard_item_2, this)
//                    iv_dashboard_item_2.visibility = View.VISIBLE
//                }
//                2 -> {
//                    iv_dashboard_item_3.visibility = View.VISIBLE
//                    item.bind(iv_dashboard_item_3, this)
//                }
//                3 -> {
//                    constraintLayout3.visibility = View.VISIBLE
//                    iv_dashboard_item_4.visibility = View.VISIBLE
//                    item.bind(iv_dashboard_item_4, this)
//                }
//                4 -> {
//                    iv_dashboard_item_5.visibility = View.VISIBLE
//                    item.bind(iv_dashboard_item_5, this)
//                }
//                5 -> {
//                    constraintLayout4.visibility = View.VISIBLE
//                    iv_dashboard_item_6.visibility = View.VISIBLE
//                    item.bind(iv_dashboard_item_6, this)
//                }
//                6 -> {
//                    iv_dashboard_item_7.visibility = View.VISIBLE
//                    item.bind(iv_dashboard_item_7, this)
//                }
//                7 -> {
//                    constraintLayout5.visibility = View.VISIBLE
//                    iv_dashboard_item_8.visibility = View.VISIBLE
//                    iv_dashboard_item_9.visibility = View.INVISIBLE
//                    iv_dashboard_item_10.visibility = View.INVISIBLE
//                    item.bind(iv_dashboard_item_8, this)
//                }
//                8 -> {
//                    item.bind(iv_dashboard_item_9, this)
//                    iv_dashboard_item_9.visibility = View.VISIBLE
//                }
//                9 -> {
//                    item.bind(iv_dashboard_item_10, this)
//                    iv_dashboard_item_10.visibility = View.VISIBLE
//                }
//            }
//        }
//        log("onDataReceived when again 2 "+iv_dashboard_item_2.visibility)
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

        updateData(list)
    }

    fun navigateTo(item: HomeItem?) {
        navigate(Navigator.HOME, item)
    }

    override fun onItemClicked(item: HomeItem?) {
        navigateTo(item)
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

    override fun onNotify(type: Int, data: Any?) {

    }

    val data = ArrayList<Array<HomeItem>>()
    var adapter: HomeAdapter? = null

    private fun updateData(list: ArrayList<HomeItem>?) {
        //log("updateData $list")
        if (list == null)
            return
        // var odd = true
        // var size = list.size
        data.clear()
        //TODO later i will do it dynamic list mapping, now skip
//        list.forEachIndexed { i, item ->
//            if (odd) {
//                if (i + 1 < size)
//                    data.add(arrayOf(list[i], list[i + 1]))
//                else
//                    data.add(arrayOf(list[i], list[i + 1]))
//                odd = false
//            } else {
//                if (i + 1 < size)
//                    data.add(arrayOf(list[i], list[i + 1]))
//                else
//                    data.add(arrayOf(list[i], list[i + 1]))
//
//                odd = true
//            }
//        }

        val size = list.size
        if (size > 1)
            data.add(arrayOf(list[0], list[1]))
        when {
            size > 4 -> data.add(arrayOf(list[2], list[3], list[4]))
            size > 3 -> data.add(arrayOf(list[2], list[3]))
            size > 2 -> data.add(arrayOf(list[2]))
        }
        when {
            size > 6 -> data.add(arrayOf(list[5], list[6]))
            size > 5 -> data.add(arrayOf(list[5]))
        }
        when {
            size > 9 -> data.add(arrayOf(list[7], list[8], list[9]))
            size > 8 -> data.add(arrayOf(list[7], list[8]))
            size > 7 -> data.add(arrayOf(list[7]))
        }
        //data.add(arrayOf(list[5], list[6]))
        //data.add(arrayOf(list[7], list[8]))
        val metrics = resources.displayMetrics
        setRecyclerView(metrics!!.widthPixels.div(3))
//        if (size > 6) {
//
//        }
//
//        if (size > 6) {
//            data.add(arrayOf(list[0], list[1]))
//            data.add(arrayOf(list[2], list[3], list[4]))
//            data.add(arrayOf(list[5], list[6]))
//            data.add(arrayOf(list[7], list[8]))
//            val metrics = resources.displayMetrics
//            setRecyclerView(metrics!!.widthPixels.div(3))
//        } else if (size > 3) {
//            data.add(arrayOf(list[0], list[1]))
//            if (size > 4)
//                data.add(arrayOf(list[2], list[3], list[4]))
//            else if (size > 3)
//                data.add(arrayOf(list[2], list[3]))
//            val metrics = resources.displayMetrics
//            setRecyclerView(metrics!!.widthPixels.div(3))
//        }
    }

    private fun setRecyclerView(width: Int) {
        log("setRecyclerView $width")
        if (width == 0) {
            Toasty.info(
                requireContext(),
                "Oops device width is zero (0)",
                Toasty.LENGTH_SHORT,
                false
            )
                .show()
        }
        adapter = HomeAdapter(data, width)
        val size = data.size - 1
        //val bottom = Utils.dpToPixel(-26, this.context)
        val bottom = -width.div(5)
        recyclerView?.layoutManager = LinearLayoutManager(context)
        recyclerView?.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                rect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
            ) {
                val pos = parent.getChildAdapterPosition(view)
                if (pos == size) {
                    //rect.set(0, 0, 0, 0)
                } else
                    rect.set(0, 0, 0, bottom)

                log(" getItemOffsets2: r=$rect s=$width p=$pos")
                //r=Rect(0, 0 - 0, -68) s=360
            }

        })
        adapter!!.setListener(object : ItemClickListener<HomeItem> {
            override fun onItemClicked(item: HomeItem?, position: Int) {
                onItemClicked(item)
            }
        })
        recyclerView?.adapter = adapter
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

        //val adapter = HomeAdapter(list, size)
        val items = list.size - 1
        val bottom = Utils.dpToPixel(-26, this.context)
        recyclerView?.layoutManager = LinearLayoutManager(context)
        recyclerView?.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                rect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
            ) {
                val pos = parent.getChildAdapterPosition(view)
                if (pos == items) {
                    //rect.set(0, 0, 0, 0)
                } else
                    rect.set(0, 0, 0, bottom)
                //rect.top = rect.top.minus(50)
                //view.translationY = -50f
                //super.getItemOffsets(rect, view, parent, state)
                //val smallRow_padding_top_bottom = parent.width / (mRowSize * 2);

                log(" getItemOffsets2: r=$rect s=$size p=$pos")
            }


        })
        //recyclerView.addItemDecoration(VerticalOverlapDecorator(2, 10f, 10f))
        //recyclerView.addItemDecoration(HorizontalOverlapDecorator(2, 10f, 10f))
        //recyclerView.adapter = adapter
    }


    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onPostEvent(event: NotifyEvent) {
        if (event.id == Navigator.HOME) {
            requireView().forceLayout()
            requireView().requestLayout()
            parent_constraint.invalidate()
            log("onPostEvent $event")
            EventBus.getDefault().removeStickyEvent(event)
        }
    }

    var isStoped = false
    override fun onResume() {
        log("onResume")
        super.onResume()

        if (isStoped && recyclerView?.adapter == null) {
            if (!::controller.isInitialized)
                controller = HomeController(this@HomeFragment, this)

            controller?.getDashboard()
        }
        isStoped = false
        // videoView?.resume()
        //videoBg()
    }

    override fun onStart() {
        log("onStart")
        super.onStart()
        EventBus.getDefault().register(this)
    }
    override fun onStop() {
        log("onStop")
        //videoView?.stopPlayback()
        isStoped = true
        super.onStop()
        //controller.onStop()
        //navigate(HOME_VIEW, false)
        EventBus.getDefault().unregister(this)
    }

    override fun onDestroy() {
        recyclerView?.adapter = null
        super.onDestroy()
    }
}