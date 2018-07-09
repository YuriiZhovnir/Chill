package jdroidcoder.ua.chill.util

import android.content.Context
import android.util.Base64
import java.security.NoSuchAlgorithmException
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

/**
 * Created by jdroidcoder on 03.07.2018.
 */
object Utils {
    private const val TOEKN_FILE_KEY = "token_file"
    private const val TOEKN_KEY = "token"

    fun saveToken(context: Context?, token: String?) {
        context?.getSharedPreferences(TOEKN_FILE_KEY, Context.MODE_PRIVATE)?.edit()?.putString(TOEKN_KEY, token)?.apply()
    }

    fun loadToken(context: Context?): String? =
            context?.getSharedPreferences(TOEKN_FILE_KEY, Context.MODE_PRIVATE)?.getString(TOEKN_KEY, null)

    fun getSignature(data: String): String {
        var hash = ""
        try {
            val sha256HMAC = Mac.getInstance("HmacSHA256")
            val secretkey = SecretKeySpec("p85VeSta3Eyuprestadrucrujethujet".toByteArray(), "HmacSHA256")
            sha256HMAC.init(secretkey)
            hash = Base64.encodeToString(sha256HMAC.doFinal(data.toByteArray()), Base64.DEFAULT) + ""
        } catch (e: Exception) {
            println("Error")
        }
        return hash
    }

    fun md5(s: String): String {
        val MD5 = "MD5"
        try {
            val digest = java.security.MessageDigest
                    .getInstance(MD5)
            digest.update(s.toByteArray())
            val messageDigest = digest.digest()
            val hexString = StringBuilder()
            for (aMessageDigest in messageDigest) {
                var h = Integer.toHexString(0xFF and aMessageDigest.toInt())
                while (h.length < 2)
                    h = "0$h"
                hexString.append(h)
            }
            return hexString.toString()


        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }

        return ""
    }
}