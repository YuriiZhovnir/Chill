package jdroidcoder.ua.chill.response

import com.google.gson.annotations.SerializedName

/**
 * Created by jdroidcoder on 13.07.2018.
 */
data class Author(@SerializedName("id")var  id :Int?,
                  @SerializedName("position_id") var positionId:Int?,
                  @SerializedName("full_name")var fullName:String?,
                  @SerializedName("description")var description:String?,
                  @SerializedName("photo_url")var photoUrl:String?,
                  @SerializedName("position")var position:Position?)