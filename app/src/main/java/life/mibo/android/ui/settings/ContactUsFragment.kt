/*
 *  Created by Sumeet Kumar on 5/12/20 12:26 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 5/12/20 12:26 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.settings

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_contact_us.*
import life.mibo.android.R
import life.mibo.android.core.API
import life.mibo.android.core.Prefs
import life.mibo.android.libs.image.ImagePicker
import life.mibo.android.models.member.SaveMemberAvatar
import life.mibo.android.ui.base.BaseFragment
import life.mibo.android.ui.base.PermissionHelper
import life.mibo.android.ui.main.Navigator
import life.mibo.android.utils.Toasty
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class ContactUsFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_contact_us, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setSpinners()

        img_upload?.setOnClickListener {
            openPicker()
        }
        button_send?.setOnClickListener {
            Toasty.snackbar(button_send, getString(R.string.thankyou_feedback))
        }
    }

    private fun setSpinners() {
        val list = ArrayList<String>()
        list.add(getString(R.string.contact_option_1))
        list.add(getString(R.string.contact_option_2))
        list.add(getString(R.string.contact_option_3))
        list.add(getString(R.string.contact_option_4))
        list.add(getString(R.string.contact_option_5))
        list.add(getString(R.string.contact_option_6))
        list.add(getString(R.string.contact_option_7))
        list.add(getString(R.string.contact_option_8))


        val adapters = ArrayAdapter<String>(requireContext(), R.layout.list_item_spinner, list)

        spinner_subject?.adapter = adapters

        spinner_subject?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {


            }
        }

    }

    private fun openPicker() {
        val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        PermissionHelper.requestPermission(this@ContactUsFragment, permissions) {
            //openPicker2()
            openPickerOptions()
        }
    }

    private fun openPickerOptions() {
        ImagePicker.with(this@ContactUsFragment).crop().compress(1024).galleryOnly().start()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        log("onActivityResult")
        if (requestCode == ImagePicker.REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val file: File? = ImagePicker.getFile(data)
            Glide.with(this).load(file).into(img_upload)
            // uploadPicApi(file)
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toasty.info(requireContext(), ImagePicker.getError(data)).show()
        } else {
            log("onActivityResult  Task Cancelled")
            // Toasty.info(requireContext(), "Task Cancelled").show()
        }
    }


    private fun uploadPicApi(file: File?) {
        log("uploadPicApi $file")
        //log("bitmapToBase64 uploadPicApi ${base64.length} : ${base64?.length?.div(1024)}")
        if (file == null) {
            Toasty.snackbar(view, getString(R.string.invalid_image_file))
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
        // map.put("MemberID", toRequestBody(member.id()))
        if (member.isMember()) {
            map.put("MemberID", toRequestBody(member.id()))
            map.put("TrainerID", toRequestBody(""))
        } else {
            map.put("TrainerID", toRequestBody(member.id()))
            map.put("MemberID", toRequestBody(""))
        }

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
                        //val memberr = Prefs.get(context).member
                        //memberr?.profileImg = data?.data?.profile
                        //Prefs.get(context).member = memberr
                        //navigate(Navigator.PIC_UPLOADED, data?.data?.profile)

                        log("onResponse data $data")
                        data?.data?.message?.let {
                            log("onResponse message $it")
                            Toasty.snackbar(view, it).show()
                        }
                    } else {
                        try {
                            data?.errors?.get(0)?.let {
                                log("onResponse message $it")
                                Toasty.snackbar(view, it?.message).show()
                            }
                        } catch (e: java.lang.Exception) {

                        }
                    }

                    log("onResponse end....")
                }

            })
    }

    fun toRequestBody(value: String?): RequestBody? {
        return value?.toRequestBody("text/plain".toMediaType())
    }
}