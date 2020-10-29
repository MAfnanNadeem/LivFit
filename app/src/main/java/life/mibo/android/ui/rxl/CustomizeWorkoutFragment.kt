/*
 *  Created by Sumeet Kumar on 1/26/20 8:55 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/26/20 8:29 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.rxl

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.appcompat.widget.AppCompatSpinner
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionInflater
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_rxl_create.*
import life.mibo.android.R
import life.mibo.android.core.API
import life.mibo.android.core.Prefs
import life.mibo.android.models.base.ResponseData
import life.mibo.android.models.base.ResponseStatus
import life.mibo.android.models.rxl.SaveMyWorkout
import life.mibo.android.models.workout.RXL
import life.mibo.android.ui.base.BaseFragment
import life.mibo.android.ui.base.ItemClickListener
import life.mibo.android.ui.main.MiboEvent
import life.mibo.android.ui.main.Navigator
import life.mibo.android.utils.Constants
import life.mibo.android.utils.Toasty
import life.mibo.hardware.core.Logger
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CustomizeWorkoutFragment : BaseFragment() {

    companion object {
        const val DATA = Constants.BUNDLE_DATA
    }

    var adapter: RxlBlocksAdapter? = null
    var rxlProgram: RXL? = null
    var minutes = 0
    var seconds = 0
    var noOfPods = 4

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //val transition = TransitionInflater.from(this.activity).inflateTransition(android.R.transition.move)
        val transition =
            TransitionInflater.from(this.activity).inflateTransition(R.transition.transition_course)

        sharedElementEnterTransition = androidx.transition.ChangeScroll().apply {
            duration = 750
        }
        sharedElementEnterTransition = transition
        sharedElementReturnTransition = null
        //androidx.transition.ChangeImageTransform
//        sharedElementEnterTransition = ChangeBounds().apply {
//            duration = 750
//            enterTransition = transition
//            exitTransition = transition
//        }

//        sharedElementReturnTransition = ChangeBounds().apply {
//            duration = 750
//        }
        postponeEnterTransition()
        val root = inflater.inflate(R.layout.fragment_rxl_create, container, false)

        startPostponedEnterTransition()
        return root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val item = arguments?.getSerializable(DATA)
        log("arguments $item")
        if (item != null && item is RXL) {
            rxlProgram = item
        }

        btn_add_block?.setOnClickListener {
            createBlock()
        }
        btn_save?.setOnClickListener {
            saveMyWorkout()
        }
        btn_play?.setOnClickListener {
            playMyWorkout()
        }

        adapter = RxlBlocksAdapter(ArrayList(), object : ItemClickListener<RXLBlock?> {
            override fun onItemClicked(item: RXLBlock?, position: Int) {
                if (position == 1) {
                    activity?.runOnUiThread {
                        adapter?.delete(item)
                    }
                }
            }

        })
        recycler_view?.layoutManager = LinearLayoutManager(context)
        recycler_view?.adapter = adapter
        setSpinner(spinner_mnt, 1)
        setSpinner(spinner_sec, 2)
        setSpinner(spinner_pods, 3)
        applyRxl()

        spinner_mnt?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                minutes = getInt(parent?.selectedItem?.toString())
            }

        }

        spinner_sec?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                seconds = getInt(parent?.selectedItem?.toString())
            }
        }

        spinner_pods?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                noOfPods = getInt(parent?.selectedItem?.toString())
            }
        }

        try {
            spinner_mnt?.setSelection(1)
            spinner_pods?.setSelection(2)
        } catch (e: Exception) {

        }


    }

    fun getInt(s: String?): Int {
        return s?.replace(Regex("\\D+"), "")?.toIntOrNull() ?: 0
    }

    private fun createBlock() {
        activity?.runOnUiThread {
            adapter?.add(RXLBlock(adapter?.list?.size?.plus(1) ?: 0))
        }
    }

    private fun applyRxl() {
        Single.fromCallable {

            log("applyRxl $rxlProgram")
            val blocks = rxlProgram?.blocks
            log("applyRxl blocks $blocks")
            if (blocks != null) {
                log("applyRxl blocks NULL.........")
                var id = 1;
                for (b in blocks) {
                    log("applyRxl block adding ---- $id")
                    activity?.runOnUiThread {
                        if (b != null) {
                            log("applyRxl block adding - $id")
                            adapter?.add(
                                RXLBlock(
                                    id,
                                    b.rXLType,
                                    b.pattern,
                                    b.rXLTotalDuration ?: 30,
                                    b.rXLAction ?: 2,
                                    b.rXLDelay ?: 0,
                                    b.rXLPause ?: 0,
                                    b.rXLRound ?: 1
                                )
                            )
                            id++;
                        }


                    }
                    try {
                        Thread.sleep(100)
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                }
            }

            "return"
        }.subscribeOn(Schedulers.io()).subscribe()

    }

    fun setSpinner(spinner: AppCompatSpinner?, type: Int) {
        if (spinner == null)
            return
        val data = ArrayList<String>()
        when (type) {
            1 -> {
                for (i in 1..20)
                    data.add("$i")
            }
            2 -> {
                for (i in 0..59)
                    data.add("$i")
            }
            3 -> {
                for (i in 2..8)
                    data.add("$i")
            }
        }
        val adapter =
            ArrayAdapter<String>(spinner.context, R.layout.list_item_spinner_selected, data)
        adapter.setDropDownViewResource(R.layout.list_item_spinner);
        spinner.adapter = adapter
    }


    private fun playMyWorkout() {
        val rxt = rxlProgram
        val list = adapter?.getBlocks()
        if (rxt == null || list == null || list.size == 0) {
            Toasty.info(requireContext(), getString(R.string.no_rxl_block)).show()
            return
        }

        val blocks = ArrayList<RXL.RXLBlock>()

        for (b in list) {
            blocks.add(
                RXL.RXLBlock(
                    "click",
                    "1",
                    "1",
                    b.pattern,
                    b.action,
                    b.delay,
                    b.pause,
                    b.round,
                    b.duration,
                    b.logic,
                    ""
                )
            )
        }

        rxt.blocks = blocks

        val intent = Intent(context, QuickPlayDetailsActivity::class.java)
        //val intent = Intent(context, QuickPlayDetailsActivity2::class.java)
        intent.putExtra(Constants.BUNDLE_DATA, rxt)
        intent.putExtra("from_user_int", 10)
        startActivityForResult(intent, 3)
    }

    private fun saveMyWorkout() {
        if (et_course_name?.text?.isEmpty() == true) {
            Toasty.info(requireContext(), getString(R.string.workout_name_req)).show()
            return
        }

        if (et_course_desc?.text?.isEmpty() == true) {
            Toasty.info(requireContext(), getString(R.string.workout_desc_req)).show()
            return
        }

        if (minutes <= 0) {
            Toasty.info(requireContext(), getString(R.string.minutes_zero)).show()
            return
        }



        log("saveMyWorkout old $rxlProgram")
        //val p = rxlProgram
        val list = adapter?.getBlocks()
        log("saveMyWorkout new $list")
        if (list != null && list.size > 0) {
            val blocks = ArrayList<SaveMyWorkout.RxlBlock>()
            for (b in list) {
                log("saveMyWorkout debug ${b.debug()}")
                blocks.add(
                    SaveMyWorkout.RxlBlock(
                        "${b.action}",
                        "click",
                        "1",
                        "${b.delay}",
                        "",
                        b.pattern,
                        "${b.pause}",
                        "${b.round}",
                        b.logic,
                        "",
                        "${b.duration}"
                    )
                )
            }

            val member = Prefs.get(context).member ?: return


            val data = SaveMyWorkout.Data(
                ArrayList<String>(),
                rxlProgram?.icon,
                ArrayList<String>(),
                et_course_desc?.text?.toString(),
                member.id(),
                "$minutes",
                et_course_name?.text?.toString(),
                "$noOfPods",
                blocks,
                "1",
                "$seconds",
                "",
                "1",
                ArrayList<String>()
            )

            log("saveMyWorkout SaveMyWorkout.Data? $data")
            saveProgram(SaveMyWorkout(data, member.accessToken))
        }
    }

    fun saveProgram(workout: SaveMyWorkout?) {
        if (workout == null) {
            return
        }
        getDialog()?.show()
        API.request.getApi()
            .saveMyRxlWorkout(workout)
            .enqueue(object : Callback<ResponseStatus> {

                override fun onFailure(call: Call<ResponseStatus>, t: Throwable) {
                    getDialog()?.dismiss()
                    t.printStackTrace()
                    Toasty.error(context!!, R.string.unable_to_connect).show()
                    MiboEvent.log(t)
                    t.printStackTrace()
                }

                override fun onResponse(
                    call: Call<ResponseStatus>,
                    response: Response<ResponseStatus>
                ) {
                    getDialog()?.dismiss()

                    val data = response.body()
                    if (data != null) {
                        if (data.isSuccess()) {
                            val msg = data.data
                            if (msg is String) {
                                Toasty.info(context!!, msg).show()
                            } else {
                                Toasty.info(context!!, getString(R.string.workout_saved)).show()
                            }
                            try {
                                Prefs.get(context).set("rxl_saved", true)
                            } catch (e: Exception) {
                                MiboEvent.log(e)
                            }
                            navigate(Navigator.RXL_HOME, null)

                        } else if (data.status.equals("error", true)) {
                            checkSession(data)
                        }
                    } else {

                    }
                }
            })
    }

    data class RXLBlock(var id: Int) {
        var distractingColors: String? = null
        var pattern: String = ""
        var action: Int = 2
        var delay: Int = 0
        var pause: Int = 0
        var round: Int = 1
        var duration: Int = 30
        var logic: String = "SEQUENCE"
        var videoLink: String = ""

        constructor(
            id: Int,
            logic: String?,
            pattern: String?,
            dur: Int,
            action: Int,
            delay: Int,
            pause: Int,
            round: Int
        ) : this(id) {
            this.logic = logic ?: ""
            this.pattern = pattern ?: ""
            this.duration = dur
            this.action = action
            this.delay = delay
            this.pause = pause
            this.round = round
        }

        fun debug(): String {
            return "RXLBlock(id=$id, distractingColors=$distractingColors, pattern='$pattern', action=$action, delay=$delay, pause=$pause, round=$round, duration=$duration, logic='$logic', videoLink='$videoLink')"
        }


    }

    class RxlBlocksAdapter(
        var list: ArrayList<RXLBlock?>,
        var listener: ItemClickListener<RXLBlock?>?
    ) : RecyclerView.Adapter<RxlBlocksAdapter.BlockHolder>() {

        //var list: ArrayList<Item>? = null

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlockHolder {
            return BlockHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.list_item_rxl_edit_block,
                    parent,
                    false
                )
            )
        }

        override fun getItemCount(): Int {
            return list?.size ?: 0
        }

        private fun getItem(position: Int): RXLBlock? {
            return list?.get(position)
        }

        override fun onBindViewHolder(holder: BlockHolder, position: Int) {
            Logger.e("ReflexAdapter: onBindViewHolder $position")

            holder.bind(getItem(position), listener)
        }

        fun add(block: RXLBlock?) {
            Logger.e("RxlBlocksAdapter add " + block?.id)
            if (block != null) {
                list.add(block)
                notifyItemInserted(list.size)
            }
        }

        fun delete(block: RXLBlock?) {
            Logger.e("RxlBlocksAdapter delete " + block?.id)
            if (block != null) {
                var id = -1
                list?.forEachIndexed { index, rxlBlock ->
                    if (block.id == rxlBlock?.id) {
                        id = index
                    }
                }

                Logger.e("RxlBlocksAdapter delete index: $id")
                if (id >= 0) {
                    list.removeAt(id)
                    notifyItemRemoved(id.plus(1))
                }
            }
        }

        fun getBlocks(): ArrayList<RXLBlock> {
            val blocks = ArrayList<RXLBlock>()
            for (i in list) {
                if (i != null)
                    blocks.add(i)
            }
            return blocks
        }

        fun getRxlBlocks(): ArrayList<RXL.RXLBlock> {
            val blocks = ArrayList<RXL.RXLBlock>()
            for (i in list) {
                //  if (i != null)
                //  blocks.add(RXL.RXLBlock("", "1", "1", i.pattern, ))
            }
            return blocks
        }


        class BlockHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var pattern: EditText? = itemView.findViewById(R.id.et_pattern)
            var delete: View? = itemView.findViewById(R.id.btn_delete)
            var logicSpinner: AppCompatSpinner? = itemView.findViewById(R.id.spinner_logic)
            var durationSpinner: AppCompatSpinner? = itemView.findViewById(R.id.spinner_dur)
            var actionSpinner: AppCompatSpinner? = itemView.findViewById(R.id.spinner_action)
            var delaySpinner: AppCompatSpinner? = itemView.findViewById(R.id.spinner_delay)
            var pauseSpinner: AppCompatSpinner? = itemView.findViewById(R.id.spinner_pause)
            var roundSpinner: AppCompatSpinner? = itemView.findViewById(R.id.spinner_round)

            var data: RXLBlock? = null

            fun bind(item: RXLBlock?, listener: ItemClickListener<RXLBlock?>?) {
                Logger.e("BlockHolder $item")
                if (item == null)
                    return
                data = item

                setSpinner(logicSpinner, 1)
                setSpinner(durationSpinner, 2)

                setSpinner(actionSpinner, 3)
                setSpinner(roundSpinner, 0)

                setSpinner(delaySpinner, 4)
                setSpinner(pauseSpinner, 4)

                delete?.setOnClickListener {
                    listener?.onItemClicked(item, 1)
                }

                pattern?.isEnabled = item.logic.toLowerCase() == "sequence"

                try {
                    logicSpinner?.setSelection(getLogicType(item.logic).minus(1))
                    durationSpinner?.setSelection(getDur(item.duration).minus(1))
                    actionSpinner?.setSelection(item.action.minus(1))
                    delaySpinner?.setSelection(item.delay.minus(1))
                    pauseSpinner?.setSelection(0)
                    roundSpinner?.setSelection(item.round.minus(1))
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }

                logicSpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {

                    }

                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        item.logic = parent?.selectedItem?.toString() ?: ""
                        pattern?.isEnabled = item.logic.toLowerCase().equals("sequence")
                    }
                }

                durationSpinner?.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onNothingSelected(parent: AdapterView<*>?) {

                        }

                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            item.duration = getInt(parent?.selectedItem?.toString())
                        }
                    }

                actionSpinner?.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onNothingSelected(parent: AdapterView<*>?) {

                        }

                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            item.action = getInt(parent?.selectedItem?.toString())
                        }
                    }

                delaySpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {

                    }

                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        item.delay = getInt(parent?.selectedItem?.toString())
                    }
                }

                pauseSpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {

                    }

                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        item.pause = getInt(parent?.selectedItem?.toString())
                    }
                }

                roundSpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {

                    }

                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        item.round = getInt(parent?.selectedItem?.toString())
                    }
                }

                pattern?.doAfterTextChanged {
                    item.pattern = it.toString()
                }

            }


            fun setSpinner(spinner: AppCompatSpinner?, type: Int) {
                if (spinner == null)
                    return
                val data = ArrayList<String>()
                when (type) {
                    1 -> {
                        data.add("SEQUENCE")
                        data.add("RANDOM")
                        data.add("FOCUS")
                        data.add("ALL AT ONCE - TAP ONE")
                        data.add("ALL AT ONCE - TAP ALL")
                    }
                    2 -> {
                        for (i in 1..10)
                            data.add("  ${i.times(15)} sec ")
                    }
                    3 -> {
                        for (i in 1..10)
                            data.add("  $i sec ")
                    }
                    4 -> {
                        for (i in 0..10)
                            data.add("  $i sec ")
                    }
                    else -> {
                        for (i in 0..10)
                            data.add("   $i   ")
                    }
                }

                val adapter =
                    ArrayAdapter<String>(spinner.context, R.layout.list_item_spinner_selected, data)
                adapter.setDropDownViewResource(R.layout.list_item_spinner);
                spinner.adapter = adapter
            }

            fun getLogicType(logic: String?): Int {
                return RxlUtils.getLogicType(logic)
            }

            fun getDur(dur: Int?): Int {
                return dur?.div(15) ?: 1
            }

            fun getBlock(): RXLBlock? {
                val block = data
                if (block != null) {
                    block.logic = logicSpinner?.selectedItem?.toString() ?: ""
                    block.pattern = pattern?.text?.toString() ?: ""
                    block.duration = getInt(durationSpinner?.selectedItem?.toString())
                    block.action = getInt(actionSpinner?.selectedItem?.toString())
                    block.pause = getInt(pauseSpinner?.selectedItem?.toString())
                    block.delay = getInt(delaySpinner?.selectedItem?.toString())
                    block.round = getInt(roundSpinner?.selectedItem?.toString())
                }

                return block
            }

            fun getInt(s: String?): Int {
                return s?.replace(Regex("\\D+"), "")?.toIntOrNull() ?: 0
            }
        }
    }

    override fun onStop() {
        super.onStop()
    }
}
