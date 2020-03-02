/*
 *  Created by Sumeet Kumar on 1/23/20 12:33 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/23/20 12:33 PM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Flowable

@Dao
interface UserDao {

    @Query("DELETE FROM member_details")
    fun deleteAll()

    @Query("SELECT * FROM member_details WHERE id = :id")
    fun getMemberById(id: String): Flowable<UserDetails>

    @Query("SELECT weight FROM member_details")
    fun getWeight(): Flowable<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(member: UserDetails): Long
}