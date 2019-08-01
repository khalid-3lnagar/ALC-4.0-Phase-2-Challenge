package khalid.elnagar.travelmantics.domain

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.OnLifecycleEvent
import khalid.elnagar.travelmantics.entities.TravelDeal

//save to data base

class SaveDealUseCase(
    private val loadingProgressBar: MutableLiveData<Boolean>,
    private val firebaseGetWay: FirebaseGetWay = FirebaseGetWay()
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

class ReadDealsUseCase(
    private val deals: MutableLiveData<List<TravelDeal>>,
    private val firebaseGetWay: FirebaseGetWay = FirebaseGetWay()
) : LifecycleObserver {

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun startListining() {
        firebaseGetWay.startListening(deals)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun stopListinig() {
        firebaseGetWay.stopListening()
    }

}