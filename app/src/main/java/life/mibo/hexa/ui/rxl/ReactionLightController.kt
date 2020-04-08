package life.mibo.hexa.ui.rxl

import android.annotation.SuppressLint
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import androidx.core.util.forEach
import androidx.core.util.size
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.Observable
import io.reactivex.functions.Action
import life.mibo.hardware.core.Logger
import life.mibo.hexa.R
import life.mibo.hexa.core.API
import life.mibo.hexa.core.Prefs
import life.mibo.hexa.core.toIntOrZero
import life.mibo.hexa.database.Database
import life.mibo.hexa.models.base.MemberPost
import life.mibo.hexa.models.base.ResponseData
import life.mibo.hexa.models.rxl.*
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

    fun getPrograms(memberId: String) {
        fragment.log("Database getPrograms $memberId")
        getProgramsDb(memberId)
    }


    private fun getProgramsDb(memberId: String) {
        try {
            if (memberId.isNotEmpty() && Prefs.get(fragment.context).get("rxl_saved", false)) {
                // later implement if list is 2 weeks old than refresh
                getRxlExercisesServer(memberId)
                return
            }
        } catch (e: Exception) {
            MiboEvent.log(e)
        }
        Database.execute(Action {
            fragment.log("Database getProgramsDb $memberId")
            val list = if (memberId.isEmpty())
                Database.getTempDb(fragment.requireContext()).rxlProgramDao().getQuickPlay()
            else Database.getTempDb(fragment.requireContext()).rxlProgramDao().getMyPlay(memberId)
            fragment.log("Database getProgramsDb memberId $memberId :: $list")
            fragment.log("Database getProgramsDb list " + list?.size)
            var load = true
            list?.let {
                if (it.isNotEmpty()) {
                    parsePrograms(it)
                    load = false
                }
            }

            if (load) {
                getRxlExercisesServer(memberId)
            }
            fragment.log("Database getProgramsDb end $load")
        })
    }


    fun getProgramsServer() {
//        fragment.log("getProgramsServer()")
//        val member = Prefs.get(fragment.context).member ?: return
//
//        fragment?.activity?.runOnUiThread {
//            fragment.getDialog()?.show()
//        }
//
//        API.request.getApi()
//            .getRXLProgram(GetRXLProgram(MemberID(member?.id()), member.accessToken!!))
//            .enqueue(object : Callback<RXLPrograms> {
//
//                override fun onFailure(call: Call<RXLPrograms>, t: Throwable) {
//                    fragment?.activity?.runOnUiThread {
//                        fragment.getDialog()?.dismiss()
//                    }
//                    t.printStackTrace()
//                    Toasty.error(fragment.context!!, R.string.unable_to_connect).show()
//                    MiboEvent.log(t)
//                    t.printStackTrace()
//                }
//
//                override fun onResponse(
//                    call: Call<RXLPrograms>,
//                    response: Response<RXLPrograms>
//                ) {
//                    fragment?.activity?.runOnUiThread {
//                        fragment.getDialog()?.dismiss()
//                    }
//
//                    val data = response.body()
//                    if (data != null) {
//                        if (data.status.equals("success", true)) {
//                            parsePrograms(data.data?.programs)
//                            savePrograms(data.data?.programs)
//
//                        } else if (data.status.equals("error", true)) {
//                            checkError(data)
//                        }
//                    } else {
//                        Toasty.error(fragment.requireContext(), R.string.error_occurred).show()
//                        fragment.log("getPrograms : " + response?.errorBody()?.toString())
//                    }
//                }
//            })
    }

    fun getRxlExercisesServer(memberId: String) {
        fragment.log("getRXLExerciseProgram()")
        val member = Prefs.get(fragment.context).member ?: return

        fragment?.activity?.runOnUiThread {
            if (memberId.isEmpty())
                fragment.getDialog()?.show()
        }

        API.request.getApi().getRXLExerciseProgram(MemberPost(memberId, member.accessToken!!, "GetRXLExerciseProgram"))
            .enqueue(object : Callback<RxlExercises> {

                override fun onFailure(call: Call<RxlExercises>, t: Throwable) {
                    fragment?.activity?.runOnUiThread {
                        fragment.getDialog()?.dismiss()
                    }
                    t.printStackTrace()
                    Toasty.error(fragment.context!!, R.string.unable_to_connect).show()
                    MiboEvent.log(t)
                    t.printStackTrace()
                }

                override fun onResponse(
                    call: Call<RxlExercises>,
                    response: Response<RxlExercises>
                ) {
                    fragment?.activity?.runOnUiThread {
                        fragment.getDialog()?.dismiss()
                    }

                    val data = response.body()
                    if (data != null) {
                        if (data.status.equals("success", true)) {

                            parsePrograms(data.data)
                           savePrograms(data.data)

                        } else if (data.status.equals("error", true)) {
                            parsePrograms(ArrayList())
                            //checkError(data)
                        }
                    } else {

                        Toasty.error(fragment.requireContext(), R.string.error_occurred).show()
                        fragment.log("getPrograms : " + response?.errorBody()?.toString())
                    }
                }
            })
    }

    fun updateProgram(program: RxlProgram?, like: Boolean) {
        if (program == null)
            return
        Database.execute(Action {
            fragment.log("Database updateProgram")
            program.isFavourite = like
            Database.getTempDb(fragment.requireContext()).rxlProgramDao().insert(program)
        })
    }

    private fun savePrograms(programs: List<RxlProgram?>?) {
        if (programs == null)
            return
        Database.execute(Action {
            fragment.log("Database savePrograms")
            val list =
                Database.getTempDb(fragment.requireContext()).rxlProgramDao().insert(programs)
            fragment.log("Database savePrograms list " + list?.size)
        })
        try {
            Prefs.get(fragment.context).set("rxl_saved", false)
            Logger.e("Database savePrograms rxl_saved false")
        } catch (e: Exception) {
            MiboEvent.log(e)
            Logger.e("Database savePrograms error $e")
        }
    }

    private val programList = ArrayList<RxlProgram>()

    private fun parsePrograms(programs: List<RxlProgram?>?) {
        programList.clear()
        if (programs == null || programs.isEmpty()) {
            observer?.onDataReceived(ArrayList())
            return
        }
        val list = ArrayList<RxlProgram>()
        programs.forEach {
            it?.let { item ->
                list.add(item)
                programList.add(item)
            }
        }
        observer?.onDataReceived(list)
    }

    fun deleteProgram(program: RxlProgram?, action: (RxlProgram?) -> Unit) {
        if (program == null)
            return
        fragment.log("getProgramsServer()")
        val member = Prefs.get(fragment.context).member ?: return

        fragment?.activity?.runOnUiThread {
            fragment.getDialog()?.show()
        }

        API.request.getApi()
            .deleteRXLExerciseProgram(
                DeleteRxlExercise(
                    DeleteRxlExercise.Data(
                        member.id(),
                        "${program.id}"
                    ), member.accessToken!!
                )
            )
            .enqueue(object : Callback<ResponseData> {

                override fun onFailure(call: Call<ResponseData>, t: Throwable) {
                    fragment?.activity?.runOnUiThread {
                        fragment.getDialog()?.dismiss()
                    }
                    t.printStackTrace()
                    Toasty.error(fragment.context!!, R.string.unable_to_connect).show()
                    MiboEvent.log(t)
                    t.printStackTrace()
                }

                override fun onResponse(
                    call: Call<ResponseData>,
                    response: Response<ResponseData>
                ) {
                    fragment?.activity?.runOnUiThread {
                        fragment.getDialog()?.dismiss()
                    }

                    val data = response.body()
                    if (data != null) {
                        if (data.status.equals("success", true)) {
                            data.response?.message?.let {
                                Toasty.error(fragment.requireContext(), it).show()
                            }
                            deleteProgramFromDb(program, action)

                        } else if (data.status.equals("error", true)) {
                            checkError(data)
                        }
                    } else {
                        Toasty.error(fragment.requireContext(), R.string.error_occurred).show()
                        fragment.log("deletePrograms : " + response?.errorBody()?.toString())
                    }
                }
            })
    }

    fun deleteProgram2(program: RxlProgram?, action: (RxlProgram?) -> Unit) {
        if (program == null)
            return
        fragment.log("getProgramsServer()")
        val member = Prefs.get(fragment.context).member ?: return

        fragment?.activity?.runOnUiThread {
            fragment.getDialog()?.show()
        }

        API.request.getApi()
            .deleteRXLProgram(DeleteRXLProgram(member.id(), "${program.id}", member.accessToken!!))
            .enqueue(object : Callback<ResponseData> {

                override fun onFailure(call: Call<ResponseData>, t: Throwable) {
                    fragment?.activity?.runOnUiThread {
                        fragment.getDialog()?.dismiss()
                    }
                    t.printStackTrace()
                    Toasty.error(fragment.context!!, R.string.unable_to_connect).show()
                    MiboEvent.log(t)
                    t.printStackTrace()
                }

                override fun onResponse(
                    call: Call<ResponseData>,
                    response: Response<ResponseData>
                ) {
                    fragment?.activity?.runOnUiThread {
                        fragment.getDialog()?.dismiss()
                    }

                    val data = response.body()
                    if (data != null) {
                        if (data.status.equals("success", true)) {
                            data.response?.message?.let {
                                Toasty.error(fragment.requireContext(), it).show()
                            }
                            deleteProgramFromDb(program, action)

                        } else if (data.status.equals("error", true)) {
                            checkError(data)
                        }
                    } else {
                        Toasty.error(fragment.requireContext(), R.string.error_occurred).show()
                        fragment.log("deletePrograms : " + response?.errorBody()?.toString())
                    }
                }
            })
    }

    fun deleteProgramFromDb(
        program: RxlProgram?,
        action: (RxlProgram?) -> Unit
    ) {
        if (program == null)
            return
        Database.execute(Action {
            //fragment.log("Database updateProgram")
            Database.getTempDb(fragment.requireContext()).rxlProgramDao().delete(program)
            action.invoke(program)
        })
    }

    // TODO Filters...........
    //val NO_OF_PODS = IntRange(1, 20)
    //    val NO_OF_PODS = 1..20
    ////    val PROGRAM_TYPE : IntRange = 21..29
    ////    val LIGHT_LOGIC : IntRange = 21..29
    enum class Filter(val range: IntRange) {
        NO_OF_PODS(1..16),
        PROGRAM_TYPE(21..34),
        LIGHT_LOGIC(IntRange(41, 45)),
        PLAYERS(51..54),
        ACCESSORIES(61..71)
    }

    //val selectedItems = HashMap<Int, ReflexFilterAdapter.ReflexFilterModel>()
    val selectedItems = SparseArray<ReflexFilterModel>()
    //val selectedItems = HashMap<Int, ReflexFilterModel>()

    @SuppressLint("CheckResult")
    fun setFilters(view: RecyclerView?, type: Filter) {
        if (view == null)
            return
        val list = ArrayList<ReflexFilterModel>()
        Observable.fromCallable {
            when (type) {

                Filter.PROGRAM_TYPE -> {

                    list.add(ReflexFilterModel(21, "Agility"))
                    // list.add(ReflexFilterModel(22, "Balanced"))
                    list.add(ReflexFilterModel(23, "Core"))
                    list.add(ReflexFilterModel(24, "Cardio"))
                    //list.add(ReflexFilterModel(25, "Coordination"))
                    // list.add(ReflexFilterModel(26, "Fitness Test"))
                    // list.add(ReflexFilterModel(27, "Flexibility"))
                    //list.add(ReflexFilterModel(28, "Functional"))
                    list.add(ReflexFilterModel(29, "Power"))
                    list.add(ReflexFilterModel(30, "Reaction Time"))
                    list.add(ReflexFilterModel(31, "Speed"))
                    list.add(ReflexFilterModel(32, "Stamina"))
                    list.add(ReflexFilterModel(33, "Strength"))

                    //list.add(ReflexFilterModel(34, "Suspension"))
                }
                Filter.NO_OF_PODS -> {
                    for (i in 1..16) {
                        list.add(ReflexFilterModel(i, "$i"))
                    }
                }
                Filter.LIGHT_LOGIC -> {
                    list.add(ReflexFilterModel(41, "Random"))
                    list.add(ReflexFilterModel(42, "Sequence"))
                    list.add(ReflexFilterModel(43, "All at once"))
                    list.add(ReflexFilterModel(44, "Focus"))
                    list.add(ReflexFilterModel(45, "Home Base"))
                }
                Filter.PLAYERS -> {
                    list.add(ReflexFilterModel(51, "1"))
                    list.add(ReflexFilterModel(52, "2"))
                    list.add(ReflexFilterModel(53, "3"))
                    list.add(ReflexFilterModel(54, "4"))
                }
                Filter.ACCESSORIES -> {
                    list.add(ReflexFilterModel(61, "No Accessories"))
                    // list.add(ReflexFilterModel(62, "Battle Rope"))
                    list.add(ReflexFilterModel(63, "Laddar"))
                    list.add(ReflexFilterModel(64, "Medicine Ball"))
                    list.add(ReflexFilterModel(64, "Cones"))
                    //list.add(ReflexFilterModel(65, "Mirror"))
                    // list.add(ReflexFilterModel(66, "Poll"))
                    list.add(ReflexFilterModel(66, "Ball"))
                    //list.add(ReflexFilterModel(67, "Pul Up Bar"))
                    //list.add(ReflexFilterModel(68, "Rig"))
                    list.add(ReflexFilterModel(69, "Suspension Straps"))
                    // list.add(ReflexFilterModel(70, "Tree"))
                    list.add(ReflexFilterModel(71, "Resistance Band"))
                }
            }
        }.subscribe {

            val adapter = ReflexFilterAdapter(list, 3)
            val manager = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
            view.isNestedScrollingEnabled = false
            //view.requestDisallowInterceptTouchEvent(true)

            adapter.setListener(filterListener)
            view.layoutManager = manager
            view.adapter = adapter

        }
    }

    fun dispose(root: View) {
        try {
            if (root is ViewGroup)
                for (i in 0 until root.childCount) {
                    val r = root.getChildAt(i)
                    if (r is RecyclerView) {
                        r.adapter = null
                    }
                }
        } catch (e: Exception) {

        }
    }


    private val filterListener = object : Listener {
        override fun onClick(data: ReflexFilterModel?) {
            if (data != null) {
                fragment.log("filterListener $data")
                selectedItems.put(data.id, data)
                if (data.isSelected) {
                    fragment.log("adding to filter list")
                    selectedItems.put(data.id, data)
                } else {
                    fragment.log("deleting from filter list")
                    selectedItems.delete(data.id)
                }
                fragment.log("filterListener selectedItems $selectedItems")
            }
            // backdropBehavior.open(true)
            //showFilterOptions(data)

        }
    }

    private var isPods = false
    private var isPrograms = false
    private var isPlayers = false
    private var isLightLogic = false
    private var isAccessories = false

    private fun resetFilters() {
        isPods = false
        isPrograms = false
        isPlayers = false
        isLightLogic = false
        isAccessories = false
    }

    fun applyFilters() {
        fragment.log("applyFilters ${selectedItems.size}")
        if (selectedItems.size() == 0) {
            observer?.onUpdateList(programList)
            return
        }

        resetFilters()
        updateFiltersInternal()
//        selectedItems.forEach { key, value ->
//            if (value.isSelected) {
//                updateTypes(key)
//            }
//        }
//        updateFilterList()
    }

    private fun updateFilterList() {
        val filterList = ArrayList<RxlProgram?>()
        if (programList.size > 0) {
            programList?.forEach { program ->
                selectedItems.forEach { key, value ->
                    if (isPods) {
                        if (program!!.pods == getInt(value.title)) {
                            filterList.add(program)
                        }
                    }
                }

                if (isPods || isAccessories || isLightLogic || isPlayers || isPrograms) {

                }
            }
        }
    }

    private fun getInt(value: String): Int = value.toIntOrZero()

    fun binarySearch(array: IntArray, size: Int, value: Int): Int {
        var lo = -1
        var hi = size - 1
        while (lo <= hi) {
            val mid = lo + hi ushr 1
            val midVal = array[mid]
            when {
                midVal < value -> {
                    lo = mid + 1
                }
                midVal > value -> {
                    hi = mid - 1
                }
                else -> {
                    return mid // found
                }
            }
        }
        return lo.inv() // not present
    }

    private fun updateTypes(id: Int) {
        when (id) {
            in Filter.NO_OF_PODS.range -> {
                isPods = true
            }
            in Filter.PROGRAM_TYPE.range -> {
                isPrograms = true
            }
            in Filter.LIGHT_LOGIC.range -> {
                isLightLogic = true
            }
            in Filter.PLAYERS.range -> {
                isPlayers = true
            }
            in Filter.ACCESSORIES.range -> {
                isAccessories = true
            }
        }
    }

    private fun updateFiltersInternal() {
        val pods = ArrayList<ReflexFilterModel>()
        val logics = ArrayList<ReflexFilterModel>()
        val accessories = ArrayList<ReflexFilterModel>()
        val prgs = ArrayList<ReflexFilterModel>()
        val players = ArrayList<ReflexFilterModel>()
        val filterList = ArrayList<RxlProgram>()
        val filterMap = SparseArray<RxlProgram>()
        selectedItems.forEach { key, value ->
            if (value.isSelected) {
                when (key) {
                    in Filter.NO_OF_PODS.range -> {
                        isPods = true
                        pods.add(value)
                    }
                    in Filter.PROGRAM_TYPE.range -> {
                        isPrograms = true
                        prgs.add(value)
                    }
                    in Filter.LIGHT_LOGIC.range -> {
                        isLightLogic = true
                        logics.add(value)
                    }
                    in Filter.PLAYERS.range -> {
                        isPlayers = true
                        players.add(value)
                    }
                    in Filter.ACCESSORIES.range -> {
                        isAccessories = true
                        accessories.add(value)
                    }
                }
            }
        }

        //val l = programList?.filterIndexed { index, program -> program?.type?.contains("") ?: false }


        programList.forEach { prg ->
            if (pods.isNotEmpty()) {
                pods.forEach {
                    if (it.title.toIntOrZero() == prg?.pods) {
                        //filterList.add(prg)
                        filterMap.put(prg.id!!, prg)
                    }
                }
            }

            if (players.isNotEmpty()) {
                players.forEach {
                    if (prg?.players == it.title?.toIntOrZero())
                        filterMap.put(prg.id!!, prg)
                }
            }

            if (prgs.isNotEmpty()) {
                prgs.forEach {
                    //fragment.log("updateFiltersInternal programType ${prg.structure} == ${it.title}")
                    if (prg?.category?.contains(it.title) == true)
                        filterMap.put(prg.id!!, prg)
                }
            }

            if (logics.isNotEmpty()) {
                logics.forEach {
                    if (prg?.logicType()?.contains(it.title) == true)
                        filterMap.put(prg.id!!, prg)
                }
            }

            if (accessories.isNotEmpty()) {
                accessories.forEach {
                    if (it.title?.toLowerCase().contains("no accessories"))
                        filterMap.put(prg?.id!!, prg)
                    else if (prg?.accessories?.contains(it.title) == true)
                        filterMap.put(prg.id!!, prg)
                }
            }
        }
        filterMap.forEach { _, value ->
            filterList.add(value)
        }

        observer?.onUpdateList(filterList)

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
