package khalid.elnagar.travelmantics


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import khalid.elnagar.travelmantics.domain.ReadDealsUseCase
import khalid.elnagar.travelmantics.domain.RetrieveDealsUseCase
import khalid.elnagar.travelmantics.domain.TAG
import khalid.elnagar.travelmantics.entities.TravelDeal
import kotlinx.android.synthetic.main.activity_list.*
import kotlinx.android.synthetic.main.item_travel.view.*

const val DEAL_EXTRA_INTENT = "deal"

//region View
class ListActivity : AppCompatActivity() {
    private val model by lazy { ViewModelProviders.of(this).get(ListViewModel::class.java) }
    private val dealsLayoutManger by lazy { LinearLayoutManager(this) }

    private val dealsAdapter by lazy { DealsListAdapter(model.deals, this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
        lifecycle.addObserver(model.readDealsUseCase)

        model.deals.observe(this, Observer { if (it.isNullOrEmpty()) showEmptyText() else showList() })
        with(rv_deals) {
            layoutManager = dealsLayoutManger
            adapter = dealsAdapter
        }
    }

    private fun showEmptyText() {
        txtLoading.visibility = View.VISIBLE
        rv_deals.visibility = View.INVISIBLE
    }


    private fun showList() {
        txtLoading.visibility = View.GONE
        rv_deals.visibility = View.VISIBLE
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.list_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_add -> {
                Intent(this, TravelActivity::class.java)
                    .also(::startActivity)
                true
            }

            else -> return super.onOptionsItemSelected(item)
        }

    }
}

//endregion

//region ViewModel
class ListViewModel(
    val deals: LiveData<List<TravelDeal>> = RetrieveDealsUseCase()(),
    val readDealsUseCase: ReadDealsUseCase = ReadDealsUseCase()
) : ViewModel()

//endregion

//region  RecyclerAdapter
class DealsListAdapter(private val deals: LiveData<List<TravelDeal>>, lifecycleOwner: LifecycleOwner) :
    RecyclerView.Adapter<DealsListAdapter.DealViewHolder>() {

    inner class DealViewHolder(view: View) : RecyclerView.ViewHolder(view)

    init {
        deals.observe(lifecycleOwner, Observer { notifyDataSetChanged() })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DealViewHolder =
        LayoutInflater.from(parent.context).inflate(R.layout.item_travel, parent, false)
            .let(::DealViewHolder)

    override fun getItemCount(): Int = deals.value?.size ?: 0

    override fun onBindViewHolder(holder: DealViewHolder, position: Int) {
        val deal = deals.value!![position]

        with(holder.itemView) {
            itemDealTitle.text = deal.dealTitle
            itemDealDescription.text = deal.description
            itemDealPrice.text = deal.price
            setOnClickListener {
                Log.d(TAG, "$position clicked")
                Intent(context, TravelActivity::class.java)
                    .putExtra(DEAL_EXTRA_INTENT, deal)
                    .also(context::startActivity)

            }
        }
    }

}
//endregion