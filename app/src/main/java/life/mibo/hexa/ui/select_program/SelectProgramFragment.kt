/*
 *  Created by Sumeet Kumar on 1/16/20 11:49 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/16/20 8:27 AM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.ui.select_program

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_select_program.*
import life.mibo.hardware.SessionManager
import life.mibo.hardware.events.ChangeColorEvent
import life.mibo.hexa.R
import life.mibo.hexa.core.API
import life.mibo.hexa.core.Prefs
import life.mibo.hexa.events.EventBusEvent
import life.mibo.hexa.models.program.Program
import life.mibo.hexa.models.program.ProgramPost
import life.mibo.hexa.models.program.SearchPrograms
import life.mibo.hexa.database.Database
import life.mibo.hexa.ui.base.BaseFragment
import life.mibo.hexa.ui.base.ItemClickListener
import life.mibo.hexa.ui.main.Navigator
import life.mibo.hexa.utils.Toasty
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SelectProgramFragment : BaseFragment() {


    val programs = ArrayList<Program?>()
    var isProgram = false
    val stateBundle = Bundle()

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View? {
        super.onCreateView(i, c, s)
        log("onCreateView $arguments")
        return i.inflate(R.layout.fragment_select_program, c, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        log("onViewCreated $arguments")
        button_next?.setOnClickListener {
            if (program == null) {
                Toasty.info(context!!, "Please select program").show()
                return@setOnClickListener
            }
            Observable.just<Program>(program).subscribeOn(Schedulers.computation()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Program> {
                    override fun onComplete() {
                        log("Observable onComplete")
                        navigate(Navigator.SESSION, stateBundle)
                    }

                    override fun onSubscribe(d: Disposable) {
                        log("Observable onSubscribe")
                    }

                    override fun onNext(t: Program) {
                        log("Observable onNext $t")
                        SessionManager.getInstance().userSession.program = t.create()
                        Prefs.get(context).settJson("user_program", t)
                    }

                    override fun onError(e: Throwable) {
                        log("Observable onError $e")
                    }

                })
        }
        button_next?.isEnabled = false
        loadPrograms()
        select_program?.setOnClickListener {
            programDialog?.showPrograms()
            //checkDialog()
        }

        select_color?.setOnClickListener {
            colorDialog?.showColors()
        }
        button_next.isEnabled = false

    }


    private var programDialog: ProgramDialog? = null
    private var colorDialog: ProgramDialog? = null

    private fun loadProgramObservables() {
        Single.fromCallable {
            val list = Database.getInstance(requireContext()).programDao().getAll()
            log("loadProgramObservables $list")
            log("loadProgramObservables ${list?.size}")
        }.subscribe()
    }
    private fun loadPrograms() {
        // loadProgramObservables()
        val member =
            Prefs.get(context).member ?: return

        getDialog()?.show()
        val post = ProgramPost(auth = member.accessToken!!)
        API.request.getApi().searchPrograms2(post).enqueue(object :
            Callback<SearchPrograms> {
            override fun onFailure(call: Call<SearchPrograms>, t: Throwable) {
                getDialog()?.dismiss()
                t.printStackTrace()
                Toasty.error(context!!, getString(R.string.unable_to_connect)).show()
            }

            override fun onResponse(
                call: Call<SearchPrograms>,
                response: Response<SearchPrograms>
            ) {
                getDialog()?.dismiss()

                val data = response.body()
                if (data != null) {
                    if (data.status.equals("success", true)) {
                        parse(data.data?.programs)

                    } else if (data.status.equals("error", true)) {
                        Toasty.error(context!!, "${data.errors?.get(0)?.message}").show()
                    }
                } else {
                    Toasty.error(context!!, R.string.error_occurred).show()
                }
            }
        })
    }

    var program: Program? = null
    private fun parse(list: ArrayList<Program?>?) {
        if (list == null)
            return



        programs.clear()
        programs.addAll(list)
        //spinner_program.adapter = ProgramSpinnerAdapter(programs, null);
//        spinner_program.adapter = ProgramArrayAdapter(
//            this.requireContext(),
//            R.layout.list_item_spinner_programs,
//            programs
//        )
        //spinner_program? = 300
//        try {
//            val popup = Spinner::class.java.getDeclaredField("mPopup")
//            popup.isAccessible = true
//
//            // Get private mPopup member variable and try cast to ListPopupWindow
//            val popupWindow = popup.get(spinner_program) as android.widget.ListPopupWindow
//
//            popupWindow.height = 300
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }

        programDialog = ProgramDialog(context!!, programs, object : ItemClickListener<Program> {

            override fun onItemClicked(item: Program?, position: Int) {
                // Toasty.info(context!!, "$position").show()
                item?.name?.let {
                    isProgram = true
                    program = item
                    select_program.text = it
                    button_next.isEnabled = true
                    //stateBundle.putParcelable("program_data", item)
                    stateBundle.putString("program_name", it)
                }
            }

        }, ProgramDialog.PROGRAMS)

        colorDialog =
            ProgramDialog(context!!, ArrayList(), object : ItemClickListener<Program> {

                override fun onItemClicked(item: Program?, position: Int) {
                    //Toasty.info(context!!, "$position").show()

                    item?.id?.let {
                        circleImage?.visibility = View.VISIBLE
                        circleImage?.circleColor = it
                        val d = SessionManager.getInstance().userSession.booster
                        d.colorPalet = it
                        EventBus.getDefault().postSticky(ChangeColorEvent(d, d.uid))
                        stateBundle.putInt("program_color", it)
                    }
                }

            }, ProgramDialog.COLORS)

        // Database.getInstance(context!!).insert(programs)

    }

    override fun onSaveInstanceState(outState: Bundle) {
        log("onSaveInstanceState ${outState?.toString()}")
        super.onSaveInstanceState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        log("onViewStateRestored ${savedInstanceState?.toString()}")

        super.onViewStateRestored(savedInstanceState)
    }

    override fun onPause() {
        log("onPause")
        super.onPause()
    }

    override fun onResume() {
        log("onResume")
        super.onResume()
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    fun onEvents(event: EventBusEvent) {
        val data = event?.data
        if (data is Bundle) {


            val name: String? = data.getString("program_name", null)
            val color: Int = data.getInt("program_color", 0)
            if (color != 0) {
                circleImage?.visibility = View.VISIBLE
                circleImage?.circleColor = color
                //val d = SessionManager.getInstance().userSession.booster
                //d.colorPalet = color
                //EventBus.getDefault().postSticky(ChangeColorEvent(d, d.uid))
                stateBundle.putInt("program_color", color)
            }
            var prg : Program? = null
            if(programs.isNotEmpty()){
                programs?.forEach {
                    if(it?.name == name){
                        prg = it
                    }
                }
            }

            prg?.name?.let {
                isProgram = true
                program = prg
                select_program.text = it
                button_next.isEnabled = true
                stateBundle.putString("program_name", it)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
    }

    override fun onBackPressed(): Boolean {
        log("onBackPressed")
        navigate(Navigator.CLEAR_HOME, null)
        return false
    }

}