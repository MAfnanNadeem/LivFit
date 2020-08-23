/*
 *  Created by Sumeet Kumar on 6/7/20 9:53 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 6/7/20 9:53 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.trainer

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.fragment_ip_profile2.*
import life.mibo.android.R
import life.mibo.android.core.API
import life.mibo.android.core.Prefs
import life.mibo.android.models.login.Member
import life.mibo.android.models.trainer.GetServicesOfProfessionals
import life.mibo.android.models.trainer.InviteProfessional
import life.mibo.android.models.trainer.ProfessionalDetails
import life.mibo.android.models.trainer.TrainerInviteResponse
import life.mibo.android.ui.base.ItemClickListener
import life.mibo.android.ui.dialog.MyDialog
import life.mibo.android.utils.Toasty
import life.mibo.android.utils.Utils
import life.mibo.hardware.core.Logger
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList

class ProfessionalDetailsActivity : AppCompatActivity() {

    companion object {

        fun launch(context: Context, item: Professional, type: Int) {
            //Logger.e("ProfessionalDetailsActivity launch $type")
            //Logger.e("ProfessionalDetailsActivity launch item $item")
            val i = Intent(context, ProfessionalDetailsActivity::class.java)
            i.putExtra("product_type", type)
            i.putExtra("product_data", item)
            context.startActivity(i)
        }

        fun create(item: life.mibo.android.models.trainer.Professional): Professional {

            val data = Professional(
                item.avatar,
                item.designation,
                item.gender,
                item.id,
                item.name,
                item.description,
                item.phone,
                item.country,
                item.city,
                item.linked
            )


            val certs = ArrayList<String>()
            val specs = ArrayList<String>()
            val c = item.certifications
            if (c != null && c.isNotEmpty()) {
                for (i in c) {
                    //certs[i?.certificationName ?: ""] = i?.id ?: 0
                    certs.add(i?.certificationName ?: "")
                }
                data.certifications = certs
            }

            try {
                val s = item.specializations
                if (s is Collections || s is List<*>) {
                    val ss = s as List<*>
                    if (ss != null && ss.isNotEmpty()) {
                        for (i in s) {
                            i?.let {
                                if (it is String)
                                    specs.add(it)
                            }
                            // if (i?.value == "1")
                            //specs[i.name ?: ""] = i.value ?: "1"
                        }
                        data.specializations = specs
                    }
                }


            } catch (e: java.lang.Exception) {

            }
            return data
        }
    }

    var data: Professional? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_ip_profile2)
        //window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        window?.statusBarColor = Color.WHITE


        log("onCreate $savedInstanceState")
        //Logger.e("onCreate savedInstanceState", intent?.extras)
        if (intent?.getIntExtra("product_type", 7) == 9) {
            data = intent?.getParcelableExtra("product_data") as Professional?
            if (data != null) {
                setup(data!!)
                return
            }
        }
        finish()
    }


    private var professional: Professional? = null
    private fun setup(data: Professional) {
        Utils.loadImage(userImage, data.avatar, data.gender?.toLowerCase() == "male")
        //if (data.avatar != null)
        //  Glide.with(this).load(data.avatar).fitCenter().error(R.drawable.ic_user_test).into(userImage!!)
        //val height = resources?.displayMetrics?.heightPixels?.times(0.5);
        //constraintLayout1?.height = height
        professional = data
        tv_name?.text = data.name
        tv_desg?.text = data.designation
        tv_city?.text = data.city
        tv_country?.text = data.country


        btn_back?.setOnClickListener {
            finish()
        }

        val member = Prefs.get(this).member
        if (member!!.isMember()) {
            btn_invite?.visibility = View.VISIBLE
            //btn_invite?.isEnabled = data.linked != "1"

            btn_invite?.setOnClickListener {
                if (isIpConnected)
                    return@setOnClickListener
                inviteDialog()
                //inviteIP(data.id, member)
                // dismiss()
            }
        } else {
            btn_invite?.visibility = View.GONE
        }

        log("ProfessionalDetails ${data.id} >> $data")
        getDetails(data.id, member)
    }

    fun inviteDialog() {
        //MaterialAlertDialogBuilder
        val d =
            MaterialAlertDialogBuilder(this).setMessage(getString(R.string.biometric_share_info))
                .setPositiveButton(R.string.yes_text)
                { dialog, which ->
                    inviteIP(professional?.id, Prefs.get(this).member!!, 1)
                }.setNegativeButton(R.string.no_text)
                { dialog, which ->
                    inviteIP(professional?.id, Prefs.get(this).member!!, 0)
                }.create()
        d.show()
    }


    fun showProgress() {
        progressBar?.visibility = View.VISIBLE
    }

    fun hideProgress() {
        progressBar?.visibility = View.GONE
    }

    var isTrainer = false
    private fun getDetails(userId: Int?, member: Member) {
        if (userId == null) {
            return
        }
        // Prefs.get(context).member

        //getmDialog()?.show()
        showProgress()
        API.request.getApi()
            .getProfessionalDetails(
                GetServicesOfProfessionals(
                    GetServicesOfProfessionals.Data(member.id, userId),
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
                            runOnUiThread {
                                parseData(list)
                                log("parseData finish3ed")
                                updateButton(data?.connected, data?.invited)
                            }
                            isTrainer = true

                        } else {
                            log("ProfessionalDetails getDetails >> onResponse failed ${response.body()}")
                            runOnUiThread {
                                parseData(null)
                                updateButton(data?.connected, data?.invited)
                            }
                            val er = data?.errors
                            if (er != null)
                                er?.get(0)?.let {
                                    if (it?.code != 404)
                                        Toasty.snackbar(btn_invite, it?.message)
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
            recyclerView?.layoutManager = GridLayoutManager(this, 1)
            recyclerView?.adapter =
                ServiceAdapters(0, dataList, null)
            recyclerView?.adapter?.notifyDataSetChanged()

        } else {
            //Toasty.info(requireContext(), getString(R.string.no_service_found)).show()
            tv_service_no?.visibility = View.VISIBLE

        }

        val spec = data?.specializations
        val certs = data?.certifications
        log("parseData  :: $list")
        log("parseData spec ${spec?.size} : $spec")
        log("parseData certs ${certs?.size} $certs")
        val txtColor = ContextCompat.getColor(this, R.color.textColor)
        if (spec != null && spec.isNotEmpty()) {
            //val certList = ArrayList<ServiceItem>()
            chip_group?.visibility = View.VISIBLE
            for (str in spec) {
                try {
                    val chip = Chip(this)
                    chip.text = str
                    chip.setTextColor(txtColor)
                    chip.setChipBackgroundColorResource(R.color.white)
                    chip.isClickable = true
                    chip.isCheckable = false
                    chip_group.addView(chip)
                } catch (e: java.lang.Exception) {
                    log("parseData add chip_group ${e.message}")
                }

            }
        } else {
            tv_specialization_no?.visibility = View.VISIBLE
        }

        if (certs != null && certs.isNotEmpty()) {
            val certList = ArrayList<ServiceItem>()
            for (c in certs) {
                log("parseData  add certifications ${c}")
                certList.add(ServiceItem(0, c, "", ""))
            }
            recyclerViewCr?.layoutManager = GridLayoutManager(this, 1)
            recyclerViewCr?.adapter =
                ServiceAdapters(100, certList, null)
            recyclerViewCr?.adapter?.notifyDataSetChanged()
        } else {
            tv_certificate_no?.visibility = View.VISIBLE
        }

    }

    var isIpConnected = true
    fun updateButton(connected: Int?, invited: Int?) {
        if (btn_invite.isEnabled && connected == 1) {
            btn_invite?.isEnabled = false
            btn_invite?.setText(R.string.invited_already)
            isIpConnected = true
        } else if (btn_invite.isEnabled && invited == 1) {
            btn_invite?.isEnabled = false
            btn_invite?.setText(R.string.invite_sent)
            isIpConnected = true
        } else {
            btn_invite?.isEnabled = true
            btn_invite?.setText(R.string.invite_me)
            isIpConnected = false
        }
        //Toasty.snackbar(btn_invite, "connected $connected")
    }

    private fun inviteIP(userId: Int?, member: Member, bio: Int = 0) {
        if (userId == null) {
            return
        }
        //val member = Prefs.get(this).member

        getDialog()?.show()

        API.request.getApi()
            .inviteProfessional(
                InviteProfessional(
                    InviteProfessional.Data(member?.id(), "$userId"),
                    member?.accessToken
                )
            )
            .enqueue(object : Callback<TrainerInviteResponse> {
                override fun onFailure(call: Call<TrainerInviteResponse>, t: Throwable) {
                    getDialog()?.dismiss()
                    // hideProgress()
                }

                override fun onResponse(
                    call: Call<TrainerInviteResponse>, response: Response<TrainerInviteResponse>
                ) {
                    getDialog()?.dismiss()
                    // hideProgress()
                    val data = response?.body();
                    if (data != null && data.isSuccess()) {
                        //parseData(data.data?.professionals)
                        data.data?.get(0)?.message?.let {
                            Toasty.grey(this@ProfessionalDetailsActivity, getString(R.string.invitation_sent, "${professional?.name}")).show()
                        }
                        finish()
                    } else {
                        val er = data?.errors
                        if (er != null)
                            er?.get(0)?.message?.let {
                                Toasty.snackbar(btn_invite, it)
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


    data class Professional(
        var avatar: String?,
        var designation: String?,
        var gender: String?,
        var id: Int?,
        var name: String?,
        var description: String?,
        var phone: String?,
        var country: String? = "",
        var city: String? = "",
        var linked: String? = "0"
    ) : Parcelable {

        //var specializations: Map<String, String>? = null
        //var certifications: Map<String, Int>? = null
        var specializations = ArrayList<String>()
        var certifications = ArrayList<String>()

        constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readInt(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString()
        ) {
            specializations = ArrayList<String>()
            certifications = ArrayList<String>()
            //parcel.readMap(specializations, HashMap::class.java.classLoader)
            //parcel.readMap(certifications, HashMap::class.java.classLoader)
            specializations = parcel.createStringArrayList() as ArrayList<String>
            certifications = parcel.createStringArrayList() as ArrayList<String>

        }


        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(avatar)
            parcel.writeString(designation)
            parcel.writeString(gender)
            parcel.writeInt(id ?: 0)
            parcel.writeString(name)
            parcel.writeString(description)
            parcel.writeString(phone)
            parcel.writeString(country)
            parcel.writeString(city)
            parcel.writeString(linked)
            parcel.writeStringList(specializations)
            parcel.writeStringList(certifications)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<Professional> {
            override fun createFromParcel(parcel: Parcel): Professional {
                return Professional(parcel)
            }

            override fun newArray(size: Int): Array<Professional?> {
                return arrayOfNulls(size)
            }
        }


    }

    class Specializations(
        var name: String?,
        var value: String?
    ) : Parcelable {
        constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString()
        ) {
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(name)
            parcel.writeString(value)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<Specializations> {
            override fun createFromParcel(parcel: Parcel): Specializations {
                return Specializations(parcel)
            }

            override fun newArray(size: Int): Array<Specializations?> {
                return arrayOfNulls(size)
            }
        }
    }

    class Certifications(
        var certificateNo: String?,
        var certificationName: String?,
        var id: Int?
    ) : Parcelable {
        constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readInt()
        ) {
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(certificateNo)
            parcel.writeString(certificationName)
            parcel.writeValue(id)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<Certifications> {
            override fun createFromParcel(parcel: Parcel): Certifications {
                return Certifications(parcel)
            }

            override fun newArray(size: Int): Array<Certifications?> {
                return arrayOfNulls(size)
            }
        }

    }

    var mDialog: MyDialog? = null

    fun getDialog(): MyDialog? {
        if (mDialog == null)
            mDialog = MyDialog.get(this)
        return mDialog
    }

    fun log(msg: String) {
        Logger.e("${this.javaClass} : $msg")
    }
}