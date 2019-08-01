package khalid.elnagar.travelmantics.domain

import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import khalid.elnagar.travelmantics.entities.TravelDeal

const val TRAVEL_DEALS_KEY = "traveldeals"

class FirebaseGetWay {
    private val firebaseDatabase by lazy { FirebaseDatabase.getInstance() }
    private val databaseReference by lazy { firebaseDatabase.reference.child(TRAVEL_DEALS_KEY) }
    private val deals = mutableListOf<TravelDeal>()
    private lateinit var travelDeals: MutableLiveData<List<TravelDeal>>

    private val childEventListener by lazy {
        object : ChildEventListener {

            override fun onChildAdded(snapshot: DataSnapshot, p1: String?) {
                snapshot
                    .getValue(TravelDeal::class.java)
                    ?.also { deals.add(it) }

                travelDeals.postValue(deals)

            }

            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildRemoved(p0: DataSnapshot) {
            }

        }
    }

    fun saveTravelDeal(travelDeal: TravelDeal) = databaseReference.push().setValue(travelDeal)

    fun startListening(travelDeals: MutableLiveData<List<TravelDeal>>) {

        this.travelDeals = travelDeals
        databaseReference.addChildEventListener(childEventListener)

    }

    fun stopListening() = databaseReference.removeEventListener(childEventListener)

}
