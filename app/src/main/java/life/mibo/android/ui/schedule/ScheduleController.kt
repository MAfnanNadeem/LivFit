/*
 *  Created by Sumeet Kumar on 1/20/20 11:40 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/20/20 11:40 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.schedule

import life.mibo.android.R
import life.mibo.android.core.API
import life.mibo.android.core.Prefs
import life.mibo.android.models.create_session.BookSession
import life.mibo.android.models.create_session.BookSessionPost
import life.mibo.android.models.create_session.Data
import life.mibo.android.models.program.ProgramPostData
import life.mibo.android.models.program.Program
import life.mibo.android.models.program.ProgramPost
import life.mibo.android.models.program.SearchPrograms
import life.mibo.android.ui.main.MiboEvent
import life.mibo.android.utils.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class ScheduleController(var fragment: ScheduleFragment) {

    fun bookSession(programId: Int, date: Date) {
        val member = Prefs.get(fragment.context).member ?: return

        fragment.getDialog()?.show()
        SimpleDateFormat.getDateInstance()
        val post = Data(
            SimpleDateFormat("yyyy-MM-dd").format(date),
            SimpleDateFormat("hh:mm:ss").format(date), member.id, programId
        )

        API.request.getApi().bookSession(BookSessionPost(post = post, auth = member.accessToken!!))
            .enqueue(object : Callback<BookSession> {

                override fun onFailure(call: Call<BookSession>, t: Throwable) {
                    fragment.getDialog()?.dismiss()
                    t.printStackTrace()
                    Toasty.error(fragment.context!!, R.string.unable_to_connect).show()
                    MiboEvent.log(t)
                }

                override fun onResponse(
                    call: Call<BookSession>,
                    response: Response<BookSession>
                ) {
                    fragment.getDialog()?.dismiss()

                    val data = response.body()
                    if (data != null) {
                        if (data.status.equals("success", true)) {
                            Toasty.info(
                                fragment.requireContext(),
                                "${data.data?.message}",
                                Toasty.LENGTH_SHORT,
                                false
                            ).show()
                            var sessionId = "${data.data?.sessionID}"

                        } else if (data.status.equals("error", true)) {
                            Toasty.error(
                                fragment.requireContext(),
                                "${data.errors?.get(0)?.message}"
                            ).show()

                            MiboEvent.log("bookAndStartConsumerSession :: error $data")
                        }
                    } else {
                        Toasty.error(fragment.requireContext(), R.string.error_occurred).show()
                    }
                }
            })
    }

    private fun loadPrograms() {
        val member =
            Prefs.get(fragment.context).member ?: return

        fragment.getDialog()?.show()
        val post =
            ProgramPost(item = ProgramPostData(), auth = member.accessToken!!, type = "SearchPrograms")
        API.request.getApi().searchPrograms2(post).enqueue(object :
            Callback<SearchPrograms> {
            override fun onFailure(call: Call<SearchPrograms>, t: Throwable) {
                fragment.getDialog()?.dismiss()
                t.printStackTrace()
                Toasty.error(fragment.context!!, fragment.getString(R.string.unable_to_connect))
                    .show()
            }

            override fun onResponse(
                call: Call<SearchPrograms>,
                response: Response<SearchPrograms>
            ) {
                fragment.getDialog()?.dismiss()

                val data = response.body()
                if (data != null) {
                    if (data.status.equals("success", true)) {
                        parse(data.data?.programs)

                    } else if (data.status.equals("error", true)) {
                        Toasty.error(fragment.context!!, "${data.errors?.get(0)?.message}").show()
                    }
                } else {
                    Toasty.error(fragment.context!!, R.string.error_occurred).show()
                }
            }
        })
    }

    fun parse(programs: ArrayList<Program?>?) {

    }
}