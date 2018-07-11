package jdroidcoder.ua.chill.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by jdroidcoder on 11.07.2018.
 */
data class Category(@SerializedName("id") var id: Int?,
                    @SerializedName("name") var name: String?,
                    @SerializedName("subcategories") var subcategories: ArrayList<Subcategory>?) : Serializable {
    companion object {
        private const val serialVersionUID: Long = 5L
    }
}