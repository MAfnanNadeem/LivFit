package life.mibo.hexa.ui.rxl

import life.mibo.hexa.R
import life.mibo.hexa.core.API
import life.mibo.hexa.core.Prefs
import life.mibo.hexa.models.circuits.CircuitResponse
import life.mibo.hexa.models.circuits.Data
import life.mibo.hexa.models.circuits.SearchCircuit
import life.mibo.hexa.ui.base.BaseController
import life.mibo.hexa.ui.base.BaseFragment
import life.mibo.hexa.ui.main.MiboEvent
import life.mibo.hexa.utils.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReactionLightController(val fragment: BaseFragment) :
    BaseController(fragment.requireActivity()), ReactionLightFragment2.Listener {
    override fun onStart() {

    }

    override fun onStop() {

    }

    fun getPrograms() {
        val member = Prefs.get(fragment.context).member ?: return

        fragment.getDialog()?.show()

        API.request.getApi()
            .getCircuits(SearchCircuit(Data(), member.accessToken!!))
            .enqueue(object : Callback<CircuitResponse> {

                override fun onFailure(call: Call<CircuitResponse>, t: Throwable) {
                    fragment.getDialog()?.dismiss()
                    t.printStackTrace()
                    Toasty.error(fragment.context!!, R.string.unable_to_connect).show()
                    MiboEvent.log(t)
                    t.printStackTrace()
                }

                override fun onResponse(
                    call: Call<CircuitResponse>,
                    response: Response<CircuitResponse>
                ) {
                    fragment.getDialog()?.dismiss()

                    val data = response.body()
                    if (data != null) {
                        if (data.status.equals("success", true)) {


                        } else if (data.status.equals("error", true)) {
                            checkError(data)
                        }
                    } else {
                        Toasty.error(fragment.requireContext(), R.string.error_occurred).show()
                        fragment.log("SaveSession : " + response?.errorBody()?.toString())
                    }
                }
            })

    }
}
