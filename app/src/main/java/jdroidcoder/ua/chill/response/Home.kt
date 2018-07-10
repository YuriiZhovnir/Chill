package jdroidcoder.ua.chill.response

import com.google.gson.annotations.SerializedName

/**
 * Created by jdroidcoder on 10.07.2018.
 */
data class Home(@SerializedName("continue")var continueArray:ArrayList<CollectionItem>?,
                @SerializedName("default")var defaultArray:ArrayList<CollectionItem>?,
                @SerializedName("recommended")var recommendedArray:ArrayList<CollectionItem>?)