/*
 *  Created by Sumeet Kumar on 2/20/20 4:21 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 2/20/20 4:21 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.rxt

/*
 *  Created by Sumeet Kumar on 1/16/20 12:10 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/16/20 12:10 PM
 *  Mibo Hexa - app
 */


import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import life.mibo.android.R
import life.mibo.android.core.Prefs
import life.mibo.android.ui.base.ItemClickListener


class RXTFilterDialog(c: Context, var options: String, val listener: ItemClickListener<Any?>?) :
    BottomSheetDialog(c) {

    var textView: TextView? = null
    var isEms = false
    var isCircuits = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_rxt_filters_dialog)
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        textView = findViewById(R.id.tv_title)
        val image: ImageView? = findViewById(R.id.island_image)
        val islandName: TextView? = findViewById(R.id.tv_island_name)
        val islandInfo: TextView? = findViewById(R.id.tv_island_tile)
        val cancel: View? = findViewById(R.id.iv_cancel)
        val apply: View? = findViewById(R.id.iv_yes)
        val radio: RadioGroup? = findViewById(R.id.radio_group)
        val checkBox: SwitchCompat? = findViewById(R.id.switch_ems_)
        val deff = options.split("-")

        if (deff.size > 0) {
            if (deff[0] == "0")
                radio?.check(R.id.radio_workouts)
            else if (deff[0] == "1")
                radio?.check(R.id.radio_circuits)
        }
        if (deff.size > 1) {
            checkBox?.isChecked = deff[1] == "1"
        }

        radio?.setOnCheckedChangeListener { group, checkedId ->
            isCircuits = checkedId == R.id.radio_circuits
        }

        checkBox?.setOnCheckedChangeListener { buttonView, isChecked ->
            isEms = isChecked
        }

        cancel?.setOnClickListener {
            listener?.onItemClicked(null, 1)
            dismiss()
        }

        apply?.setOnClickListener {
            if (isCircuits)
                listener?.onItemClicked(null, 20)
            else listener?.onItemClicked(null, 10)
            dismiss()
        }

        image?.setOnClickListener {
            listener?.onItemClicked(null, 30)
            dismiss()
        }

        val prefs = Prefs.get(context)
        Glide.with(image!!).load(prefs.get(Prefs.ISLAND_IMAGE)).error(R.drawable.ic_broken_image)
            .fallback(R.drawable.ic_broken_image).into(image)
        islandName?.text = prefs.get(Prefs.ISLAND_NAME)
        islandInfo?.text = prefs.get(Prefs.ISLAND_HEIGHT) + " x " + prefs.get(Prefs.ISLAND_WIDTH)
    }

}