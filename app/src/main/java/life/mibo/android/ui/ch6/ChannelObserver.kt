/*
 *  Created by Sumeet Kumar on 1/16/20 5:09 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/16/20 5:09 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.ch6

interface ChannelObserver {
    abstract fun onTimerUpdate(time: Long)
    abstract fun updatePlayButton(isPaused: Boolean)
}