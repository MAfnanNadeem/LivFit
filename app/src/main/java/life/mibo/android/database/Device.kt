/*
 *  Created by Sumeet Kumar on 1/27/20 12:11 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/25/20 4:20 PM
 *  Mibo Hexa - app
 */

/*
 * $Created by $Sumeet $Kumar 2020.
 */

package life.mibo.android.database


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import life.mibo.android.models.base.BaseModel
import java.net.InetAddress

@Entity(tableName = "devices")
data class Device(@PrimaryKey var uid: String, var name: String, var ip: String) : BaseModel {

    //@PrimaryKey(autoGenerate = false)
    var id: Int = 0

    @TypeConverters(Converters::class)
    var address: InetAddress? = null

    var memberId: Int = 0
    var memberName: Int = 0

    @ColumnInfo(name = "device_type")
    var type: Int = 0

    @ColumnInfo(name = "device_active")
    var isActive: Boolean = false
}