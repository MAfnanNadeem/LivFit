/*
 *  Created by Sumeet Kumar on 2/13/20 2:03 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 2/3/20 3:43 PM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.room

import androidx.room.*
//import life.mibo.hexa.models.rxl.RxlExercises.Program
import life.mibo.hexa.models.rxl.RxlProgram

@Dao
interface RxlProgramDao {

    @Query("SELECT * from rxl_programs")
    fun getPrograms(): List<RxlProgram>

    @Query("SELECT * from rxl_programs")
    fun getAll(): List<RxlProgram?>?

    @Query("SELECT * from rxl_programs where memberId == :memberId")
    fun getAll(memberId: String): List<RxlProgram?>?

    @Query("SELECT * from rxl_programs where memberId == :memberId")
    fun getMyPlay(memberId: String): List<RxlProgram?>?

    @Query("SELECT * from rxl_programs WHERE NULLIF(memberId, '') IS NULL")
    fun getQuickPlay(): List<RxlProgram?>?

    @Query("DELETE FROM rxl_programs")
    fun deleteAll()

    @Delete
    fun delete(programs: List<RxlProgram>)

    @Delete
    fun delete(program: RxlProgram)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(program: RxlProgram): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(program: RxlProgram): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(programs: List<RxlProgram?>): LongArray
}