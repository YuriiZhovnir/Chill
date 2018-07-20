package jdroidcoder.ua.chill.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by jdroidcoder on 10.07.2018.
 */
data class CollectionItem(@SerializedName("id") var id: Int?,
                          @SerializedName("title") var title: String?,
                          @SerializedName("cover_text") var coverText: String?,
                          @SerializedName("preview_photo_url") var previewPhotoUrl: String?,
                          @SerializedName("background_photo_url") var backgroundPhotoUrl: String?,
                          @SerializedName("rating") var rating: Float?,
                          @SerializedName("item_count") var itemCount: Int?,
                          @SerializedName("is_free") var isFree: Int?,
                          @SerializedName("is_favorite") var isFavorite: Int?,
                          @SerializedName("ended_count") var endedCount: Int? = 1,
                          @SerializedName("is_new") var isNew: Int?,
                          @SerializedName("is_meditation") var isMeditation: Int?,
                          @SerializedName("authors") var authors: ArrayList<Author>,
                          var selectedDay: CollectionData?,
                          @SerializedName("collection_items") var collectionItems: ArrayList<CollectionData>) : Serializable {
    companion object {
        private const val serialVersionUID: Long = 7L
    }

    fun isFree(): Boolean {
        return isFree == 1
    }

    fun isFavorite(): Boolean {
        return isFavorite == 1
    }

    fun isNew(): Boolean {
        return isNew == 1
    }

    fun isMeditation(): Boolean {
        return isMeditation == 1
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CollectionItem

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id ?: 0
    }
}
