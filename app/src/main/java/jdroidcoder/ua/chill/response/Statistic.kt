package jdroidcoder.ua.chill.response

import com.google.gson.annotations.SerializedName

/**
 * Created by jdroidcoder on 16.07.2018.
 */
data class Statistic(@SerializedName("current_streak") var currentStreak:Int?,
                     @SerializedName("mindful_days") var mindfulDays:Int?,
                     @SerializedName("longest_streak")var longestStreak:Int?)