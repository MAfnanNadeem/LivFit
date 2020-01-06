package life.mibo.views.dialog


import android.graphics.drawable.Drawable

/**
 * Created by Sumeet Kumar @MI.BO
 */
data class ActionItem(
    val id: Int,
    val title: CharSequence,
    val image: Drawable? = null
)