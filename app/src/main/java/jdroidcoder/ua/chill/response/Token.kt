package jdroidcoder.ua.chill.response

import com.google.gson.annotations.SerializedName

/**
 * Created by jdroidcoder on 09.07.2018.
 */
data class Token(@SerializedName("access_token") var accessToken: String?,
            @SerializedName("is_subscribed") var isSubscribed: Int = 0) {

    public fun getIsSubscribed(): Boolean {
        return isSubscribed == 1
    }
}