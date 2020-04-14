/*
 *  Created by Sumeet Kumar on 1/23/20 12:30 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/23/20 12:30 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "member_details")
data class UserDetails(@PrimaryKey(autoGenerate = true) var id: Int = 0) {

    var weight: Int = 0
    var memberId: Int = 0
}