package khalid.elnagar.travelmantics

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import khalid.elnagar.travelmantics.domain.DeleteTravelById
import khalid.elnagar.travelmantics.domain.SaveDealUseCase
import khalid.elnagar.travelmantics.domain.toMutableLiveData
import khalid.elnagar.travelmantics.entities.TravelDeal
import kotlinx.android.synthetic.main.activity_travel.*


class TravelActivity : AppCompatActivity() {
    private val model by
    lazy { ViewModelProviders.of(this).get(MakeDealViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_travel)
        model.loadingLD.observe(this, Observer { progress.visibility = if (it) View.VISIBLE else View.GONE })
        model.travelDeal.observe(this, Observer { it?.also(::editTravel) })
        intent
            .getParcelableExtra<TravelDeal>(DEAL_EXTRA_INTENT)
            ?.also { model.travelDeal.postValue(it) }


    }

    private fun editTravel(deal: TravelDeal) {
        txtTitle.setText(deal.dealTitle)
        txtDescription.setText(deal.description)
        txtPrice.setText(deal.price)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater
            .inflate(R.menu.travel_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {


        return when (item.itemId) {
            R.id.action_save -> {
                createDeal()
                model.saveDeal(::onSavedSuccessful, ::onFailure)
                true
            }
            R.id.action_delete -> {
                model.deleteDeal(::onDeletedSuccessful, ::onFailure)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun createDeal() {
        model.travelDeal.value
            ?.apply {
                dealTitle = txtTitle.text.toString()
                price = txtPrice.text.toString()
                description = txtDescription.text.toString()
            }
    }

    private fun onSavedSuccessful() {
        showToast("deal is saved ")
        finish()
    }

    private fun onDeletedSuccessful() {
        showToast("deal is deleted")
        finish()
    }

    private fun onFailure() {
        showToast("error happen")
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}

//viewModel
class MakeDealViewModel(
    val loadingLD: MutableLiveData<Boolean> = false.toMutableLiveData(),
    val travelDeal: MutableLiveData<TravelDeal> = TravelDeal().toMutableLiveData(),
    private val deleteTravelById: DeleteTravelById = DeleteTravelById(),
    private val saveUseCase: SaveDealUseCase = SaveDealUseCase(loadingLD)
) : ViewModel() {

    fun saveDeal(onSuccess: () -> Unit, onFailure: () -> Unit) {
        saveUseCase.saveDeal(travelDeal.value!!, onSuccess, onFailure)
    }

    fun deleteDeal(onSuccess: () -> Unit, onFailure: () -> Unit) {
        deleteTravelById.invoke(travelDeal.value!!.id, onSuccess, onFailure)
    }

}
