/*
 *  Created by Sumeet Kumar on 2/26/20 1:51 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 2/26/20 1:51 PM
 *  Mibo Hexa - app
 */

package life.mibo.hexa.core

import android.content.Context

class PrefsKt(var context: Context) {
//    private val PREFS_NAME = "prefs_name"
//    private val ENCRYPTED_PREFS_NAME = "encrypted_$PREFS_NAME"
//
//    private val sharedPrefs by lazy {
//        context.getSharedPreferences(
//            PREFS_NAME, Context.MODE_PRIVATE)
//    }
//
//    //    implementation "androidx.security:security-crypto:1.0.0-alpha02"
//    private val encryptedSharedPrefs by lazy {
//        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
//        EncryptedSharedPreferences.create(
//            ENCRYPTED_PREFS_NAME,
//            masterKeyAlias,
//            applicationContext,
//            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
//            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
//        )
//    }
//
//    fun put(key: String, value: String, encrypted: Boolean = false) {
//        val prefs = if (encrypted) sharedPrefs else encryptedSharedPrefs
//        with(prefs.edit()) {
//            putString(key, value)
//            commit()
//        }
//    }
//
//    fun get(key: String, default: String? = null, encrypted: Boolean = false): String? {
//        val prefs = if (encrypted) sharedPrefs else encryptedSharedPrefs
//        return prefs.getString(key, default)
//    }
}