package khalid.elnagar.travelmantics.entities

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TravelDeal(
    var dealTitle: String = "",
    var price: String = "",
    var description: String = "",
    var imageURL: String = "",
    var id: String = "",
    var imageName: String = ""
) : Parcelable