package life.mibo.android.ui.rxt

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_rxt_install_predefine.*
import life.mibo.android.R
import life.mibo.android.core.API
import life.mibo.android.core.Prefs
import life.mibo.android.models.base.ResponseStatus
import life.mibo.android.models.rxt.SaveIslandPost
import life.mibo.android.ui.base.BaseActivity
import life.mibo.android.ui.base.ItemClickListener
import life.mibo.android.ui.rxt.model.ChildAdapter
import life.mibo.android.ui.rxt.model.Controller
import life.mibo.android.ui.rxt.model.InstallAdapter
import life.mibo.android.ui.rxt.model.Tile
import life.mibo.android.utils.Toasty
import life.mibo.hardware.CommunicationManager
import life.mibo.hardware.SessionManager
import life.mibo.hardware.events.ChangeColorEvent
import life.mibo.hardware.events.RxlStatusEvent
import life.mibo.hardware.models.Device
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ConfigureIslandActivity : BaseActivity() {

    companion object {

        const val REQUEST_CODE = 2345
        fun launch(
            context: androidx.fragment.app.Fragment,
            name: String?,
            id: Int,
            width: Int,
            height: Int,
            total: Int,
            update: Int
        ) {
            val intent = Intent(context.context, ConfigureIslandActivity::class.java)
            intent.putExtra("island_name", name)
            intent.putExtra("island_id", id)
            intent.putExtra("island_x", height)
            intent.putExtra("island_y", width)
            intent.putExtra("island_total", total)
            intent.putExtra("island_update_mode", update)
            context.startActivityForResult(intent, REQUEST_CODE)
        }
    }

    //var tvStatus: TextView? = null
    // var emptyIslandView:TextView? = null
    //var islandName: EditText? = null
    // var playSequence: ImageButton? = null
    //var recyclerView: RecyclerView? = null
    //var islandRecyclerView:RecyclerView? = null
    // var constraintTop: View? = null
    // var constraintLeft:android.view.View? = null
    //var constraintRight:android.view.View? = null
    // var constraintBottom:android.view.View? = null
    var mainAdapter: InstallAdapter? = null
    var islandAdapter: ChildAdapter? = null

    //List<Tile> islandList = new ArrayList<>();
    var isCreateMode = true
    var islandX = 0
    var islandY = 0
    var islandName = ""
    var islandId = 0
    var totalTiles = 0
    var isUpdateMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_rxt_install_predefine)

        setup()
    }

    fun setup() {
//        tvStatus = findViewById(R.id.tv_status)
//        val config: View = view.findViewById(R.id.btn_configure)
//        val back: View = view.findViewById(R.id.btn_back)
//        val island: View = view.findViewById(R.id.btn_island)
//        val save: View = view.findViewById(R.id.btn_save_island)
//        playSequence = view.findViewById(R.id.btn_play)
//        constraintLeft = view.findViewById(R.id.left_view)
//        constraintRight = view.findViewById(R.id.right_view)
//        recyclerView = view.findViewById(R.id.recyclerView)
//        islandRecyclerView = view.findViewById(R.id.recyclerViewSelected)
//        emptyIslandView = view.findViewById(R.id.tv_empty)
//        islandName = view.findViewById(R.id.et_island)


        islandId = intent?.getIntExtra("island_id", 0) ?: 0
        islandName = intent?.getStringExtra("island_name") ?: ""
        islandX = intent?.getIntExtra("island_x", 0) ?: 0
        islandY = intent?.getIntExtra("island_y", 0) ?: 0
        totalTiles = intent?.getIntExtra("island_total", 0) ?: 0
        isUpdateMode = intent?.getIntExtra("island_update_mode", 0) ?: 0 == 7


        if (islandId == 0 || islandX == 0 || islandY == 0) {
            setResult(Activity.RESULT_CANCELED)
            finish()
            return
        }

        et_island?.text = islandName
        //btn_back.setOnClickListener { v: View? -> onBackPressed() }
        tv_empty?.visibility = View.GONE

        setupMainAdapter()
        setupIslandAdapter()

        btn_play?.setOnClickListener { v: View? ->
            if (islandAdapter != null) {
                playSequence(islandAdapter!!.selectedCopy)
            }
        }

        btn_save_island?.setOnClickListener { v: View? -> saveIslandClicked() }
        //btn_save_island.isEnabled = true
    }


    private fun saveIslandClicked() {
        if (islandAdapter == null) {
            return
        }
        val list = islandAdapter!!.selectedCopy

        if (list == null || list.isEmpty()) {
            Toasty.snackbar(btn_save_island, "Please add tiles to save")
            return
        }
        if (et_island.text.toString().isEmpty()) {
            Toasty.snackbar(btn_save_island, "Island Name can not be empty")
            return
        }

        AlertDialog.Builder(this).setTitle(if(isUpdateMode)  "Update Island?" else "Save Island?")
            .setMessage("Make sure you play the sequence, Island will perform the sequence as in order tile was added")
            .setPositiveButton("SAVE") { dialog: DialogInterface?, which: Int ->
                //log("setPositiveButton $list")
                saveIsland(list)
                //updateIsland(list)
            }
            .setNeutralButton("PLAY SEQUENCE") { dialog: DialogInterface?, which: Int ->
                playSequence(
                    list
                )
            }
            .show()
    }

    private fun saveIsland(list: List<Tile>) {
        if (isUpdateMode) {
            updateIsland(list)
            return
        }
        // API
        log("islands : $list")
        val trainer = Prefs.get(this).member ?: return
        val locId = trainer.locationID
        if (locId.isNullOrEmpty()) {
            Toasty.info(this, getString(R.string.invalid_locationid)).show()
            return
        }

        getDialog()?.show()
        var first = true
        val builder = StringBuilder()
        for (tile in list) {
            if (!first)
                builder.append(",")
            builder.append(tile.uid)
            builder.append("-")
            builder.append(tile.tileId)
            first = false
        }
        val tiles = builder.toString()
        log("builder : $builder")
        log("tiles : $tiles")

        val post = SaveIslandPost(
            SaveIslandPost.Data(
                islandId,
                trainer.id,
                locId,
                tiles
            ), trainer.accessToken
        )
        API.request.getApi().saveIslandTiles(post).enqueue(object : Callback<ResponseStatus> {
            override fun onFailure(call: Call<ResponseStatus>, t: Throwable) {
                getDialog()?.dismiss()
            }

            override fun onResponse(
                call: Call<ResponseStatus>,
                response: Response<ResponseStatus>
            ) {
                getDialog()?.dismiss()
                if (response.body()?.isSuccess() == true) {
                    Toasty.info(this@ConfigureIslandActivity, "Island saved successfully").show()
                    intent?.putExtra("tiles_config", tiles)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                } else {
                    Toasty.info(
                        this@ConfigureIslandActivity,
                        response.body()?.errors?.get(0)?.message ?: "Error Occurred"
                    ).show()
                }
            }

        })

    }

    private fun updateIsland(list: List<Tile>) {
        // API
        log("islands : $list")
        val trainer = Prefs.get(this).member ?: return
        val locId = trainer.locationID
        if (locId.isNullOrEmpty()) {
            Toasty.info(this, getString(R.string.invalid_locationid)).show()
            return
        }

        getDialog()?.show()
        var first = true
        val builder = StringBuilder()
        for (tile in list) {
            if (!first)
                builder.append(",")
            builder.append(tile.uid)
            builder.append("-")
            builder.append(tile.tileId)
            first = false
        }
        val tiles = builder.toString()
        log("builder : $builder")
        log("tiles : $tiles")

        val post = SaveIslandPost(
            SaveIslandPost.Data(
                islandId,
                trainer.id,
                locId,
                tiles
            ), trainer.accessToken, "UpdateIslandTiles"
        )
        API.request.getApi().updateIslandTiles(post).enqueue(object : Callback<ResponseStatus> {
            override fun onFailure(call: Call<ResponseStatus>, t: Throwable) {
                getDialog()?.dismiss()
            }

            override fun onResponse(
                call: Call<ResponseStatus>,
                response: Response<ResponseStatus>
            ) {
                getDialog()?.dismiss()
                if (response.body()?.isSuccess() == true) {
                    Toasty.info(this@ConfigureIslandActivity, "Island Updated Successfully").show()
                    intent?.putExtra("tiles_config", tiles)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                } else {
                    Toasty.info(
                        this@ConfigureIslandActivity,
                        response.body()?.errors?.get(0)?.message ?: "Error Occurred"
                    ).show()
                }
            }

        })

    }


    private fun setupMainAdapter() {
        val devices = SessionManager.getInstance().userSession.devices
        //ArrayList<String> list = new ArrayList<>();
        val controllers = ArrayList<Controller>()
        var controllerId = 0
        val tilesId = 0
        for (device in devices) {
            if (device.isRxt) {
                controllerId++
                val tiles = ArrayList<Tile>()
                val tileCount = device.tiles
                val uid = device.uid
                //log("setupAdapter UID "+uid);
                if (tileCount > 0) {
                    for (i in 0 until tileCount) {
                        tiles.add(
                            Tile(
                                uid,
                                controllerId,
                                i + 1,
                                false,
                                false,
                                R.drawable.bg_tile_generic
                            )
                        )
                    }
                }
                controllers.add(Controller(uid, controllerId, tileCount, tiles))
            }
        }
        //Toasty.info(this, "Controllers Connected " + controllers.size).show()
        recyclerView.layoutManager = LinearLayoutManager(this)
        mainAdapter = InstallAdapter(controllers, object : ItemClickListener<Any> {
            override fun onItemClicked(item: Any?, position: Int) {
                if (item != null)
                    onItemClick(item, position)
            }

        })
        recyclerView.adapter = mainAdapter
        refreshSpan()
    }

    private fun setupIslandAdapter() {
        val list = ArrayList<Tile>();
        val total = islandX.times(islandY)
        for (i in 0 until total) {
            list.add(Tile("", i, 0, false, false, true))
        }
        recyclerViewSelected.layoutManager =
            StaggeredGridLayoutManager(islandY, StaggeredGridLayoutManager.VERTICAL)
        //islandRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        islandAdapter = ChildAdapter(list, object : ItemClickListener<Any> {
            override fun onItemClicked(item: Any?, position: Int) {
                if (item != null)
                    onIslandItemClick(item, position)
            }

        })
        recyclerViewSelected.adapter = islandAdapter
    }

    fun onItemClick(`object`: Any, position: Int) {
        log("InstallAdapter onItemClick $position : $`object`")
        if (`object` is Tile) {
            val tile = `object`
            if (isCreateMode) {
                if (!tile.isSelected) sendColor(tile.uid, "" + tile.tileId, Color.RED, 1000)
                tile.updateSelect()
                mainAdapter!!.update(tile)
                addToIsland(tile)
            } else {
                sendColor(tile.uid, "" + tile.tileId, Color.RED, 1000)
            }
        } else if (`object` is Controller) {
            blinkAll(`object`.uid, 2, Color.GREEN, 500, 200)
        }
    }

    fun onIslandItemClick(`object`: Any, position: Int) {
        log("InstallAdapter onItemClick $position : $`object`")
        if (`object` is Tile) {
            val tile = `object`
            sendColor(tile.uid, "" + tile.tileId, Color.RED, 1500)
        } else if (`object` is Controller) {
            blinkAll(`object`.uid, 2, Color.GREEN, 1000, 1000)
        }
    }

    fun refreshSpan() {
        recyclerView.viewTreeObserver.addOnGlobalLayoutListener(
            object : OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    recyclerView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    val viewWidth = recyclerView.measuredWidth
                    val cardViewWidth = recyclerView.resources.getDimension(R.dimen.rxt_child_item)
                    val newSpanCount = Math.floor(viewWidth.div(cardViewWidth.toDouble())).toInt()
                    mainAdapter!!.setSpanCount(newSpanCount)
                    //manager.requestLayout();
                    mainAdapter!!.notifyDataSetChanged()
                }
            })
    }

    fun refreshIslandSpan() {
        recyclerView?.viewTreeObserver?.addOnGlobalLayoutListener(
            object : OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    recyclerView?.viewTreeObserver?.removeOnGlobalLayoutListener(this)
                    val viewWidth: Int? = recyclerView?.measuredWidth
                    val cardViewWidth: Float? =
                        recyclerView?.resources?.getDimension(R.dimen.rxt_child_item)
                    val newSpanCount =
                        Math.floor(viewWidth!!.div(cardViewWidth!!.toDouble())).toInt()
                    val manager: RecyclerView.LayoutManager? = recyclerView.layoutManager
                    if (manager is GridLayoutManager) {
                        manager.spanCount = newSpanCount
                        islandAdapter!!.notifyDataSetChanged()
                    }
                }
            })
    }

    private var lastUpdateId = -1;
    private fun addToIsland(tile: Tile?) {
        log("addToIsland $tile")
        if (tile != null) {
            if (tile.isSelected) {
                if (lastUpdateId < 0)
                    lastUpdateId = 0
                else
                    lastUpdateId++
            } else {
                if (lastUpdateId > 1)
                    lastUpdateId--
            }
            //log("addToIsland lastUpdateId $lastUpdateId")
            tile.isSelected = islandAdapter!!.update(tile.copy(), lastUpdateId)

            val size = islandAdapter!!.addedTiles.size
            updatePlayButton(size == 0)
            btn_save_island.isEnabled = size == totalTiles
            log("addToIsland lastUpdateId $lastUpdateId :: total $totalTiles : size $size")
            // if (islandAdapter != null)
            //   islandAdapter.notifyDataSetChanged();
        }
    }

    var playNeedUpdate = true

    fun updatePlayButton(empty: Boolean) {
        if (playNeedUpdate == empty) return
        playNeedUpdate = empty
        if (empty) {
            // btn_play?.setColorFilter(Color.GRAY)
            tv_empty?.visibility = View.VISIBLE
        } else {
            //btn_play?.setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary))
            tv_empty?.visibility = View.GONE
        }
    }


    fun sendColor(uid: String?, tileId: String?, color: Int, time: Int) {
        if (time < 1) {
            toast("Time must be > 0")
            return
        }
        Single.fromCallable {
            CommunicationManager.getInstance()
                .onChangeRxtColorEvent(ChangeColorEvent(uid, tileId, color, time, 0))
            ""
        }.subscribeOn(Schedulers.io()).doOnError { }.subscribe()
    }

    fun blinkAll(uid: String?, cycle: Int, color: Int, timeOn: Int, timeOff: Int) {
        if (cycle < 1) {
            toast("Cycles must be > 0")
            return
        }
        Single.fromCallable {
            CommunicationManager.getInstance()
                .onRxtBlinkAll(ChangeColorEvent(uid, "" + cycle, color, timeOn, timeOff))
            ""
        }.subscribeOn(Schedulers.io()).doOnError { }.subscribe()
    }

    fun blinkAll2(uid: List<String>, cycle: Int, color: Int) {
        if (cycle < 1) {
            toast("Cycles must be > 0")
            log("blinkAll2 $cycle")
            return
        }
        log("blinkAll2 $uid")
        Observable.fromIterable(uid)
            .doOnError { throwable -> log("blinkAll2 adoOnError accept $throwable") }
            .subscribeOn(Schedulers.io()).doOnComplete { log("blinkAll2 doOnComplete run...") }
            .doOnNext { s ->
                log("blinkAll2 doOnNext accept : $s")
                try {
                    CommunicationManager.getInstance()
                        .onRxtBlinkAll(ChangeColorEvent(s, "" + cycle, color, 500, 500))
                    Thread.sleep(200)
                } catch (e: Exception) {
                    log("blinkAll2 doOnNext error : " + e.message)
                }
            }.subscribe()
    }

    fun playSequence(tiles: List<Tile>) {
        log("playSequence $tiles")
        if (!isCreateMode || tiles.isEmpty()) {
            return
        }
        val color = Color.BLUE
        Observable.fromIterable(tiles)
            .doOnError { throwable: Throwable -> log("playSequence doOnError accept $throwable") }
            .subscribeOn(Schedulers.io()).doOnComplete { log("playSequence doOnComplete run...") }
            .doOnNext { tile: Tile ->
                log("playSequence doOnNext accept : $tile")
                try {
                    CommunicationManager.getInstance().onChangeRxtColorEvent(
                        ChangeColorEvent(
                            tile.uid,
                            "" + tile.tileId,
                            color,
                            500,
                            0
                        )
                    )
                    Thread.sleep(500)
                } catch (e: Exception) {
                    log("playSequence doOnNext error : " + e.message)
                }
            }.subscribe()
    }

    private fun toast(s: String) {
        Toasty.info(this, s).show()
    }

    @Subscribe
    fun EventReceived(device: Device?) {
    }

    @Subscribe
    fun EventReceived(event: RxlStatusEvent) {
        log("RxtInstallation RxlStatusEvent $event")
        try {
            // if (tvStatus != null) {
            //     getActivity().runOnUiThread(Runnable { tvStatus.setText(event.commandString) })
            //  }
        } catch (e: Exception) {
        }
    }

    override fun onStart() {
        super.onStart()
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this)
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
    }
}