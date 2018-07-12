package jdroidcoder.ua.chill.response

import com.google.gson.annotations.SerializedName

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
                          @SerializedName("ended_count") var endedCount: Int?,
                          @SerializedName("is_new") var isNew: Int?) {
    fun isFree(): Boolean {
        return isFree == 0
    }

    fun isFavorite(): Boolean {
        return isFavorite == 1
    }

    fun isNew(): Boolean {
        return isNew == 1
    }
}
