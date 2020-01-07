/*
 * $Created by $Sumeet $Kumar 2020.
 */

package life.mibo.hexa.ui.home

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.get
import life.mibo.views.HexagonMaskView

data class HomeItem(
    val id: Int,
    var title: String = "",
    var color: Int = 0,
    var type: Type = Type.UNKNOWN
) {

    constructor(title: String, color: IntArray, type: Type, icon: Int = 0) : this(
        0,
        title,
        0,
        type
    ) {
        colorArray = color
        iconRes = icon
    }

    private var colorArray: IntArray? = null
    private var iconRes: Int = 0

    enum class Type {
        HEART, WEIGHT, CALORIES, PROFILE, CALENDAR, PROGRAMS, EXERCISE, RXL, BOOSTER, ReFlex, TILES, FLOOR, UNKNOWN
    }

    fun bind(view: ViewGroup) {
        for (i in 0..view.childCount) {
            if (view[i] is HexagonMaskView) {

            }
            if (view[i] is TextView) {

            }
        }
    }

    fun bind(image: HexagonMaskView, icon: ImageView, text: TextView) {
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

    }


}