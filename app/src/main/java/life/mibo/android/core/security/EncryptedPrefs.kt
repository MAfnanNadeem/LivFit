/*
 *  Created by Sumeet Kumar on 2/26/20 1:51 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 2/26/20 1:51 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.core.security

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

class EncryptedPrefs(var context: Context) {
    //  String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
    private val PREFS_NAME = "prefs_name"
    private val ENCRYPTED_PREFS_NAME = "livfit_mibo_world"

    private val sharedPrefs by lazy {
        context.getSharedPreferences(
            PREFS_NAME, Context.MODE_PRIVATE
        )
    }

    //    implementation "androidx.security:security-crypto:1.0.0-alpha02"
    private val encryptedSharedPrefs by lazy {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        EncryptedSharedPreferences.create(
            ENCRYPTED_PREFS_NAME,
            masterKeyAlias,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun set(key: String, value: String?, encrypted: Boolean = false) {
        val prefs = if (encrypted) encryptedSharedPrefs else sharedPrefs
        with(prefs.edit()) {
            putString(key, value)
            commit()
        }
    }

    fun get(key: String, defValue: String? = null, encrypted: Boolean = false): String? {
        val prefs = if (encrypted) encryptedSharedPrefs else sharedPrefs
        return prefs.getString(key, defValue)
    }
}