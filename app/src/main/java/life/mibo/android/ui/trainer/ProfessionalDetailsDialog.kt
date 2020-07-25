/*
 *  Created by Sumeet Kumar on 5/13/20 12:13 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 5/12/20 4:12 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.trainer

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import kotlinx.android.synthetic.main.fragment_ip_profile2.*
import life.mibo.android.R
import life.mibo.android.core.API
import life.mibo.android.core.Prefs
import life.mibo.android.models.base.UserID
import life.mibo.android.models.trainer.*
import life.mibo.android.ui.base.ItemClickListener
import life.mibo.android.ui.dialog.MyDialog
import life.mibo.android.utils.Toasty
import life.mibo.hardware.core.Logger
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList


class ProfessionalDetailsDialog(var data: Professional) :
    DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_ip_profile2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var invite: Button? = view?.findViewById(R.id.btn_invite)
        var back: ImageButton? = view?.findViewById(R.id.btn_back)
        var userImage: ImageView? = view?.findViewById(R.id.userImage)

        //isCancelable = false

        if (data.avatar != null)
            Glide.with(this).load(data.avatar).fitCenter().error(R.drawable.ic_user_test).into(userImage!!)
        tv_name?.text = data.name
        tv_desg?.text = data.designation
        tv_city?.text = data.city
        tv_country?.text = data.country


        back?.setOnClickListener {

            dismiss()
        }

        val member = Prefs.get(context).member
        if (member!!.isMember()) {
            invite?.visibility = View.VISIBLE
            invite?.isEnabled = data.linked != "1"

            invite?.setOnClickListener {
                inviteIP(data.id)
                // dismiss()
            }
        } else {
            invite?.visibility = View.INVISIBLE
        }

        Logger.e("ProfessionalDetails ${data.id} >> $data")
        getDetails(data.id)
    }


    var mDialog: MyDialog? = null

    fun getmDialog(): MyDialog? {
        if (mDialog == null)
            mDialog = MyDialog.get(requireContext())
        return mDialog
    }

    fun showProgress() {
        progressBar?.visibility = View.VISIBLE
    }

    fun hideProgress() {
        progressBar?.visibility = View.GONE
    }

    var isTrainer = false
    private fun getDetails(userId: Int?) {
        if (userId == null) {
            return
        }
        // Prefs.get(context).member

        //getmDialog()?.show()
        showProgress()
        API.request.getApi()
            .getProfessionalDetails(
                GetServicesOfProfessionals(
                    GetServicesOfProfessionals.Data(Prefs.get(context).member?.id, userId),
                    Prefs.get(context).member?.accessToken
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
                    Logger.e("ProfessionalDetails getDetails >> onResponse ")
                    try {
                        val data = response?.body();
                        Logger.e("ProfessionalDetails getDetails >> onResponse success $data")
                        if (data != null && data.isSuccess()) {
                            val list = data.data
                            parseData(list)
                            isTrainer = true

                        } else {
                            Logger.e("ProfessionalDetails getDetails >> onResponse failed ${response.body()}")
                            parseData(null)
                            val er = data?.errors
                            if (er != null)
                                er?.get(0)?.message?.let {
                                    Toasty.snackbar(view, it)
                                }
                            //tv_service_no?.visibility = View.VISIBLE
                            //tv_specialization_no?.visibility = View.VISIBLE
                            //tv_certificate_no?.visibility = View.VISIBLE
                        }
                    } catch (e: Exception) {

                    }
                    //getmDialog()?.dismiss()
                    hideProgress()
                }

            })
    }

    private fun parseData(list: List<ProfessionalDetails.Data?>?) {
        if (list != null) {
            val dataList = ArrayList<ServiceItem>()
            for (i in list) {
                dataList.add(
                    ServiceItem(
                        1,
                        i?.name,
                        i?.description,
                        "${i?.currencyType} ${i?.currency}"
                    )
                )
            }
            recyclerView?.layoutManager = GridLayoutManager(context, 1)
            recyclerView?.adapter =
                ServiceAdapters(0, dataList, null)
            recyclerView?.adapter?.notifyDataSetChanged()

        } else {
            //Toasty.info(requireContext(), getString(R.string.no_service_found)).show()
            tv_service_no?.visibility = View.VISIBLE

        }

        val spec = data?.specializations
        val certs = data?.certifications
        Logger.e("parseData  :: $list")
        Logger.e("parseData ?? $spec")
        Logger.e("parseData >> $certs")
        if (spec != null && spec.isNotEmpty()) {

            //val certList = ArrayList<ServiceItem>()
            for (c in spec) {
                c?.let {
                    //Logger.e("parseData  Specializations $c")
                    val chip = Chip(chip_group.context)
                    chip.text = "$c"
                    chip.setChipBackgroundColorResource(R.color.white)
                    chip.isClickable = true
                    chip.isCheckable = false
                    chip_group.addView(chip)
                }
                // certList.add(ServiceItem(1, c?.name, "", ""))
            }
            //Logger.e("parseData  Specializations $c")
//            recyclerViewSp?.layoutManager =
//                StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
//            //recyclerViewSp?.layoutManager = ChipsLayoutManager
//            recyclerViewSp?.adapter =
//                ServiceAdapters(200, certList, null)
//            recyclerViewSp?.adapter?.notifyDataSetChanged()
        } else {
            tv_specialization_no?.visibility = View.VISIBLE
        }

        if (certs != null && certs.isNotEmpty()) {
            val certList = ArrayList<ServiceItem>()
            for (c in certs) {
                certList.add(ServiceItem(1, c?.certificationName, "", ""))
            }
            recyclerViewCr?.layoutManager = GridLayoutManager(context, 1)
            recyclerViewCr?.adapter =
                ServiceAdapters(100, certList, null)
            recyclerViewCr?.adapter?.notifyDataSetChanged()
        } else {
            tv_certificate_no?.visibility = View.VISIBLE
        }

    }

    private fun inviteIP(userId: Int?) {
        if (userId == null) {
            return
        }
        val member = Prefs.get(context).member

        getmDialog()?.show()

        API.request.getApi()
            .inviteProfessional(
                InviteProfessional(
                    InviteProfessional.Data(member?.id(), "$userId"),
                    member?.accessToken
                )
            )
            .enqueue(object : Callback<TrainerInviteResponse> {
                override fun onFailure(call: Call<TrainerInviteResponse>, t: Throwable) {
                    getmDialog()?.dismiss()
                    // hideProgress()
                }

                override fun onResponse(
                    call: Call<TrainerInviteResponse>, response: Response<TrainerInviteResponse>
                ) {
                    getmDialog()?.dismiss()
                    // hideProgress()
                    val data = response?.body();
                    if (data != null && data.isSuccess()) {
                        //parseData(data.data?.professionals)
                        data.data?.get(0)?.message?.let {
                            Toasty.info(requireContext(), it).show()
                        }
                        dismiss()
                    } else {
                        val er = data?.errors
                        if (er != null)
                            er?.get(0)?.message?.let {
                                Toasty.snackbar(view, it)
                            }
                    }

                }

            })
    }


    class ServiceAdapters(
        var type: Int = 0,
        val list: ArrayList<ServiceItem>,
        val listener: ItemClickListener<ProfessionalDetails.Data>?
    ) : RecyclerView.Adapter<ServiceHolder>() {
        val formatter = DecimalFormat("#,###");

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceHolder {
            return ServiceHolder(
                LayoutInflater.from(
                    parent.context
                ).inflate(R.layout.list_item_trainer_services, parent, false)
            )
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: ServiceHolder, position: Int) {
            formatter.currency = Currency.getInstance("AED")
            holder.bind(list[position], formatter, listener, type)

        }

    }

    data class ServiceItem(val id: Int, var name: String?, val desc: String?, var amount: String?)

    class ServiceHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView? = itemView.findViewById(R.id.tv_service)
        val sessions: TextView? = itemView.findViewById(R.id.tv_service_session)
        val amount: TextView? = itemView.findViewById(R.id.tv_service_amount)
        // val chip: Chip? = itemView.findViewById(R.id.chipText)
        //val img: ImageView? = itemView.findViewById(R.id.imageView)

        fun bind(
            item: ServiceItem?,
            format: DecimalFormat,
            listener: ItemClickListener<ProfessionalDetails.Data>?, type: Int
        ) {
            if (item != null) {
                if (type == 100) {
                    name?.text = item.name
                    sessions?.visibility = View.GONE
                    amount?.visibility = View.GONE
                    return
                }

                name?.text = item.name
                sessions?.text = "Description: ${item.desc}"
                //sessions?.text = "Sessions #   ${item.noOfSession}"

                amount?.text = "Charges:   ${item.amount}"
                //amount?.text = "Charges:    AED ${format.format(item.currency)}"
            }

        }

    }

    override fun onStart() {
        super.onStart()
        if (dialog != null) {
            dialog?.window
                ?.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }
}