package khalid.elnagar.travelmantics

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import khalid.elnagar.travelmantics.domain.ReadDealsUseCase
import khalid.elnagar.travelmantics.domain.toMutableLiveData
import khalid.elnagar.travelmantics.entities.TravelDeal
import kotlinx.android.synthetic.main.activity_list.*

class ListActivity : AppCompatActivity() {
    private val model by lazy { ViewModelProviders.of(this).get(ListViewModel::class.java) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
        model.deals.observe(this, Observer { it?.also(::showList) })
        lifecycle.addObserver(model.readDealsUseCase)

    }

    private fun showList(travelList: List<TravelDeal>) {
        val builder = StringBuilder(travelList.size)
        travelList
            .takeUnless { it.isEmpty() }
            ?.forEach { builder.append("$it \n\n ") }
            ?.let { builder.append("\n ${travelList.size}\n").toString() }
            ?.also(txtDeals::setText)
    }
}
//ViewModel

class ListViewModel(
    val deals: MutableLiveData<List<TravelDeal>> = listOf<TravelDeal>().toMutableLiveData(),
    val readDealsUseCase: ReadDealsUseCase = ReadDealsUseCase(deals)

) : ViewModel()