package khalid.elnagar.travelmantics.domain

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.OnLifecycleEvent
import khalid.elnagar.travelmantics.entities.TravelDeal

//save to data base

class SaveDealUseCase(
    private val loadingProgressBar: MutableLiveData<Boolean>,
    private val firebaseGetWay: FirebaseGetWay = FirebaseGetWay
) {

    fun saveDeal(travelDeal: TravelDeal, onSuccess: () -> Unit, onFailure: () -> Unit) {

        loadingProgressBar.value
            .takeUnless { it == true || travelDeal.isBlank() }
            ?.also { loadingProgressBar.postValue(true) }
            ?.let { firebaseGetWay.saveTravelDeal(travelDeal) }
            ?.addOnSuccessListener { onSuccess() }
            ?.addOnFailureListener { onFailure() }
            ?.addOnCompleteListener { loadingProgressBar.postValue(false) }

    }

    private fun TravelDeal.isBlank(): Boolean =
        price.isBlank() && dealTitle.isBlank() && description.isBlank() && imageURL.isBlank()


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
    fun startListening() {
        firebaseGetWay.startListening()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun stopListening() {
        firebaseGetWay.stopListening()
    }

}

//delete Travel by id
class DeleteTravelById(
    private val firebaseGetWay: FirebaseGetWay = FirebaseGetWay
) {
    operator fun invoke(id: String, onSuccess: () -> Unit, onFailure: () -> Unit) {
        firebaseGetWay.deleteTravelById(id)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure() }

    }
}