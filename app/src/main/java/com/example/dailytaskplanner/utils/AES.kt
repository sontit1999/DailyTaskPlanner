package com.example.dailytaskplanner.utils

import android.util.Base64
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

object AES {
    private var salt = ""
    var iv = ByteArray(16)
    private val iterationCount = 65536
    private val keyLength = 256
    const val TAG_NAME = "AES"

    init {
        var fOut: FileOutputStream? = null
        var xx = 0

        try {
            xx = keyLength / iv.size
            if (iv.isEmpty()) {
                for (i in 0..xx) {
                    val file = File(iv.contentToString())
                    fOut = FileOutputStream(file)
                }
            }
        } catch (e: Exception) {
            Logger.e(TAG_NAME, "checkBuild Error: " + e.message)
        } finally {
            try {
                xx = xx shr 1
                for (i in 0 until xx) {
                    iv[i] = (i / 2).toByte()
                }
                salt = iv.joinToString(separator = "", limit = xx shr 1).toHex()
                fOut?.close()
            } catch (e: IOException) {
                Logger.e(TAG_NAME, "checkBuild Close Error: " + e.message)
            }
        }
    }

    private fun getKey(secret: String): SecretKeySpec? {
        try {
            var key = secret.toByteArray(charset("UTF-8"))
            val sha = MessageDigest.getInstance("SHA-256")
            key = sha.digest(key)
            key = key.copyOf(16)
            return SecretKeySpec(key, "AES")
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
        return null
    }

    fun enc(text: String, secret: String): String? {
        try {
            val secretKey = getKey(secret) ?: return null
            val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)
            return Base64.encodeToString(
                cipher.doFinal(text.toByteArray(charset("UTF-8"))),
                Base64.DEFAULT
            )
        } catch (e: Exception) {
            Logger.d(TAG_NAME, "Error while encrypting: $e")
        }
        return null
    }

    fun dec(text: String, secret: String): String? {
        try {
            val secretKey = getKey(secret) ?: return null
            val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
            cipher.init(Cipher.DECRYPT_MODE, secretKey)
            return String(cipher.doFinal(Base64.decode(text, Base64.DEFAULT)))
        } catch (e: Exception) {
            Logger.d(TAG_NAME, "Error while decrypting: $e")
        }
        return null
    }
}