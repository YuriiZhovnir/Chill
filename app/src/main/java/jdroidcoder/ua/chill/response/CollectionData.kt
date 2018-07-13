package jdroidcoder.ua.chill.response

import com.google.gson.annotations.SerializedName

/**
 * Created by jdroidcoder on 13.07.2018.
 */
data class CollectionData(@SerializedName("id")var id:Int?,
                          @SerializedName("collection_id")var collectionId:Int?,
                          @SerializedName("title")var title:String?,
                          @SerializedName("number")var number:Int?,
                          @SerializedName("audio_url")var audioUrl:String?,
                          @SerializedName("audio_duration")var audioDuration:Int?)