package jdroidcoder.ua.chill.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by jdroidcoder on 13.07.2018.
 */
data class CollectionData(@SerializedName("id") var id: Int?,
                          @SerializedName("collection_id") var collectionId: Int?,
                          @SerializedName("title") var title: String?,
                          @SerializedName("number") var number: Int,
                          @SerializedName("is_ended") var isEnded: Int?,
                          @SerializedName("audio_url") var audioUrl: String?,
                          @SerializedName("audio_duration") var audioDuration: Int?) : Serializable {
    fun isEnded(): Boolean {
        return isEnded == 1
    }

    companion object {
        private const val serialVersionUID: Long = 9L
    }
}