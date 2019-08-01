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
import khalid.elnagar.travelmantics.domain.SaveDealUseCase
import khalid.elnagar.travelmantics.domain.toMutableLiveData
import khalid.elnagar.travelmantics.entities.TravelDeal
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private val model by
    lazy { ViewModelProviders.of(this).get(MakeDealViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        model.loadingLD.observe(this, Observer { progress.visibility = if (it) View.VISIBLE else View.GONE })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater
            .inflate(R.menu.save_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {


        return when (item.itemId) {
            R.id.action_save -> {
                model.saveDeal(createDeal(), ::onSuccess, ::onFailure)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun createDeal(): TravelDeal {
        return TravelDeal(
            txtTitle.text.toString(),
            txtPrice.text.toString(),
            txtDescription.text.toString(),
            ""
        )
    }

    private fun onSuccess() {
        showToast("deal is saved ")
        txtTitle.setText("")
        txtPrice.setText("")
        txtDescription.setText("")
        txtTitle.requestFocus()
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
    private val useCases: SaveDealUseCase = SaveDealUseCase(loadingLD)
) : ViewModel() {

    fun saveDeal(
        deal: TravelDeal,
        onSuccess: () -> Unit, onFailure: () -> Unit
    ) {
        useCases.saveDeal(deal, onSuccess, onFailure)
    }


}
