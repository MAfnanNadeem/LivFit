/*
 *  Created by Sumeet Kumar on 1/28/20 8:52 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/28/20 8:52 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.profile

//import com.dichotome.profilebar.ui.tabPager.TabFragment
import android.Manifest
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_profile.*
import life.mibo.android.R
import life.mibo.android.core.API
import life.mibo.android.core.Prefs
import life.mibo.android.models.base.ResponseData
import life.mibo.android.models.member.Avatar
import life.mibo.android.ui.base.BaseFragment
import life.mibo.android.ui.base.PermissionHelper
import life.mibo.android.utils.Toasty
import life.mibo.android.utils.Utils
import life.mibo.hardware.core.Logger
import life.mibo.imagepicker.RxGalleryFinal
import life.mibo.imagepicker.RxGalleryFinalApi
import life.mibo.imagepicker.imageloader.ImageLoaderType
import life.mibo.imagepicker.rxbus.RxBusResultDisposable
import life.mibo.imagepicker.rxbus.event.ImageRadioResultEvent
import life.mibo.imagepicker.ui.base.IRadioImageCheckedListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class ProfileFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_profile, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        val pagerFragments = arrayListOf(
//            Tab1Fragment.newInstance("Tab 1"),
//            Tab2Fragment.newInstance("Tab 2")
//        )

//        val member = Prefs.get(context).member
//        //drawer_user_email?.text = member.imageThumbnail
//            "${member?.firstName} ${member?.lastName}"
//        profileBar.apply {
//            photo = ContextCompat.getDrawable(context, R.drawable.rxl_score_user)
//            subtitle = "Joined on "+SimpleDateFormat("dd MMMM, yyyy").format(Date())
//            title =  "${member?.firstName} ${member?.lastName}"
//            wallpaper = ContextCompat.getDrawable(context, R.drawable.ic_reflex_user)
//            optionWindow.changeWallpaperButton.visibility = View.GONE
//            optionWindow.changeUsernameButton.visibility = View.GONE
//            optionWindow.logOutButton.visibility = View.GONE
//            optionWindow.changePhotoButton.visibility = View.GONE
//            tabsEnabled = false
//
//
//        }
        //profilePager.adapter = TabPagerAdapter(childFragmentManager)
        //profilePager.fragments = pagerFragments

        //profileBar.setupWithViewPager(profilePager)

        this.setProfile()

    }

    private fun setProfile() {
        val member = Prefs.get(context).member ?: return
        tv_name?.text = member.firstName + " " + member.lastName
        tv_email?.text = Prefs.get(context).get("user_email")
        tv_dob?.text = member.dob
        tv_city?.text = member.city
        tv_number?.text = member.contact
        tv_country?.text = member.country
        setUserImage()

        tv_change_pwd?.setOnClickListener {
            ChangePasswordDialog(requireContext()).show()
        }
    }

    private fun setUserImage() {

        //userImage?.setImageResource(R.drawable.ic_user_test)
        loadImage(userImage, R.drawable.ic_user_test)
        constraintLayout1?.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.colorAccent
            )
        )
        //setGradient(constraintLayout1, userImage.drawable)
        userImage?.setOnClickListener {
            openPicker()
        }
    }

    private fun openPickerOld() {
        log("openPicker called")
        RxGalleryFinal.with(requireActivity()).image()
            .single()
            .crop()
            .imageLoader(ImageLoaderType.GLIDE)
            .subscribe(object : RxBusResultDisposable<ImageRadioResultEvent>() {
                override fun onEvent(t: ImageRadioResultEvent?) {
                    Logger.e("openPicker RxGalleryFinalApi onEvent $t")
                }
            })
            .openGallery();
    }

    private val permissions = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA
    )

    private fun openPicker() {
        PermissionHelper.requestPermission(this@ProfileFragment, permissions) {
            openPicker2()
        }
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
                    file?.let {
                        Glide.with(this@ProfileFragment).load(it).override(300)
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
                                    saveAndUpload(resource)

                                    return true
                                }

                            }).into(userImage)
                        Logger.e("openPicker RxGalleryFinalApi glide loading")
                    }
                }
            })
    }

    private fun saveAndUpload(drawable: Drawable?) {
        drawable?.let {
            Single.fromCallable {
                val bmp = drawableToBitmap(it)
                //log("Base64 " + bmp?.byteCount)
                // log("Base64 " + Utils.bitmapToBase64(bmp))
                uploadPicApi(Utils.bitmapToBase64(bmp))
                activity?.runOnUiThread {
                    userImage?.setImageDrawable(it)
                }
                "return"
            }.doOnError {

            }.subscribeOn(Schedulers.io()).subscribe()
        }
    }

    private fun uploadPicApi(base64: String?) {
        if (base64 == null || base64.length < 100)
            return
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

    private fun loadImage(iv: ImageView, defaultImage: Int) {
        Maybe.fromCallable {
            log("loadImage fromCallable")
            var bitmap: Bitmap? = null
            val img = Prefs.get(this.context).member?.imageThumbnail
            bitmap = if (!img.isNullOrEmpty())
                Utils.base64ToBitmap(img)
            else
                BitmapFactory.decodeResource(resources, defaultImage)
            // else
            //   bitmap = Utils.base64ToBitmap(Utils.testUserImage())
            bitmap
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).doOnSuccess {
            log("loadImage doOnSuccess $it")
            if (it != null)
                iv.setImageBitmap(it)
            else
                iv.setImageResource(defaultImage)
        }.doOnError {

        }.subscribe()
    }

//    class Tab1Fragment() : TabFragment() {
//        companion object {
//            fun newInstance(tabTitle: String) = Tab1Fragment().apply {
//                title = tabTitle
//            }
//        }
//    }
//
//    class Tab2Fragment() : TabFragment() {
//        companion object {
//            fun newInstance(tabTitle: String) = Tab2Fragment().apply {
//                title = tabTitle
//            }
//        }
//    }
//
//    override fun onBackPressed(): Boolean {
//       // if(profileBar.onBackPressed())
//        return super.onBackPressed()
//    }

    private fun setGradient(view: View?, drawable: Drawable?) {
        Logger.e("ReflexHolder setGradient $drawable")
        if (drawable == null || view == null)
            return

        Single.fromCallable {
            Logger.e("ReflexHolder setGradient fromCallable")
            var color = Color.GRAY
            try {
                val gd = GradientDrawable(
                    GradientDrawable.Orientation.TOP_BOTTOM,
                    intArrayOf(-0x9e9d9f, -0xececed)
                )
                gd.cornerRadius = 0f
                Logger.e("ReflexHolder setGradient generating......")
                Palette.from(drawableToBitmap(drawable)!!).generate {
                    Logger.e("ReflexHolder setGradient generated palette")
                    it?.let { palette ->
                        color = palette.getDominantColor(
                            ContextCompat.getColor(view?.context!!, R.color.grey)
                        )
                        Logger.e("ReflexHolder setGradient applied dominantColor $color")
                        view.setBackgroundColor(color)
                        //return@let color
                    }
                }
                //Utils.getColor(view?.drawable)
            } catch (e: Exception) {
                Logger.e("ReflexHolder setGradient Exception $e")
                e.printStackTrace()
            }

            return@fromCallable color

        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).doOnSuccess {
            Logger.e("ReflexHolder setGradient doOnSuccess $it")
            view.setBackgroundColor(it)
        }.subscribe()

    }

    private fun drawableToBitmap(drawable: Drawable): Bitmap? {
        var bitmap: Bitmap? = null
        if (drawable is BitmapDrawable) {
            if (drawable.bitmap != null) {
                return drawable.bitmap
            }
        }
        bitmap = if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
            Bitmap.createBitmap(
                1,
                1,
                Bitmap.Config.ARGB_8888
            ) // Single color bitmap will be created of 1x1 pixel
        } else {
            Bitmap.createBitmap(
                drawable.intrinsicWidth,
                drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
        }
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }
}