/*
 *  Created by Sumeet Kumar on 1/22/20 11:24 AM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 1/22/20 11:24 AM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.room

import android.annotation.SuppressLint
import android.content.Context
import androidx.room.RoomDatabase
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Callable

@androidx.room.Database(entities = [Member::class], version = 3, exportSchema = false)
abstract class Database() : RoomDatabase() {

    abstract fun memberDao(): MemberDao

    companion object {

        @Volatile
        private var INSTANCE: Database? = null

        fun getInstance(context: Context): Database =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            androidx.room.Room.databaseBuilder(
                context.applicationContext,
                Database::class.java,
                "MIBOHEXA"
            ).fallbackToDestructiveMigration().build()

        fun execute(action: Action): Disposable {
            return Observable.empty<String>().observeOn(Schedulers.newThread())
                .doOnComplete(action)
                .subscribe()
        }

        fun <T> execute(call: Callable<T>, action: Action): Disposable {
            return Observable.fromCallable(call).observeOn(Schedulers.newThread())
                .doOnComplete(action)
                .subscribe()
        }

        fun <T> execute(call: Callable<T>, action: Consumer<T>): Disposable {
            return Observable.fromCallable(call).observeOn(Schedulers.newThread()).subscribe(action)
        }

        fun <T> execute(call: Callable<T>, action: Observer<T>) {
            return Observable.fromCallable(call).observeOn(Schedulers.newThread()).subscribe(action)
        }
    }

    @SuppressLint("CheckResult")
    fun clearAll() {
        Single.just("").subscribeOn(Schedulers.newThread()).subscribe { i ->
            clearAllTables()
        }
    }

    fun insert(member: Member) {
        execute(Action {
            memberDao().add(member)
        })
    }

}

