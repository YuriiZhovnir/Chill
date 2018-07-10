package jdroidcoder.ua.chill.response

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

/**
 * Created by jdroidcoder on 09.07.2018.
 */
data class Preference(var id: Int, var name: String,
                      @SerializedName("photo_url")var photoUrl:String) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readString(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeString(photoUrl)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Preference> {
        override fun createFromParcel(parcel: Parcel): Preference {
            return Preference(parcel)
        }

        override fun newArray(size: Int): Array<Preference?> {
            return arrayOfNulls(size)
        }
    }
}