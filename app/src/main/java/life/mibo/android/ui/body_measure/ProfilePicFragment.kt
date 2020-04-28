/*
 *  Created by Sumeet Kumar on 4/14/20 2:29 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 4/14/20 11:34 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.body_measure

import android.Manifest
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
import life.mibo.android.models.base.ResponseData
import life.mibo.android.models.member.Avatar
import life.mibo.android.ui.base.PermissionHelper
import life.mibo.android.ui.body_measure.adapter.BodyBaseFragment
import life.mibo.android.ui.body_measure.adapter.Calculate
import life.mibo.android.utils.Toasty
import life.mibo.android.utils.Utils
import life.mibo.hardware.core.Logger
import life.mibo.imagepicker.RxGalleryFinalApi
import life.mibo.imagepicker.rxbus.RxBusResultDisposable
import life.mibo.imagepicker.rxbus.event.ImageRadioResultEvent
import life.mibo.imagepicker.ui.base.IRadioImageCheckedListener
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
        Calculate.clear()
        //Calculate.addValue("user_gender", Prefs.get(context).member?.gender)
        val isMale = Prefs.get(context).member?.gender?.equals("male", true) ?: true
        Calculate.getMeasureData().gender(isMale)

    }

    override fun onResume() {
        super.onResume()
        //testMode()
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

            profile_pic?.setOnClickListener {
                uploadPic()
            }

        } else if (type == 2) {
            //tv_continue?.visibility = View.INVISIBLE
            profile_pic?.visibility = View.INVISIBLE
            profile_text?.visibility = View.INVISIBLE
            profile_pic_uploaded?.visibility = View.VISIBLE
            user_name?.visibility = View.VISIBLE
            user_welcome?.visibility = View.VISIBLE
            profile_bg?.visibility = View.VISIBLE
            user_name?.text =
                Prefs.get(context).member?.firstName + " " + Prefs.get(context).member?.lastName
            profile_pic_uploaded?.setOnClickListener {
                uploadPic()
            }
        }

    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        log("setUserVisibleHint $isVisibleToUser")
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
           // updateNextButton(false)
            updateSkipButton(false)
        }
    }

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
            uploadPic2()
        }
    }

    private fun uploadPic2() {
//        val frg = parentFragment
//        log("parentFragment1 $frg")
//        log("parentFragment2 ${parentFragmentManager?.fragments}")
//        log("parentFragment2 ${parentFragmentManager?.fragments[0]}")
//        log("parentFragment3 ${childFragmentManager?.fragments}")
//        log("parentFragment4 $targetFragment")
//        if (frg is MeasurementFragment) {
//            frg.updateNext(false)
//        }
        // viewModel!!.updateNext(false)
        log("openPicker called")
        RxGalleryFinalApi.setImgSaveRxDir(context?.externalCacheDir);
        RxGalleryFinalApi.setImgSaveRxCropDir(context?.externalCacheDir);

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
                    profile_pic_uploaded?.load(file) {
                        Logger.e("openPicker coil response $this")
                    }

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
                                        saveAndUpload(resource)
                                        updateNextButton(true)
                                    }

                                    return true
                                }

                            }).into(profile_pic)
                        Logger.e("openPicker RxGalleryFinalApi glide loading")
                    }
                }
            })
    }

    private fun saveAndUpload(drawable: Drawable?) {
        drawable?.let {
            Single.fromCallable {
                //log("Base64 " + bmp?.byteCount)
                // log("Base64 " + Utils.bitmapToBase64(bmp))
                uploadPicApi(Utils.bitmapToBase64(drawable))
//                activity?.runOnUiThread {
//                    profile_pic_uploaded?.setImageDrawable(it)
//                }
                "return"
            }.doOnError {

            }.subscribeOn(Schedulers.io()).subscribe()
        }
    }

    private fun uploadPicApi(base64: String) {
        log("bitmapToBase64 uploadPicApi ${base64.length} : ${base64?.length?.div(1024)}")
        val member = Prefs.get(context).member ?: return
        API.request.getApi()
            .memberAvatar(Avatar(Avatar.Data(base64, member.id()), member.accessToken))
            .enqueue(object : Callback<ResponseData> {
                override fun onFailure(call: Call<ResponseData>, t: Throwable) {
                    Toasty.error(requireContext(), R.string.unable_to_connect).show()
                }

                override fun onResponse(
                    call: Call<ResponseData>,
                    response: Response<ResponseData>
                ) {
                    val data = response?.body()?.data
                    log("onResponse data $data")
                    data?.message?.let {
                        log("onResponse message $it")
                        Toasty.snackbar(view, it).show()
                    }
                    log("onResponse end....")
                }

            })
    }

}