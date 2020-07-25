/*
 *  Created by Sumeet Kumar on 7/8/20 10:44 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 7/7/20 5:48 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.trainer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.fragment_my_services.*
import life.mibo.android.R
import life.mibo.android.core.API
import life.mibo.android.core.Prefs
import life.mibo.android.models.base.TrainerID
import life.mibo.android.models.trainer.GetServicesOfProfessionals
import life.mibo.android.models.trainer.ProfessionalDetails
import life.mibo.android.models.trainer.TrainerServices
import life.mibo.android.ui.base.BaseFragment
import life.mibo.android.utils.Toasty
import life.mibo.android.utils.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MyServicesFragment : BaseFragment() {

    companion object {
        fun create(type: Int): Bundle {
            val bundle = Bundle()
            bundle.putInt("type_", type)
            return bundle
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_my_services, container, false)
    }

    var isRefreshing = false

    private var type_ = 0
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setSwipeRefreshColors(swipeToRefresh)
        swipeToRefresh?.setOnRefreshListener {
            log("swipeToRefresh?.setOnRefreshListener $isRefreshing")
            isRefreshing = true
            getServices()
        }
        recyclerView?.layoutManager = GridLayoutManager(context, 1)

        type_ = arguments?.getInt("type_", 0) ?: 0

        //  getProfessionals()
//        imageViewFilter?.setOnClickListener {
//            updateGrid()
//        }
        // setHasOptionsMenu(true)
        val member = Prefs.get(context).member
        Utils.loadImage(userImage, member?.profileImg, member?.isMale() ?: true)
        getServices()
    }

    fun showProgress() {
        activity?.runOnUiThread {
            // progressBar?.visibility = View.VISIBLE
            isRefreshing = true
            swipeToRefresh?.isRefreshing = isRefreshing

        }
    }

    fun hideProgress() {
        activity?.runOnUiThread {
            //progressBar?.visibility = View.GONE
            isRefreshing = false
            swipeToRefresh?.isRefreshing = isRefreshing

        }
    }


    var isTrainer = false
    private fun getServices() {
        val member = Prefs.get(context).member ?: return
        // Prefs.get(context).member

        //getmDialog()?.show()
        showProgress()
        API.request.getApi()
            .getProfessionalDetails(
                GetServicesOfProfessionals(
                    GetServicesOfProfessionals.Data(member.id, member.id),
                    member.accessToken
                )
            )
            .enqueue(object : Callback<ProfessionalDetails> {
                override fun onFailure(call: Call<ProfessionalDetails>, t: Throwable) {
                    // getmDialog()?.dismiss()
                    hideProgress()

                }

                override fun onResponse(
                    call: Call<ProfessionalDetails>,
                    response: Response<ProfessionalDetails>
                ) {
                    log("ProfessionalDetails getDetails >> onResponse ")
                    try {
                        val data = response?.body();
                        log("ProfessionalDetails getDetails >> onResponse success $data")
                        if (data != null && data.isSuccess()) {
                            val list = data.data
                            parseData(list)

                        } else {
                            log("ProfessionalDetails getDetails >> onResponse failed ${response.body()}")
                            parseData(null)
                            val er = data?.errors
                            if (er != null)
                                er?.get(0)?.let {
                                    if (it?.code != 404)
                                        Toasty.snackbar(recyclerView, it?.message)
                                }
                            //tv_service_no?.visibility = View.VISIBLE
                            //tv_specialization_no?.visibility = View.VISIBLE
                            //tv_certificate_no?.visibility = View.VISIBLE
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    //getmDialog()?.dismiss()
                    hideProgress()
                }

            })
    }

    @Synchronized
    private fun parseData(list: List<ProfessionalDetails.Data?>?) {
        if (list != null) {
            val dataList = ArrayList<ProfessionalDetailsActivity.ServiceItem>()
            for (i in list) {
                dataList.add(
                    ProfessionalDetailsActivity.ServiceItem(
                        1,
                        i?.name,
                        i?.description,
                        "${i?.currencyType} ${i?.currency}"
                    )
                )
            }
            recyclerView?.layoutManager = GridLayoutManager(this.context, 1)
            recyclerView?.adapter =
                ProfessionalDetailsActivity.ServiceAdapters(0, dataList, null)
            recyclerView?.adapter?.notifyDataSetChanged()

        } else {
            //Toasty.info(requireContext(), getString(R.string.no_service_found)).show()
            tv_service_no?.visibility = View.VISIBLE

        }

//        val spec = data?.specializations
//        val certs = data?.certifications
//        log("parseData  :: $list")
//        log("parseData spec ${spec?.size} : $spec")
//        log("parseData certs ${certs?.size} $certs")
//        val txtColor = ContextCompat.getColor(this, R.color.textColor)
//        if (spec != null && spec.isNotEmpty()) {
//            //val certList = ArrayList<ServiceItem>()
//            chip_group?.visibility = View.VISIBLE
//            for (str in spec) {
//                try {
//                    val chip = Chip(this)
//                    chip.text = str
//                    chip.setTextColor(txtColor)
//                    chip.setChipBackgroundColorResource(R.color.white)
//                    chip.isClickable = true
//                    chip.isCheckable = false
//                    chip_group.addView(chip)
//                } catch (e: java.lang.Exception) {
//                    log("parseData add chip_group ${e.message}")
//                }
//
//            }
//        } else {
//            tv_specialization_no?.visibility = View.VISIBLE
//        }
//
//        if (certs != null && certs.isNotEmpty()) {
//            val certList = ArrayList<ProfessionalDetailsActivity.ServiceItem>()
//            for (c in certs) {
//                log("parseData  add certifications ${c}")
//                certList.add(ProfessionalDetailsActivity.ServiceItem(0, c, "", ""))
//            }
//            recyclerViewCr?.layoutManager = GridLayoutManager(this, 1)
//            recyclerViewCr?.adapter =
//                ProfessionalDetailsActivity.ServiceAdapters(100, certList, null)
//            recyclerViewCr?.adapter?.notifyDataSetChanged()
//        } else {
//            tv_certificate_no?.visibility = View.VISIBLE
//        }

    }


    //New
    private fun getTrainerServices() {
        val member = Prefs.get(context).member ?: return
        // Prefs.get(context).member

        //getmDialog()?.show()
        showProgress()
        API.request.getApi()
            .getTrainerServices(
                TrainerID(
                    member.id,
                    member.accessToken,
                    "GetIndependentProfessionalServices"
                )
            )
            .enqueue(object : Callback<TrainerServices> {
                override fun onFailure(call: Call<TrainerServices>, t: Throwable) {
                    // getmDialog()?.dismiss()
                    hideProgress()

                }

                override fun onResponse(
                    call: Call<TrainerServices>,
                    response: Response<TrainerServices>
                ) {
                    log("ProfessionalDetails getDetails >> onResponse ")
                    try {
                        val data = response?.body();
                        log("ProfessionalDetails getDetails >> onResponse success $data")
                        if (data != null && data.isSuccess()) {
                            val list = data.data
                            //parseData(list)

                        } else {
                            log("ProfessionalDetails getDetails >> onResponse failed ${response.body()}")
                            parseData(null)
                            val er = data?.errors
                            if (er != null)
                                er?.get(0)?.let {
                                    if (it?.code != 404)
                                        Toasty.snackbar(recyclerView, it?.message)
                                }
                            //tv_service_no?.visibility = View.VISIBLE
                            //tv_specialization_no?.visibility = View.VISIBLE
                            //tv_certificate_no?.visibility = View.VISIBLE
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    //getmDialog()?.dismiss()
                    hideProgress()
                }

            })
    }


}