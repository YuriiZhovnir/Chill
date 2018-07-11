package jdroidcoder.ua.chill.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by jdroidcoder on 11.07.2018.
 */
data class Subcategory(@SerializedName("id") var id: Int?,
                       @SerializedName("category_id") var categoryId: Int?,
                       @SerializedName("name") var name: String?) : Serializable {
    companion object {
        private const val serialVersionUID: Long = 6L
    }
}