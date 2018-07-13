package jdroidcoder.ua.chill.response

import com.google.gson.annotations.SerializedName

/**
 * Created by jdroidcoder on 13.07.2018.
 */
data class Position(@SerializedName("id") var id: Int?,
                    @SerializedName("name") var name: String?)