/*
 *  Created by Sumeet Kumar on 2/3/20 2:00 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/28/20 8:41 AM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.database

import androidx.room.*
import life.mibo.hexa.models.program.Program

@Dao
interface ProgramDao {

    @Query("SELECT * from programs")
    fun getPrograms(): List<Program>

    @Query("SELECT * from programs")
    fun getAll(): List<Program?>?

    @Query("DELETE FROM programs")
    fun deleteAll()

    @Delete
    fun delete(devices: List<Program>)

    @Delete
    fun delete(device: Program)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(device: Program): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(device: Program): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(device: List<Program?>): LongArray
}