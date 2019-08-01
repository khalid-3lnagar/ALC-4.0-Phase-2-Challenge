package khalid.elnagar.travelmantics.domain

import com.google.firebase.database.FirebaseDatabase
import khalid.elnagar.travelmantics.entities.TravelDeal

const val TRAVEL_DEALS_KEY = "traveldeals"

class FirebaseGetWay {
    private val firebaseDatabase by lazy { FirebaseDatabase.getInstance() }
    private val databaseReference by lazy { firebaseDatabase.reference.child(TRAVEL_DEALS_KEY) }

    fun saveTravelDeal(travelDeal: TravelDeal) = databaseReference.push().setValue(travelDeal)
}
