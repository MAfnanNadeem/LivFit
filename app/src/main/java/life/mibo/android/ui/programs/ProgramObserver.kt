/*
 *  Created by Sumeet Kumar on 1/8/20 5:40 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/8/20 9:12 AM
 *  Mibo Hexa - app
 */

package life.mibo.android.ui.programs

interface ProgramObserver {
    fun onDataReceived(list: ArrayList<Program>)
    fun onItemClicked(item: Program?)
}