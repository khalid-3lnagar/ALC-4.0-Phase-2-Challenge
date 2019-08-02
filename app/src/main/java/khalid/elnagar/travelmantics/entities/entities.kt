package khalid.elnagar.travelmantics.entities

import android.os.Parcel
import android.os.Parcelable

data class TravelDeal(
    var dealTitle: String = "",
    var price: String = "",
    var description: String = "",
    var imageURL: String = "",
    var id: String = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(dealTitle)
        parcel.writeString(price)
        parcel.writeString(description)
        parcel.writeString(imageURL)
        parcel.writeString(id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TravelDeal> {
        override fun createFromParcel(parcel: Parcel): TravelDeal {
            return TravelDeal(parcel)
        }

        override fun newArray(size: Int): Array<TravelDeal?> {
            return arrayOfNulls(size)
        }
    }
}