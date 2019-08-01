package khalid.elnagar.travelmantics.domain

import androidx.lifecycle.MutableLiveData
import khalid.elnagar.travelmantics.entities.TravelDeal

//save to data base

class DealUseCases(
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