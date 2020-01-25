/*
 *  Created by Sumeet Kumar on 1/22/20 11:28 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/22/20 11:28 AM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Completable
import io.reactivex.Flowable

@Dao
interface MemberDao {

    @Query("SELECT * from mibo_member ORDER BY id ASC")
    fun getMembers(): List<Member>

//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertMember(member: Member)

    @Query("DELETE FROM mibo_member")
    fun deleteAll()

    @Query("SELECT * FROM mibo_member WHERE id = :id")
    fun getMemberById(id: String): Flowable<Member>

    @Query("SELECT * FROM mibo_member")
    fun getMember(): Flowable<List<Member>>

    @Query("SELECT accessToken FROM mibo_member")
    fun getToken(): Flowable<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(member: Member): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(member: Member): Long
}