package khalid.elnagar.travelmantics.domain

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import khalid.elnagar.travelmantics.entities.TravelDeal

private const val TRAVEL_DEALS_KEY = "traveldeals"
private const val DEALS_IMAGES_KEY = "deals_images"

object FirebaseGetWay {
    //database
    private val firebaseDatabase by lazy { FirebaseDatabase.getInstance() }
    private val databaseReference by lazy { firebaseDatabase.reference.child(TRAVEL_DEALS_KEY) }
    private val deals = mutableListOf<TravelDeal>().toMutableLiveData()
    //auth
    private val firebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val childEventListener by lazy {
        object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, p1: String?) {
                //deal
                snapshot.getValue(TravelDeal::class.java)
                    ?.apply { id = snapshot.key!! }
                    ?.also { Log.d(TAG, "Deal ${it.dealTitle}") }
                    ?.also { deals.value!!.add(it) }
                    ?.also { deals.postValue(deals.value) }

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
    //storage
    private val firebaseStorage by lazy { FirebaseStorage.getInstance() }
    private val storageReference by lazy { firebaseStorage.reference.child(DEALS_IMAGES_KEY) }

    fun retrieveDeals() = deals as LiveData<List<TravelDeal>>

    fun saveTravelDeal(travelDeal: TravelDeal): Task<Void> {
        return if (travelDeal.id == "")
            databaseReference.push().setValue(travelDeal)
        else
            databaseReference.child(travelDeal.id).setValue(travelDeal)
    }

    fun startDatabaseListening() {
        deals.value?.clear()
        deals.postNewValue()
        databaseReference.addChildEventListener(childEventListener)
    }

    fun stopDatabaseListening() = databaseReference.removeEventListener(childEventListener)

    fun deleteTravelById(id: String) = databaseReference.child(id).removeValue()

    fun attachFbAuthListener(authStateListener: FirebaseAuth.AuthStateListener) {
        firebaseAuth.addAuthStateListener(authStateListener)
    }

    fun detachFbAuthListener(authStateListener: FirebaseAuth.AuthStateListener) {
        firebaseAuth.removeAuthStateListener(authStateListener)
    }

    fun uploadImage(file: Uri) =
        file.lastPathSegment
            ?.split("/")
            ?.let { storageReference.child(it.last()) }
            ?.putFile(file)

    fun deleteImage(name: String) =
        name.also { Log.d(TAG, "deleteImage$it") }
            .let { storageReference.child(name).delete() }
            .addOnSuccessListener { Log.d(TAG, "Image Successfully deleted") }
            .addOnFailureListener { Log.d(TAG, "delete Image ${it.message}") }


}