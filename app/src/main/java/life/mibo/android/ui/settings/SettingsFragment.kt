/*
 *  Created by Sumeet Kumar on 5/12/20 12:26 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 5/12/20 12:26 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import com.rilixtech.widget.countrycodepicker.Country
import com.rilixtech.widget.countrycodepicker.CountryUtils
import kotlinx.android.synthetic.main.fragment_settings.*
import life.mibo.android.R
import life.mibo.android.core.Prefs
import life.mibo.android.ui.base.BaseFragment
import life.mibo.android.ui.base.WebViewFragment
import life.mibo.android.ui.main.Navigator

class SettingsFragment : BaseFragment() {

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
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    var type: Int = 0


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        type = arguments?.getInt("type_", 0) ?: 0

        if (type == 1) {
            view_units?.visibility = View.VISIBLE
            view_notify?.visibility = View.GONE
            view_policies?.visibility = View.GONE

            tv_measure?.setOnClickListener {
                showHeightDialog()
            }
            tv_weight?.setOnClickListener {
                showWeightDialog()
            }

            weight = getPref()?.weightPrefs ?: 0
            height = getPref()?.heightPrefs ?: 0

            if (height > 1)
                height = 0
            if (weight > 1)
                weight = 0
            val heightText =
                if (height == 0) getString(R.string.centimeters) else getString(R.string.feet_inches)
            val weightText =
                if (weight == 0) getString(R.string.kilograms) else getString(R.string.pounds)

            tv_measure_unit?.text = heightText
            tv_weight_unit?.text = weightText
        } else if (type == 2) {
            view_units?.visibility = View.GONE
            view_policies?.visibility = View.GONE
            view_notify?.visibility = View.VISIBLE

            view_notify?.setOnClickListener {
                switch_notify?.isChecked = !switch_notify.isChecked

            }

            switch_notify?.isChecked = true
            switch_notify?.isClickable = false
        } else if (type == 3) {
            view_units?.visibility = View.GONE
            view_notify?.visibility = View.GONE
            view_policies?.visibility = View.VISIBLE

            tv_privacy?.setOnClickListener {
                navigate(
                    Navigator.WEBVIEW,
                    WebViewFragment.bundle("https://docs.google.com/viewerng/viewer?embedded=true&url=https://mibo.life/wp-content/uploads/2020/06/Mibo-livfit-privacy-policy.pdf", getString(R.string.privacy_policy))
                )
            }

            tv_terms?.setOnClickListener {
                navigate(
                    Navigator.WEBVIEW,
                    WebViewFragment.bundle("https://docs.google.com/viewerng/viewer?embedded=true&url=https://mibo.life/wp-content/uploads/2020/06/Mibo-livfit-privacy-policy.pdf", getString(R.string.terms_of_agreement))
                )
            }

            tv_legal?.setOnClickListener {
                navigate(
                    Navigator.WEBVIEW,
                    WebViewFragment.bundle("https://docs.google.com/viewerng/viewer?embedded=true&url=https://mibo.life/wp-content/uploads/2020/06/Mibo-livfit-privacy-policy.pdf")
                )
            }

            tv_faq?.setOnClickListener {
                navigate(
                    Navigator.WEBVIEW,
                    WebViewFragment.bundle(
                        "http://test.mibo.life/faq-mobile-application/",
                        getString(R.string.faq)
                    )
                )
            }

        } else if (type == 4) {
            view_units?.visibility = View.GONE
            view_notify?.visibility = View.GONE
            view_policies?.visibility = View.GONE
            view_lang?.visibility = View.VISIBLE
            val prefs = Prefs.get(requireContext()).member
            tv_country_?.text = "${prefs?.countryCode?.toUpperCase()}"

            try {
                val id = CountryUtils.getFlagDrawableResId(
                    Country(
                        prefs?.countryCode?.toLowerCase(),
                        "",
                        ""
                    )
                )
                tv_country_flag?.setImageResource(id)
            } catch (e: Exception) {

            }

            try {
                tv_lang_?.setText(R.string.lang_eng)
                tv_lang_flag?.setImageResource(R.drawable.flag_united_kingdom)

            } catch (e: Exception) {

            }


            tv_country?.setOnClickListener {

            }

            tv_lang?.setOnClickListener {
                showLanguageDialog()
            }
        }


    }

    private fun showLanguageDialog() {
        val options = arrayOf(getString(R.string.lang_eng))
        val builder =
            AlertDialog.Builder(requireContext())
        builder.setTitle("")
        builder.setSingleChoiceItems(options, 0) { dialog, which ->

        }.setPositiveButton(R.string.ok_button) { dialog, which ->

        }
        builder.show()
    }

    var prefs: Prefs? = null

    fun getPref(): Prefs? {
        if (prefs == null)
            prefs = Prefs.getTemp(context)
        return prefs
    }


    var weight = -1
    var height = -1

    private fun showWeightDialog() {
        val options = arrayOf(getString(R.string.kilograms), getString(R.string.pounds))
        if (weight > 1)
            weight = 0
        val builder =
            AlertDialog.Builder(requireContext())
        builder.setTitle("")
        builder.setSingleChoiceItems(options, weight) { dialog, which ->
            weight = which

        }.setPositiveButton(R.string.ok_button) { dialog, which ->
            // save
            val text =
                if (weight == 0) getString(R.string.kilograms) else getString(R.string.pounds)
            tv_weight_unit?.text = text
            getPref()?.weightPrefs = weight
        }
        builder.show()
    }

    private fun showHeightDialog() {
        var options = arrayOf(getString(R.string.centimeters), getString(R.string.feet_inches))
        if (height > 1)
            height = 0
        val builder =
            AlertDialog.Builder(requireContext())
        builder.setTitle("")
        builder.setSingleChoiceItems(options, height) { dialog, which ->
            height = which
        }.setPositiveButton(R.string.ok_button) { dialog, which ->
            // save
            val text =
                if (height == 0) getString(R.string.centimeters) else getString(R.string.feet_inches)
            tv_measure_unit?.text = text
            getPref()?.heightPrefs = height
        }
        builder.show()
    }

}