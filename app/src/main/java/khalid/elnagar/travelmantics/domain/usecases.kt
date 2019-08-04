package khalid.elnagar.travelmantics.domain

import android.net.Uri
import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import khalid.elnagar.travelmantics.entities.TravelDeal

//save to data base

class SaveDealUseCase(
    private val loadingProgressBar: MutableLiveData<Boolean>,
    private val firebaseGetWay: FirebaseGetWay = FirebaseGetWay
) {

    fun saveDeal(travelDeal: LiveData<TravelDeal>, onSuccess: () -> Unit, onFailure: () -> Unit) {

        loadingProgressBar.value
            .takeUnless { it == true || travelDeal.value.isBlank() }
            ?.also { loadingProgressBar.postValue(true) }
            ?.let { firebaseGetWay.saveTravelDeal(travelDeal.value!!) }
            ?.addOnSuccessListener { onSuccess() }
            ?.addOnFailureListener { onFailure() }
            ?.addOnCompleteListener { loadingProgressBar.postValue(false) }

    }

    private fun TravelDeal?.isBlank(): Boolean =
        when (this) {
            null -> false
            else -> price.isBlank() && dealTitle.isBlank() && description.isBlank() && imageURL.isBlank()
        }


}

// get deals liveData
class RetrieveDealsUseCase(
    private val firebaseGetWay: FirebaseGetWay = FirebaseGetWay
) {
    operator fun invoke() = firebaseGetWay.retrieveDeals()
}

//listen to changes on database
class ReadDealsUseCase(
    private val firebaseGetWay: FirebaseGetWay = FirebaseGetWay
) : LifecycleObserver {

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun startListening() = firebaseGetWay.startDatabaseListening()
/*

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun stopListening() = firebaseGetWay.stopDatabaseListening()
*/

}

//delete Travel by id
class DeleteTravelById(
    private val loading: MutableLiveData<Boolean>,
    private val firebaseGetWay: FirebaseGetWay = FirebaseGetWay
) {
    operator fun invoke(travelDeal: TravelDeal, onSuccess: () -> Unit, onFailure: () -> Unit) {

        if (loading.value == true)
            return
        loading.postValue(true)
        val task = if (travelDeal.imageName.isNotBlank())
            firebaseGetWay.deleteImage(travelDeal.imageName)
                .onSuccessTask { firebaseGetWay.deleteTravelById(travelDeal.id) }
        else
            firebaseGetWay.deleteTravelById(travelDeal.id)

        task
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure() }
            .addOnCompleteListener { loading.postValue(false) }
    }


}

//listen to changes on auth
class AuthListenerUseCase(
    private val firebaseAuthStateListener: FirebaseAuth.AuthStateListener,
    private val firebaseGetWay: FirebaseGetWay = FirebaseGetWay
) : LifecycleObserver {

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun startListening() = firebaseGetWay.attachFbAuthListener(firebaseAuthStateListener)


    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun stopListening() = firebaseGetWay.detachFbAuthListener(firebaseAuthStateListener)

}

//upload image
class UploadImageUseCase(
    private val uploading: MutableLiveData<Boolean>,
    private val firebaseGetWay: FirebaseGetWay = FirebaseGetWay
) {
    operator fun invoke(file: Uri): StorageTask<UploadTask.TaskSnapshot>? {
        return uploading.value
            .takeUnless { it == true }
            ?.also { uploading.postValue(true) }
            ?.let { firebaseGetWay.uploadImage(file) }
            ?.addOnCompleteListener { uploading.postValue(false) }
    }
}