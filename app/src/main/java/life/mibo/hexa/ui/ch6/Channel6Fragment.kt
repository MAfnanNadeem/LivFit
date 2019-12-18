package life.mibo.hexa.ui.ch6

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_channel6.*
import life.mibo.hardware.CommunicationManager
import life.mibo.hexa.R
import life.mibo.hexa.ui.base.BaseFragment
import life.mibo.hexa.ui.ch6.adapter.ChannelAdapter
import life.mibo.hexa.ui.dashboard.Channel6ViewModel


class Channel6Fragment : BaseFragment() {

    private lateinit var viewModel: Channel6ViewModel
    var recyclerView: RecyclerView? = null
    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View? {
        viewModel =
            ViewModelProviders.of(this).get(Channel6ViewModel::class.java)
        val root = i.inflate(R.layout.fragment_channel6, c, false)
        //  val textView: TextView = root.findViewById(R.id.text_dashboard)
        recyclerView = root.findViewById(R.id.recyclerView)
        viewModel.text.observe(this, Observer {
            //    textView.text = ""//it
        })
        this.activity?.actionBar?.hide()
        setRecycler(recyclerView!!)
        retainInstance = true

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        iv_plus?.setOnClickListener {

            Observable.fromArray(list).flatMapIterable { x -> x }
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : io.reactivex.Observer<ChannelAdapter.Item> {
                    override fun onSubscribe(d: Disposable) {
                        log("iv_plus onSubscribe")
                    }

                    override fun onNext(t: ChannelAdapter.Item) {
                        log("iv_plus onNext")
                        t.incChannelPercent()
                    }

                    override fun onError(e: Throwable) {
                        log("iv_plus onError", e)
                        e.printStackTrace()
                    }

                    override fun onComplete() {
                        log("iv_plus onComplete")
                        adapter?.notifyDataSetChanged()

                    }
                })

        }

        iv_minus?.setOnClickListener {

        }

        iv_play?.setOnClickListener {

        }

        iv_stop?.setOnClickListener {

        }
    }

    private var manager: CommunicationManager? = null
    fun getManager(): CommunicationManager {
        if (manager == null)
            manager = CommunicationManager.getInstance()
        return manager!!
    }

    var list = ArrayList<ChannelAdapter.Item>()
    var adapter: ChannelAdapter? = null
    fun setRecycler(view: RecyclerView) {

        if (list == null)
            list = ArrayList()
        list.clear()

        list.add(ChannelAdapter.Item(1, R.drawable.ic_channel_abdomen, 1, 2))
        list.add(ChannelAdapter.Item(2, R.drawable.ic_channel_back_neck, 0, 0))
        list.add(ChannelAdapter.Item(3, R.drawable.ic_channel_biceps, 0, 0))
        list.add(ChannelAdapter.Item(4, R.drawable.ic_channel_chest, 0, 0))
        list.add(ChannelAdapter.Item(5, R.drawable.ic_channel_glutes, 0, 0))
        list.add(ChannelAdapter.Item(6, R.drawable.ic_channel_thighs, 0, 0))
        if (isLand)
            view.layoutManager = GridLayoutManager(this@Channel6Fragment.activity, 6)
        else
            view.layoutManager = GridLayoutManager(this@Channel6Fragment.activity, 1)
        adapter = ChannelAdapter(list, isLand)
        adapter?.setListener(object : ChannelAdapter.Listener {
            override fun onClick(id: Int) {

            }

            override fun onPlusClicked(id: Int) {

            }

            override fun onMinusClicked(id: Int) {

            }

            override fun onPlayPauseClicked(id: Int, isPlay: Boolean) {

            }

        })
        //val manager = GridLayoutManager(this@Channel6Fragment.activity, 1)
        //view.layoutManager = manager
        view.adapter = adapter
        //Toasty.warning(this@Channel6Fragment.context, "")
        //Toasty.warning(this@Channel6Fragment.context!!, "Configuration changes $isLand").show()

    }

    private var isLand = false

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        isLand = newConfig.orientation === Configuration.ORIENTATION_LANDSCAPE
        //Toasty.warning(this@Channel6Fragment.context!!, "Configuration changes $isLand").show()
        setRecycler(recyclerView!!)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        //setRecycler(recyclerView!!)
    }
}