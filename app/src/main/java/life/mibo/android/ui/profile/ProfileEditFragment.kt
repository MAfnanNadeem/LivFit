/*
 *  Created by Sumeet Kumar on 1/28/20 8:52 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/28/20 8:52 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.profile

import android.Manifest
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlinx.android.synthetic.main.fragment_profile_edit.*
import life.mibo.android.R
import life.mibo.android.core.API
import life.mibo.android.core.Prefs
import life.mibo.android.libs.datepicker.SpinnerDatePickerDialogBuilder
import life.mibo.android.models.base.ResponseData
import life.mibo.android.models.member.SaveMemberAvatar
import life.mibo.android.models.user_details.UpdateMemberDetails
import life.mibo.android.ui.base.BaseFragment
import life.mibo.android.ui.base.PermissionHelper
import life.mibo.android.ui.main.Navigator
import life.mibo.android.utils.Toasty
import life.mibo.android.utils.Utils
import life.mibo.hardware.core.Logger
import life.mibo.imagepicker.RxGalleryFinalApi
import life.mibo.imagepicker.rxbus.RxBusResultDisposable
import life.mibo.imagepicker.rxbus.event.ImageRadioResultEvent
import life.mibo.imagepicker.ui.base.IRadioImageCheckedListener
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class ProfileEditFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_profile_edit, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.setProfile()

        setHasOptionsMenu(true)
    }


    private fun setProfile() {
        val member = Prefs.get(context).member ?: return
        et_fname?.setText(member.firstName)
        et_lname?.setText(member.lastName)
        et_email?.setText(Prefs.get(context).get("user_email"))

        try {
            val format = SimpleDateFormat("yyyy-MM-dd")
            val date = Calendar.getInstance()
            date.time = format?.parse(member.dob)

            dobUpdate = String.format(
                "%02d/%02d/%d",
                date.get(Calendar.DAY_OF_MONTH),
                date.get(Calendar.MONTH).plus(1),
                date.get(Calendar.YEAR)
            )
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            dobUpdate = member.dob ?: ""
        }

        tv_dob2?.setText(dobUpdate)
        et_number.setText(member.contact)
        et_city.setText(member.city)
        tv_country2.setText(member.country)

        et_number?.keyListener = null
        et_number?.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                toastNotEditable()
            }
        }

        et_email?.keyListener = null
        et_email?.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                toastNotEditable()
            }
        }

        // et_dob?.isClickable = false
        //et_country?.isClickable = false


        tv_change_pwd?.setOnClickListener {
            log("ChangePassword")
            ChangePasswordDialog(requireContext()).show()
        }

        tv_dob1?.setOnClickListener {
            showDobPicker()
        }
        tv_dob2?.setOnClickListener {
            showDobPicker()
        }

        tv_country1?.setOnClickListener {
            ccp?.showCountryCodePickerDialog()
        }
        tv_country2?.setOnClickListener {
            ccp?.showCountryCodePickerDialog()
        }

        profile_pic_uploaded?.setOnClickListener {
            log("Profile image click")
            showProfilePickerDialog()
            //openPicker()
        }

        ccp?.setOnCountryChangeListener {
            tv_country2?.setText(it.name)
        }

        isMale = member.isMale()
        profileUrl = member.profileImg ?: ""
        Utils.loadImage(userImage, member?.profileImg, member.isMale())

    }


    var dobUpdate = ""
    private var isMale = true
    private var profileUrl = ""
    fun showDobPicker() {
        var year = 2000
        var day = 1
        var month = 2
        try {
            val sp = dobUpdate.split("/")
            day = sp.get(0).toIntOrNull() ?: 1
            month = sp.get(1).toIntOrNull() ?: 2
            year = sp.get(2).toIntOrNull() ?: 2000
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        SpinnerDatePickerDialogBuilder()
            .context(context)
            .callback { _, year, monthOfYear, dayOfMonth ->
                //tv_dob.setText("$dayOfMonth/$monthOfYear/$year")
//                String.format(
//                    "%02d/%02d/%d",
//                    dayOfMonth,
//                    monthOfYear.plus(1),
//                    year
//                )
                dobUpdate = String.format(
                    "%02d/%02d/%d",
                    dayOfMonth,
                    monthOfYear.plus(1),
                    year
                )
                tv_dob2?.setText(dobUpdate)
                //dob?.text = String.format("%02d/%02d/%d", dayOfMonth, monthOfYear.plus(1), year)
            }
            .spinnerTheme(R.style.DatePickerSpinner)
            .showTitle(true)
            .showDaySpinner(true)
            .defaultDate(year, month.minus(1), day)
            .maxDate(2015, 0, 1)
            .minDate(1950, 0, 1)
            .build()
            .show()
    }

    private fun showProfilePickerDialog() {
        val options = arrayOf(
            getString(R.string.view_photo),
            getString(R.string.change_photo),
            getString(R.string.remove_photo)
        )
        val builder =
            AlertDialog.Builder(requireContext())
        builder.setTitle("")
        builder.setItems(options) { dialog, which ->
            when (which) {
                0 -> {
                    viewPhoto(profileUrl)
                }
                1 -> {
                    //change
                    openPicker()
                }
                2 -> {
                    removePhoto()
                }
            }
        }
        builder.show()
    }

    private fun viewPhoto(url: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("")
        builder.setMessage("")
        builder.setView(R.layout.image_dialog)

        val d = builder.create()
        d.setOnShowListener {
            Utils.loadImage(d.findViewById(R.id.user_image), url, isMale)
        }
        d.show()
        d.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    private fun removePhoto() {
        val builder = AlertDialog.Builder(requireContext(), R.style.AlertDialogPhoto)
        builder.setTitle(R.string.remove_photo)
        builder.setMessage(R.string.remove_photo_hint)
        builder.setPositiveButton(R.string.yes_text) { dialog, which ->
            Toasty.snackbar(userImage, getString(R.string.remove_photo_success))
        }
        builder.setNegativeButton(R.string.no_text) { dialog, which -> }

        builder.show()
    }

    private val permissions = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA
    )

    private fun openPicker() {
        PermissionHelper.requestPermission(this@ProfileEditFragment, permissions) {
            log("requestPermission")
            openPicker2()
        }
        log("openPicker")
    }

    private fun openPicker2() {
        log("openPicker called")
        RxGalleryFinalApi.getInstance(requireActivity())
            .openGalleryRadioImgDefault(object : RxBusResultDisposable<ImageRadioResultEvent>() {
                override fun onEvent(t: ImageRadioResultEvent?) {
                    Logger.e("openPicker RxGalleryFinalApi onEvent $t")
                }
            })
            .onCropImageResult(object : IRadioImageCheckedListener {

                override fun isActivityFinish(): Boolean {
                    Logger.e("openPicker RxGalleryFinalApi isActivityFinish")
                    return true
                }

                override fun cropAfter(file: File?) {
                    Logger.e("openPicker RxGalleryFinalApi cropAfter $file")
                    uploadPicApi("", file)
                }
            })
    }


    private fun uploadPicApi(base64: String, file: File?) {
        log("uploadPicApi $file")
        log("bitmapToBase64 uploadPicApi ${base64.length} : ${base64?.length?.div(1024)}")
        if (file == null) {
            Toasty.snackbar(view, "Invalid image path")
            return
        }
        getDialog()?.show()
//        val filePart: MultipartBody.Part = MultipartBody.Part.createFormData(
//            "file",
//            file.name,
//            RequestBody.create(MediaType.parse("image/*"), file)
//        )
        val member = Prefs.get(context).member ?: return

        val map: HashMap<String, RequestBody?> = HashMap()

        map.put("token", toRequestBody(member.accessToken))
        map.put("RequestType", toRequestBody("SaveMemberAvatar"))
        map.put("MemberID", toRequestBody(member.id()))
        val fileBody: RequestBody = file.asRequestBody("image/*".toMediaType())
        //val fileBody: RequestBody = RequestBody.create("image/png".toMediaType()), file)
        map.put("Avatar\"; filename=\".png\"", fileBody)

        //.uploadAvatar(filePart, member.accessToken, "SaveMemberAvatar", member.id())
        API.request.getApi().uploadAvatar(map)
            .enqueue(object : Callback<SaveMemberAvatar> {
                override fun onFailure(call: Call<SaveMemberAvatar>, t: Throwable) {
                    getDialog()?.dismiss()
                    Toasty.error(requireContext(), R.string.unable_to_connect).show()
                }

                override fun onResponse(
                    call: Call<SaveMemberAvatar>,
                    response: Response<SaveMemberAvatar>
                ) {
                    getDialog()?.dismiss()


                    val data = response?.body()
                    if (data != null && data?.isSuccess()) {
                        val memberr = Prefs.get(context).member
                        memberr?.profileImg = data?.data?.profile
                        Prefs.get(context).member = memberr
                        showPicture(file)
                        navigate(
                            life.mibo.android.ui.main.Navigator.PIC_UPLOADED,
                            data?.data?.profile
                        )
                    }
                    log("onResponse data $data")
                    data?.data?.message?.let {
                        log("onResponse message $it")
                        Toasty.snackbar(view, it).show()
                    }
                    log("onResponse end....")
                }

            })
    }

    fun toRequestBody(value: String?): RequestBody? {
        return value?.toRequestBody("text/plain".toMediaType())
    }

    fun showPicture(file: File?) {
        file?.let {
            Glide.with(this@ProfileEditFragment).load(it).override(300)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        Logger.e("openPicker onLoadFailed glide ")
                        return true
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        Logger.e("openPicker onResourceReady glide ")
                        userImage?.setImageDrawable(resource)
                        return true
                    }

                }).into(userImage)
            Logger.e("openPicker RxGalleryFinalApi glide loading")
        }
    }

    fun toastNotEditable() {
        Toasty.snackbar(tv_dob2, getString(R.string.not_editable))
    }

    fun error(msg: String) {
        Toasty.snackbar(tv_dob2, msg)
    }


    fun saveDateApi() {

        if (et_fname?.text?.toString().isNullOrEmpty()) {
            error(getString(R.string.enter_fname))
            return
        }

        if (et_lname?.text?.toString().isNullOrEmpty()) {
            error(getString(R.string.enter_lname))
            return
        }
        if (et_email?.text?.toString().isNullOrEmpty()) {
            error(getString(R.string.enter_email))
            return
        }


        if (et_city?.text?.toString().isNullOrEmpty()) {
            error(getString(R.string.enter_city))
            return
        }

        if (tv_country2?.text?.toString().isNullOrEmpty()) {
            error(getString(R.string.select_your_country))
            return
        }
        if (et_number?.text?.toString().isNullOrEmpty()) {
            error(getString(R.string.enter_number))
            return
        }
//        if (areaCode.isNullOrEmpty()) {
//            error(getString(R.string.enter_area_number))
//            return
//        }

//        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//            Toasty.warning(context, getString(R.string.email_not_valid)).show()
//            return
//        }

        if (tv_dob2?.text?.toString().isNullOrEmpty()) {
            error(getString(R.string.enter_dob))
            return
        }

        var date = ""
        try {
            val sp = dobUpdate.split("/")
            val day = sp.get(0).toIntOrNull() ?: 0
            val month = sp.get(1).toIntOrNull() ?: 0
            val year = sp.get(2).toIntOrNull() ?: 0
            date = "$year-$month-$day"
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        log("date :::::::  $date")

        if (date.isNullOrEmpty()) {
            error(getString(R.string.enter_dob))
            return
        }
        val member = Prefs.get(context).member ?: return
        val data = UpdateMemberDetails.Data(
            et_city?.text?.toString(),
            tv_country2?.text?.toString(),
            date,
            et_fname?.text?.toString(),
            et_lname?.text?.toString(),
            member.id
        )
        getDialog()?.show()
        API.request.getApi().updateMemberDetails(UpdateMemberDetails(data, member.accessToken))
            .enqueue(object : Callback<ResponseData> {
                override fun onFailure(call: Call<ResponseData>, t: Throwable) {
                    getDialog()?.dismiss()
                }

                override fun onResponse(
                    call: Call<ResponseData>,
                    response: Response<ResponseData>
                ) {
                    getDialog()?.dismiss()
                    val body = response?.body()
                    if (body != null && body.isSuccess()) {
                        Prefs.get(context).set("profile_update", "true")
                        Toasty.info(requireContext(), getString(R.string.profile_updated)).show()
                        navigate(Navigator.CLEAR_HOME, null)
                    }
                }

            })
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_profile_done, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_done -> {
                saveDateApi()
            }
            R.id.action_edit -> {
                //profileEditable(true)
            }
        }
        return super.onOptionsItemSelected(item)
    }


}