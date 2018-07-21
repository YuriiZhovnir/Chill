package jdroidcoder.ua.chill.response

import com.google.gson.annotations.SerializedName

/**
 * Created by jdroidcoder on 21.07.2018.
 */
class Profile(@SerializedName("statistics") var statistics: Statistic,
              @SerializedName("histories") var history: ArrayList<Statistic>)