/*
 *  Created by Sumeet Kumar on 1/27/20 12:14 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/22/20 3:13 PM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.database

import androidx.room.*
import io.reactivex.Flowable

@Dao
interface DevicesDao {

    @Query("SELECT * from devices")
    fun getDevices(): List<Device>

    @Query("SELECT * from devices")
    fun getAll(): List<Device>

    @Query("DELETE FROM devices")
    fun deleteAll()

    @Delete
    fun delete(devices: List<Device>)

    @Delete
    fun delete(device: Device)

    @Query("SELECT * FROM devices WHERE uid = :uid")
    fun getDevice(uid: String): Flowable<Device>

    //@Query("INSERT INTO devices :device")
   // fun insert(device: Device): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(device: Device): Long
}