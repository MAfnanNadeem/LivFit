package life.mibo.android.ui.home

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Rect
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.Field
import com.google.android.gms.location.LocationServices
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
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
import life.mibo.android.utils.Toasty
import life.mibo.android.utils.Utils
import life.mibo.hardware.SessionManager
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
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
       // navigate(HOME_VIEW, true)
        //val format = SimpleDateFormat("EEE, dd MMM, yyyy")
        //controller.setRecycler(recyclerView!!)
        //textView2?.text = format.format(Date())
        textView2?.visibility = View.INVISIBLE

        iv_user_pic?.setOnClickListener {
            navigate(Navigator.HOME, HomeItem(HomeItem.Type.PROFILE))
        }

        loadImage(iv_user_pic, member.profileImg, member.isMale())

        SessionManager.getInstance().userSession.isBooster = false;
        SessionManager.getInstance().userSession.isRxl = false;
        //setBottomView()
        checkIntro()
        loadDashboardData()
        setHasOptionsMenu(true)
        // if (!isMember)
        //    tv_item_fab?.setImageResource(R.drawable.ic_home_black_24dp)
    }

    fun testAnim() {
        getDialog()?.show()
        Single.just("").delay(5, TimeUnit.SECONDS).doOnSuccess {
            getDialog()?.dismiss()
        }.subscribe()
    }

    private var isMember = false

    private fun checkIntro() {

//        try {
//            val pwd = Prefs.get(context).get("skip_pwd_")
//            val prefs = Prefs.getEncrypted(context)
//            val isLogin = prefs.get("login_enable", "false", true)
//            if (!java.lang.Boolean.parseBoolean(isLogin) && !java.lang.Boolean.parseBoolean(pwd)) {
//                RememberMeDialog().show(childFragmentManager, "RememberMeDialog")
//                return
//            }
//        } catch (e: java.lang.Exception) {
//
//        }

        val prefs = Prefs.getEncrypted(context)
        prefs?.initCipher()
        val skip = prefs.get("profile_skipped", "false", true)
        if (java.lang.Boolean.parseBoolean(skip))
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
//        val m = android.net.MacAddress()
//        val w =WifiManager.MA

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

    private fun loadDashboardData() {
        if (isMember) {
            controller.getCalories()
            getWeather()
            getDailySteps()
        } else {
            getWeather()
            getDailySteps()
        }
    }

    private fun getWeather() {
        try {
            log("weather fusedLocationClient--------------")
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            val fusedLocationClient =
                LocationServices.getFusedLocationProviderClient(requireActivity())
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
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
        } catch (e: Exception) {

        }

    }

    private fun getDailySteps() {
        try {
            val fit = FitnessOptions.builder()
                .addDataType(DataType.TYPE_STEP_COUNT_CUMULATIVE)
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA)
                .build()
            val google = GoogleSignIn.getLastSignedInAccount(requireContext())
            if (GoogleSignIn.hasPermissions(google, fit)) {
                Fitness.getHistoryClient(requireContext(), google!!)
                    .readDailyTotal(DataType.TYPE_STEP_COUNT_DELTA)
                    .addOnSuccessListener { dataSet ->
                        log("getDailySteps addOnSuccessListener dataSet $dataSet")
                        if (!dataSet.isEmpty) {
                            val pt = dataSet.dataPoints[0]
                            log("getDailySteps addOnSuccessListener dataPoints $pt")
                            val total = pt.getValue(Field.FIELD_STEPS).asInt()
                            if (total > 0) {
                                activity?.runOnUiThread {
                                    adapter?.updateSteps(total)
                                }

                            }
                        }

                        //Log.i(TAG, "Total steps: $total")
                    }
                    .addOnFailureListener { e ->
                        log("getDailySteps addOnFailureListener $e")
                    }
            }
        } catch (e: java.lang.Exception) {

        }

    }

    private fun loadImage(iv: ImageView?, url: String?, male: Boolean) {
        Utils.loadImage(iv, url, male)
//        if (url == null) {
//            if (iv != null)
//                Glide.with(this).load(defaultImage).error(defaultImage).fallback(defaultImage)
//                    .into(iv)
//            return
//        }
//        url?.let {
//            if (iv != null)
//                Glide.with(this).load(it).error(defaultImage).fallback(defaultImage).into(iv)
//        }
    }

    override fun onDataReceived(list: ArrayList<HomeItem>) {
        log("onDataReceived $list")
        getDialog()?.dismiss()
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
        log("onNotify $type :: $data")
        if (type == 20) {
            if (data is Int) {
                activity?.runOnUiThread {
                    adapter?.updateCalories(data)
                }
            }
        } else if (type == 30) {
            if (data is String) {
                activity?.runOnUiThread {
                    adapter?.updateUserWeight(data)
                }
            }
        }

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
            EventBus.getDefault().removeStickyEvent(event)
            requireView().forceLayout()
            requireView().requestLayout()
            parent_constraint.invalidate()
            log("onPostEvent $event")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater?.inflate(R.menu.menu_notifications, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item?.itemId == R.id.action_notifications) {
            navigateTo(HomeItem(HomeItem.Type.NOTIFICATIONS))
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    var isStoped = false
    override fun onResume() {
        log("RESUME HOME ")
        super.onResume()

        if (isStoped && recyclerView?.adapter == null) {
            if (!::controller.isInitialized)
                controller = HomeController(this@HomeFragment, this)

            controller?.getDashboard()
        }
        isStoped = false
//        navigate(Navigator.HOME_DRAWER, null)
//        tv_item_fab?.visibility = View.GONE
//        log("visibility " + tv_item_fab?.visibility)
//        tv_item_fab?.visibility = View.VISIBLE
//        log("visibility " + tv_item_fab?.visibility)
//        tv_item_fab?.show()
//        tv_item_fab?.startAnimation(AnimationUtils.loadAnimation(context, R.anim.scale_up_anim))
        // tv_item_fab?.invalidate()
        // videoView?.resume()
        //videoBg()
        // updateFab()
    }

    override fun onStart() {
        log("onStart")
        super.onStart()
        EventBus.getDefault().register(this)
        navigate(Navigator.HOME_START, null)
    }

    override fun onStop() {
        log("onStop")
        //videoView?.stopPlayback()
        isStoped = true
        super.onStop()
        navigate(Navigator.HOME_STOP, null)
        //controller.onStop()
        //navigate(HOME_VIEW, false)
        EventBus.getDefault().unregister(this)
    }

    override fun onDestroy() {
        recyclerView?.adapter = null
        super.onDestroy()
    }
}