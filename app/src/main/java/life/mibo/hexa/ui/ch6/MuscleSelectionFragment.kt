/*
 *  Created by Sumeet Kumar on 3/24/20 10:05 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 3/24/20 10:01 AM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.ui.ch6

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_select_muscles.*
import life.mibo.hexa.R
import life.mibo.hexa.ui.base.BaseFragment
import life.mibo.hexa.ui.ch6.adapter.ChannelSelectAdapter
import life.mibo.hexa.ui.main.Navigator

class MuscleSelectionFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_select_muscles, container, false)
    }

    var muscleAdapter: ChannelSelectAdapter? = null
    var channelAdapter: ChannelSelectAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadMuscles()
        loadChannels()

        button_next?.setOnClickListener {
            navigate(Navigator.SELECT_PROGRAM, null)
        }
    }

    private fun loadMuscles() {
        val list = ArrayList<ChannelSelectAdapter.Item>()
        list.add(ChannelSelectAdapter.Item(1, R.drawable.ic_channel_abdomen))
        list.add(ChannelSelectAdapter.Item(1, R.drawable.ic_channel_abdomen))
        list.add(ChannelSelectAdapter.Item(1, R.drawable.ic_channel_abdomen))
        list.add(ChannelSelectAdapter.Item(1, R.drawable.ic_channel_abdomen))
        list.add(ChannelSelectAdapter.Item(1, R.drawable.ic_channel_abdomen))
        list.add(ChannelSelectAdapter.Item(1, R.drawable.ic_channel_abdomen))
        list.add(ChannelSelectAdapter.Item(1, R.drawable.ic_channel_abdomen))
        list.add(ChannelSelectAdapter.Item(1, R.drawable.ic_channel_abdomen))
        list.add(ChannelSelectAdapter.Item(1, R.drawable.ic_channel_abdomen))
        list.add(ChannelSelectAdapter.Item(1, R.drawable.ic_channel_abdomen))

        muscleAdapter = ChannelSelectAdapter(list, 2)
        recyclerViewLeft?.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerViewLeft?.adapter = muscleAdapter


    }

    private fun loadChannels() {
        val list = ArrayList<ChannelSelectAdapter.Item>()
        list.add(ChannelSelectAdapter.Item(1, R.drawable.ic_channel_abdomen))
        list.add(ChannelSelectAdapter.Item(1, R.drawable.ic_channel_abdomen))
        list.add(ChannelSelectAdapter.Item(1, R.drawable.ic_channel_abdomen))
        list.add(ChannelSelectAdapter.Item(1, R.drawable.ic_channel_abdomen))
        list.add(ChannelSelectAdapter.Item(1, R.drawable.ic_channel_abdomen))
        list.add(ChannelSelectAdapter.Item(1, R.drawable.ic_channel_abdomen))
        list.add(ChannelSelectAdapter.Item(1, R.drawable.ic_channel_abdomen))
        list.add(ChannelSelectAdapter.Item(1, R.drawable.ic_channel_abdomen))
        list.add(ChannelSelectAdapter.Item(1, R.drawable.ic_channel_abdomen))
        list.add(ChannelSelectAdapter.Item(1, R.drawable.ic_channel_abdomen))

        channelAdapter = ChannelSelectAdapter(list, 1)
        recyclerView?.adapter = channelAdapter
        LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerView?.adapter = muscleAdapter
    }


}