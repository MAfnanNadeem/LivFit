package life.mibo.hexa.ui.rxl

import android.annotation.SuppressLint
import android.util.SparseArray
import androidx.core.util.forEach
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.Observable
import io.reactivex.functions.Action
import life.mibo.hexa.R
import life.mibo.hexa.core.API
import life.mibo.hexa.core.Prefs
import life.mibo.hexa.models.rxl.GetRXLProgram
import life.mibo.hexa.models.rxl.MemberID
import life.mibo.hexa.models.rxl.RXLPrograms
import life.mibo.hexa.room.Database
import life.mibo.hexa.ui.base.BaseController
import life.mibo.hexa.ui.base.BaseFragment
import life.mibo.hexa.ui.main.MiboEvent
import life.mibo.hexa.ui.rxl.adapter.ReflexFilterAdapter
import life.mibo.hexa.ui.rxl.adapter.ReflexFilterAdapter.Listener
import life.mibo.hexa.ui.rxl.adapter.ReflexFilterAdapter.ReflexFilterModel
import life.mibo.hexa.ui.rxl.impl.ReactionListener
import life.mibo.hexa.ui.rxl.impl.ReactionObserver
import life.mibo.hexa.utils.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReactionLightController(val fragment: BaseFragment, val observer: ReactionObserver? = null) :
    BaseController(fragment.requireActivity()), ReactionListener {


    override fun onStart() {

    }

    override fun onStop() {

    }

    fun getPrograms() {
        fragment.log("Database getPrograms")
        getProgramsDb()
    }


    private fun getProgramsDb() {
        Database.execute(Action {
            fragment.log("Database getProgramsDb")
            val list = Database.getTempDb(fragment.requireContext()).rxlProgramDao().getAll()
            fragment.log("Database getProgramsDb list " + list?.size)
            var load = true
            list?.let {
                if (it.isNotEmpty()) {
                    parsePrograms(it)
                    load = false
                }
            }

            if (load) {
                getProgramsServer()
            }
            fragment.log("Database getProgramsDb end $load")
        })
    }

    fun getProgramsServer() {
        fragment.log("getProgramsServer()")
        val member = Prefs.get(fragment.context).member ?: return

        fragment?.activity?.runOnUiThread {
            fragment.getDialog()?.show()
        }

        API.request.getApi()
            .getRXLProgram(GetRXLProgram(MemberID(member?.id()), member.accessToken!!))
            .enqueue(object : Callback<RXLPrograms> {

                override fun onFailure(call: Call<RXLPrograms>, t: Throwable) {
                    fragment?.activity?.runOnUiThread {
                        fragment.getDialog()?.dismiss()
                    }
                    t.printStackTrace()
                    Toasty.error(fragment.context!!, R.string.unable_to_connect).show()
                    MiboEvent.log(t)
                    t.printStackTrace()
                }

                override fun onResponse(
                    call: Call<RXLPrograms>,
                    response: Response<RXLPrograms>
                ) {
                    fragment?.activity?.runOnUiThread {
                        fragment.getDialog()?.dismiss()
                    }

                    val data = response.body()
                    if (data != null) {
                        if (data.status.equals("success", true)) {
                            parsePrograms(data.data?.programs)
                            savePrograms(data.data?.programs)

                        } else if (data.status.equals("error", true)) {
                            checkError(data)
                        }
                    } else {
                        Toasty.error(fragment.requireContext(), R.string.error_occurred).show()
                        fragment.log("getPrograms : " + response?.errorBody()?.toString())
                    }
                }
            })
    }

    fun updateProgram(program: RXLPrograms.Program?, like: Boolean) {
        if (program == null)
            return
        Database.execute(Action {
            fragment.log("Database updateProgram")
            program.isFavourite = like
            Database.getTempDb(fragment.requireContext()).rxlProgramDao().insert(program)
        })
    }

    private fun savePrograms(programs: List<RXLPrograms.Program?>?) {
        if (programs == null)
            return
        Database.execute(Action {
            fragment.log("Database savePrograms")
            val list =
                Database.getTempDb(fragment.requireContext()).rxlProgramDao().insert(programs)
            fragment.log("Database savePrograms list " + list?.size)
        })
    }

    private fun parsePrograms(programs: List<RXLPrograms.Program?>?) {
        if (programs == null || programs.isEmpty()) {
            observer?.onDataReceived(ArrayList())
            return
        }
        val list = ArrayList<RXLPrograms.Program>()
        programs.forEach {
            it?.let { item ->
                list.add(item)
            }
        }
        observer?.onDataReceived(list)
    }

    // TODO Filters...........
    //val NO_OF_PODS = IntRange(1, 20)
    //    val NO_OF_PODS = 1..20
    ////    val PROGRAM_TYPE : IntRange = 21..29
    ////    val LIGHT_LOGIC : IntRange = 21..29
    enum class Filter(val range: IntRange) {
        NO_OF_PODS(1..20),
        PROGRAM_TYPE(21..29),
        LIGHT_LOGIC(IntRange(31, 35)),
        PLAYERS(41..44),
        ACCESSORIES(51..61)
    }

    //val selectedItems = HashMap<Int, ReflexFilterAdapter.ReflexFilterModel>()
    val selectedItems = SparseArray<ReflexFilterModel>()
    //val selectedItems = HashMap<Int, ReflexFilterModel>()

    @SuppressLint("CheckResult")
    private fun setFilters(view: RecyclerView?, type: Int = 0) {
        if (view == null)
            return
        val list = ArrayList<ReflexFilterModel>()
        Observable.fromCallable {
            when (type) {

                1 -> {
                    list.add(ReflexFilterModel(21, "Agility"))
                    list.add(ReflexFilterModel(22, "Balanced"))
                    list.add(ReflexFilterModel(23, "Core"))
                    list.add(ReflexFilterModel(24, "Flexibility"))
                    list.add(ReflexFilterModel(25, "Power"))
                    list.add(ReflexFilterModel(26, "Reaction Time"))
                    list.add(ReflexFilterModel(27, "Speed"))
                    list.add(ReflexFilterModel(28, "Stamina"))
                    list.add(ReflexFilterModel(29, "Strength"))
                }
                2 -> {
                    for (i in 1..16) {
                        list.add(ReflexFilterModel(i, "$i"))
                    }
                }
                3 -> {
                    list.add(ReflexFilterModel(31, "Random"))
                    list.add(ReflexFilterModel(32, "Sequence"))
                    list.add(ReflexFilterModel(33, "All at once"))
                    list.add(ReflexFilterModel(34, "Focus"))
                    list.add(ReflexFilterModel(35, "Home Base"))
                }
                4 -> {
                    list.add(ReflexFilterModel(41, "1"))
                    list.add(ReflexFilterModel(42, "2"))
                    list.add(ReflexFilterModel(43, "3"))
                    list.add(ReflexFilterModel(44, "4"))
                }
                5 -> {
                    list.add(ReflexFilterModel(51, "No Accessories"))
                    list.add(ReflexFilterModel(52, "Battle Rope"))
                    list.add(ReflexFilterModel(53, "Laddar"))
                    list.add(ReflexFilterModel(54, "Medicine Ball"))
                    list.add(ReflexFilterModel(55, "Mirror"))
                    list.add(ReflexFilterModel(56, "Poll"))
                    list.add(ReflexFilterModel(57, "Pul Up Bar"))
                    list.add(ReflexFilterModel(58, "Rig"))
                    list.add(ReflexFilterModel(59, "Suspension Straps"))
                    list.add(ReflexFilterModel(60, "Tree"))
                    list.add(ReflexFilterModel(61, "Resistance Band"))
                }
                else -> {
                    for (i in 1..50) {
                        list.add(ReflexFilterModel(i, "Option $i"))
                    }
                }
            }
        }.subscribe {
            val adapter = ReflexFilterAdapter(list, 3)
            val manager = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)

            adapter.setListener(filterListener)
            view.layoutManager = manager
            view.adapter = adapter
            view.isNestedScrollingEnabled = false
        }
    }


    private val filterListener = object : Listener {
        override fun onClick(data: ReflexFilterModel?) {
            if (data != null)
                selectedItems.put(data.id, data)
            // backdropBehavior.open(true)
            //showFilterOptions(data)

        }
    }

    fun applyFilters() {
        if (selectedItems.size() == 0)
            return

        selectedItems.forEach { key, value ->
            if (value.isSelected) {

            }
        }
    }


    private fun filterList(type: Int): ArrayList<RXLPrograms.Program> {
        var list = ArrayList<RXLPrograms.Program>()
        var result = ArrayList<RXLPrograms.Program>()
        when (type) {
            in Filter.NO_OF_PODS.range -> {
                list.forEach {
                    if (it.numberOfRxl == type) {
                        result.add(it.copy())
                    }
                }

            }
            in Filter.PROGRAM_TYPE.range -> {
//                list.add(ReflexFilterAdapter.ReflexFilterModel(21, "Agility"))
//                list.add(ReflexFilterAdapter.ReflexFilterModel(22, "Balanced"))
//                list.add(ReflexFilterAdapter.ReflexFilterModel(23, "Core"))
//                list.add(ReflexFilterAdapter.ReflexFilterModel(24, "Flexibility"))
//                list.add(ReflexFilterAdapter.ReflexFilterModel(25, "Power"))
//                list.add(ReflexFilterAdapter.ReflexFilterModel(26, "Reaction Time"))
//                list.add(ReflexFilterAdapter.ReflexFilterModel(27, "Speed"))
//                list.add(ReflexFilterAdapter.ReflexFilterModel(28, "Stamina"))
//                list.add(ReflexFilterAdapter.ReflexFilterModel(29, "Strength"))
                when (type) {
                    21 -> {
                        list.forEach {
                            if (it.programType == "Agility") {
                                result.add(it.copy())
                            }
                        }
                    }
                    22 -> {
                        list.forEach {
                            if (it.programType == "Balanced") {
                                result.add(it.copy())
                            }
                        }
                    }
                    23 -> {
                        list.forEach {
                            if (it.programType == "Core") {
                                result.add(it.copy())
                            }
                        }
                    }
                    24 -> {
                        list.forEach {
                            if (it.programType == "Flexibility") {
                                result.add(it.copy())
                            }
                        }
                    }
                    25 -> {
                        list.forEach {
                            if (it.programType == "Power") {
                                result.add(it.copy())
                            }
                        }
                    }
                    26 -> {
                        list.forEach {
                            if (it.programType == "Reaction Time") {
                                result.add(it.copy())
                            }
                        }
                    }
                    27 -> {
                        list.forEach {
                            if (it.programType == "Speed") {
                                result.add(it.copy())
                            }
                        }
                    }
                    28 -> {
                        list.forEach {
                            if (it.programType == "Stamina") {
                                result.add(it.copy())
                            }
                        }
                    }
                    29 -> {
                        list.forEach {
                            if (it.programType == "Strength") {
                                result.add(it.copy())
                            }
                        }
                    }
                }
            }
            in Filter.LIGHT_LOGIC.range -> {


            }
            in Filter.PLAYERS.range -> {


            }
            in Filter.ACCESSORIES.range -> {


            }

        }
        return result
    }
}
