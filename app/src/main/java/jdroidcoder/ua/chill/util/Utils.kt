package jdroidcoder.ua.chill.util

import android.content.Context
import android.util.Base64
import com.google.gson.GsonBuilder
import jdroidcoder.ua.chill.response.CollectionItem
import java.security.NoSuchAlgorithmException
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import okhttp3.ResponseBody
import java.io.*
import java.util.TreeSet

/**
 * Created by jdroidcoder on 03.07.2018.
 */
object Utils {
    private const val TOEKN_FILE_KEY = "token_file"
    private const val TOEKN_KEY = "token"
    private const val DOWNLOADED_COLLECTION_FILE_KEY = "downloaded_collection_file"
    private const val DOWNLOADED_COLLECTION_KEY = "downloaded_collection"

    fun saveDownloadedCollection(context: Context?, collection: CollectionItem) {
        val previousCollections = loadDownloadedCollection(context)
        val gson = GsonBuilder().create()
        val newCollectionJson = gson.toJson(collection)
        if (previousCollections?.contains(newCollectionJson) == false) {
            previousCollections?.add(newCollectionJson)
        }
        context?.getSharedPreferences(DOWNLOADED_COLLECTION_FILE_KEY, Context.MODE_PRIVATE)
                ?.edit()?.clear()?.apply()
        context?.getSharedPreferences(DOWNLOADED_COLLECTION_FILE_KEY, Context.MODE_PRIVATE)
                ?.edit()?.putStringSet(DOWNLOADED_COLLECTION_KEY, previousCollections)?.apply()
    }

    fun loadDownloadedCollection(context: Context?): MutableSet<String>? {
        return context?.getSharedPreferences(DOWNLOADED_COLLECTION_FILE_KEY, Context.MODE_PRIVATE)
                ?.getStringSet(DOWNLOADED_COLLECTION_KEY, HashSet<String>()) as HashSet<String>
    }

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

    fun writeResponseBodyToDisk(body: ResponseBody, context: Context?, fileName: String): Boolean {
        try {
            val futureStudioIconFile = File(context?.getExternalFilesDir(null).toString() + File.separator + fileName)
            var inputStream: InputStream? = null
            var outputStream: OutputStream? = null
            try {
                val fileReader = ByteArray(4096)
                var fileSizeDownloaded: Long = 0
                inputStream = body.byteStream()
                outputStream = FileOutputStream(futureStudioIconFile)
                while (true) {
                    val read = inputStream.read(fileReader)
                    if (read == -1) {
                        break
                    }
                    outputStream.write(fileReader, 0, read)
                    fileSizeDownloaded += read.toLong()
                }
                outputStream.flush()
                return true
            } catch (e: IOException) {
                e.printStackTrace()
                return false
            } finally {
                if (inputStream != null) {
                    inputStream.close()
                }
                if (outputStream != null) {
                    outputStream.close()
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        }
    }
}