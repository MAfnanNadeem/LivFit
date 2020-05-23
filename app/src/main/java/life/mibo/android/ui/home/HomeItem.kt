/*
 * $Created by $Sumeet $Kumar 2020.
 */

package life.mibo.android.ui.home

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.get
import life.mibo.views.DashboardItem
import life.mibo.views.hexa.HexagonMaskView

data class HomeItem(
    val id: Int,
    var title: String = "",
    var color: Int = 0,
    var type: Type = Type.UNKNOWN
) {

    constructor(title: String, color: IntArray, type: Type, icon: Int = 0, image: Int = 0) : this(
        0,
        title,
        0,
        type
    ) {
        colorArray = color
        iconRes = icon
        imageRes = image
    }

    constructor(title: String, header: String = "",  type: Type, icon: Int = 0, image: Int = 0) : this(
        0,
        title,
        0,
        type
    ) {
        iconRes = icon
        imageRes = image
        headerText = header
    }

    constructor(type: Type, bundle: Bundle? = null) : this(0, "", 0, type) {
        this.bundle = bundle
    }

    private var colorArray: IntArray? = null
    var iconRes: Int = 0
    var imageRes: Int = 0
    var headerText: String = ""
    var bundle: Bundle? = null
    var updateHeader = false
    var updateTitle = false

    enum class Type {
        HEART, WEIGHT, CALORIES, PROFILE, SERVICES, PROFILE_UPLOAD, CALENDAR, PROGRAMS, EXERCISE, RXL, BOOSTER_SCAN, WEATHER, RXL_SCAN, TILES, FLOOR, UNKNOWN, ADD, SCHEDULE, STEPS, RXL_TEST, MEASURE, MY_ACCOUNT, MEASURE_NEW, CENTER_BUTTON
    }

    fun bind(view: ViewGroup) {
        for (i in 0..view.childCount) {
            if (view[i] is HexagonMaskView) {

            }
            if (view[i] is TextView) {

            }
        }
    }

    fun bind(
        image: HexagonMaskView,
        icon: ImageView,
        text: TextView,
        listener: HomeObserver? = null
    ) {
        if (colorArray == null || colorArray!!.isEmpty()) {
            colorArray = intArrayOf(Color.DKGRAY, Color.GRAY, Color.LTGRAY)
        }
        image.setGradient(colorArray)

        if (iconRes == 0)
            icon.visibility = View.GONE
        else {
            icon.visibility = View.VISIBLE
            icon.setImageDrawable(ContextCompat.getDrawable(icon.context, iconRes))
        }
        text.text = title
        image.setOnClickListener {
            listener?.onItemClicked(this)
        }

    }

    fun bind(
        view: DashboardItem, listener: HomeObserver? = null
    ) {
        view.set(imageRes, iconRes, title, headerText)

        view.setOnClickListener {
            listener?.onItemClicked(this)
        }

    }


}