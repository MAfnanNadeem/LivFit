/*
 *  Created by Sumeet Kumar on 4/14/20 2:29 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 4/14/20 11:34 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.body_measure

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import coil.api.load
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_body_profile.*
import life.mibo.android.R
import life.mibo.android.core.API
import life.mibo.android.core.Prefs
import life.mibo.android.libs.image.ImagePicker
import life.mibo.android.models.member.SaveMemberAvatar
import life.mibo.android.ui.base.PermissionHelper
import life.mibo.android.ui.body_measure.adapter.BodyBaseFragment
import life.mibo.android.utils.Toasty
import life.mibo.android.utils.Utils
import life.mibo.hardware.core.Logger
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File


class ProfilePicFragment : BodyBaseFragment() {

    companion object {
        fun create(type: Int): ProfilePicFragment {
            val frg = ProfilePicFragment()
            val arg = Bundle()
            arg.putInt("profile_type", type)
            frg.arguments = arg
            return frg
        }
    }

    private var viewModel: MeasureViewModel? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, bundle: Bundle?):
            View? {
        return inflater.inflate(R.layout.fragment_body_profile, container, false)
    }

    var selected = -1
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // viewModel = ViewModelProvider(this@ProfilePicFragment).get(MeasureViewModel::class.java)
        selected = arguments?.getInt("profile_type", 1) ?: 1
        //updateNextButton(false)
        setup(selected)
//        tv_continue?.setOnClickListener {
//            skipClicked()
//        }

//        rulerDemo?.setValuePickerListener(object : RulerValuePickerListener {
//            override fun onValueChange(selectedValue: Int) {
//                log("onValueChange $selectedValue")
//                textDemo?.text = "$selectedValue"
//            }
//
//            override fun onIntermediateValueChange(selectedValue: Int) {
//                log("onIntermediateValueChange $selectedValue")
//
//            }
//        })

        val prefs = Prefs.getEncrypted(context)
        prefs?.initCipher()
        prefs.set("profile_skipped", "true", true)
    }


    private fun setup(type: Int) {
        if (type == 1) {
            //?.visibility = View.VISIBLE
            profile_pic?.visibility = View.VISIBLE
            profile_text?.visibility = View.VISIBLE
            profile_pic_uploaded?.visibility = View.INVISIBLE
            user_name?.visibility = View.INVISIBLE
            user_welcome?.visibility = View.INVISIBLE
            profile_bg?.visibility = View.INVISIBLE

            btn_upload?.setOnClickListener {
                if (btn_upload?.text?.toString()!!.toLowerCase().contains("upload"))
                    uploadPic()
                if (btn_upload?.text?.toString()!!.toLowerCase().contains("finish"))
                    navigate(life.mibo.android.ui.main.Navigator.CLEAR_HOME, null)
            }

            btn_skip?.setOnClickListener {
                Prefs.get(context).set("profile_skipped", true)
                navigate(life.mibo.android.ui.main.Navigator.CLEAR_HOME, null)
            }
            // btn_upload

        } else if (type == 2) {
            //tv_continue?.visibility = View.INVISIBLE
            profile_pic?.visibility = View.INVISIBLE
            profile_text?.visibility = View.INVISIBLE
            profile_pic_uploaded?.visibility = View.VISIBLE
            user_name?.visibility = View.VISIBLE
            user_welcome?.visibility = View.VISIBLE
            profile_bg?.visibility = View.VISIBLE
            btn_skip?.setText(R.string.done)
            btn_skip?.setBackgroundResource(R.drawable.login_button_accent)
            user_name?.text =
                Prefs.get(context).member?.firstName + " " + Prefs.get(context).member?.lastName
            profile_pic_uploaded?.setOnClickListener {
                uploadPic()
            }
            btn_skip?.setOnClickListener {
                Prefs.get(context).set("profile_skipped", true)
                navigate(life.mibo.android.ui.main.Navigator.CLEAR_HOME, null)
            }
        }

    }

    override fun onResume() {
        super.onResume()
        //testMode()
        updateSkipButton(false)
    }
//    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
//        log("setUserVisibleHint $isVisibleToUser")
//        super.setUserVisibleHint(isVisibleToUser)
//        if (isVisibleToUser) {
//           // updateNextButton(false)
//            updateSkipButton(false)
//        }
//    }

    override fun onAttachFragment(childFragment: Fragment) {
        super.onAttachFragment(childFragment)
        log("onAttachFragment $childFragment")
    }

    private fun skipClicked() {

    }

    private val permissions = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA
    )

    private fun uploadPic() {
        PermissionHelper.requestPermission(this@ProfilePicFragment, permissions) {
            ImagePicker.with(this@ProfilePicFragment).crop().compress(1024).start()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        log("onActivityResult")
        if (requestCode == ImagePicker.REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            log("onActivityResult data?.data $data")

            // val fileUri = data?.data
            //log("onActivityResult fileUri $fileUri")
            //userImage.setImageURI(fileUri)

            val file: File? = ImagePicker.getFile(data)
            // Glide.with(userImage).load(file).error(R.drawable.ic_user_female)
            //   .fallback(R.drawable.ic_user_male).into(userImage)
            uploadPicApi(file)
//            val file: File? = ImagePicker.getFile(data)
//            log("onActivityResult file $file")
//            val exist = file?.exists()
//            log("onActivityResult exist $exist")
//
//            val filePath: String? = ImagePicker.getFilePath(data)
//            log("onActivityResult filePath $filePath ")
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toasty.info(requireContext(), ImagePicker.getError(data)).show()
        } else {
            log("onActivityResult  Task Cancelled")
            // Toasty.info(requireContext(), "Task Cancelled").show()
        }
    }


    private fun uploadPicApi(file: File?) {
        log("uploadPicApi $file")
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
        val filePart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "Avatar", file.name, RequestBody.create("image/*".toMediaType(), file)
        )
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
            Glide.with(profile_pic).load(it.absolutePath)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        Logger.e("openPicker onLoadFailed glide ")
                        e?.logRootCauses("openPicker")
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
                        resource?.let { d ->
                            profile_pic_uploaded.setImageDrawable(d)
                            setup(2)
                            updateNextButton(true)
                        }

                        return true
                    }

                }).into(profile_pic)
            Logger.e("openPicker RxGalleryFinalApi glide loading")
        }
    }

}