/*
 *  Created by Sumeet Kumar on 2/26/20 1:51 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 2/26/20 1:51 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.core.security

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import java.nio.charset.Charset
import java.security.NoSuchAlgorithmException
import java.security.spec.InvalidKeySpecException
import java.security.spec.KeySpec
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

class EncryptedPrefs(var context: Context) {
    //  String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
    private val PREFS_NAME = "livfit_mibo_club"
    private val ENCRYPTED_PREFS_NAME = "livfit_mibo_world"

    private val sharedPrefs by lazy {
        context.getSharedPreferences(
            PREFS_NAME, Context.MODE_PRIVATE
        )
    }


    var prefs: SharedPreferences? = null
    var cipher: AESEncyption? = null
    fun createCipher() {

    }
    fun create() {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        prefs = EncryptedSharedPreferences.create(
            ENCRYPTED_PREFS_NAME,
            masterKeyAlias,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    @Synchronized
    fun getEncrypted(): SharedPreferences? {
        if (prefs == null) {
            prefs = context.getSharedPreferences("a236d14b36e6b1856", Context.MODE_PRIVATE)
            //create()
        }
        return prefs
    }

    fun getCypher(): AESEncyption {
        if (cipher == null) {
            cipher = AESEncyption()
        }
        return cipher!!
    }

    @Synchronized
    fun initCipher() {
        prefs = context.getSharedPreferences("a236d14b36e6b1856", Context.MODE_PRIVATE)
        cipher = AESEncyption()
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
        if (encrypted) {
            val keyEnc = getCypher().encrypt(key)
            val valueEnc = getCypher().encrypt(value ?: "")
            with(getEncrypted()!!.edit()) {
                putString(keyEnc, valueEnc)
                commit()
            }

        } else {
            with(sharedPrefs.edit()) {
                putString(key, value)
                commit()
            }
        }
    }

    fun get(key: String, defValue: String? = null, encrypted: Boolean = false): String? {
        if (encrypted) {
            val value = getEncrypted()?.getString(getCypher().encrypt(key), "")
            if (value?.length == 0)
                return defValue
            return getCypher().decrypt(value)
        } else {
            return sharedPrefs?.getString(key, defValue)
        }
    }

    //fun encode(text: String) = Base64.encodeToString()


    class AESEncyption {

        private val pswdIterations = 10
        private val keySize = 128
        private val cypherInstance = "AES/CBC/PKCS5Padding"
        private val secretKeyInstance = "PBKDF2WithHmacSHA1"

        //private const val secureKey = "livfit_mibo_club"
        private val secureKey = "a236d14b265c449d9dee8af36e6b1856"

        //private const val secureSalt = "livfit_mibo_world"
        private val secureSalt = "c773ff7e0ae1e3d76f58bceee48cd92e"
        private val initializationVector = "8119745113154120"

        @Throws(Exception::class)
        fun encrypt(textToEncrypt: String): String {
            val skeySpec = SecretKeySpec(
                getRaw(secureKey, secureSalt),
                "AES"
            )
            val cipher: Cipher = Cipher.getInstance(cypherInstance)
            cipher.init(
                Cipher.ENCRYPT_MODE,
                skeySpec,
                IvParameterSpec(initializationVector.toByteArray())
            )
            val encrypted: ByteArray = cipher.doFinal(textToEncrypt.toByteArray())
            return Base64.encodeToString(encrypted, Base64.DEFAULT)
        }

        @Throws(Exception::class)
        fun decrypt(textToDecrypt: String?): String {
            val encryted_bytes: ByteArray = Base64.decode(textToDecrypt, Base64.DEFAULT)
            val skeySpec = SecretKeySpec(
                getRaw(secureKey, secureSalt),
                "AES"
            )
            val cipher: Cipher = Cipher.getInstance(cypherInstance)
            cipher.init(
                Cipher.DECRYPT_MODE,
                skeySpec,
                IvParameterSpec(initializationVector.toByteArray())
            )
            val decrypted: ByteArray = cipher.doFinal(encryted_bytes)
            // return String(decrypted, "UTF-8")
            return String(decrypted, Charset.defaultCharset())
        }

        private fun getRaw(plainText: String, salt: String): ByteArray {
            try {
                val factory: SecretKeyFactory =
                    SecretKeyFactory.getInstance(secretKeyInstance)
                val spec: KeySpec = PBEKeySpec(
                    plainText.toCharArray(),
                    salt.toByteArray(),
                    pswdIterations,
                    keySize
                )
                return factory.generateSecret(spec).getEncoded()
            } catch (e: InvalidKeySpecException) {
                e.printStackTrace()
            } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace()
            }
            return ByteArray(0)
        }
    }
}