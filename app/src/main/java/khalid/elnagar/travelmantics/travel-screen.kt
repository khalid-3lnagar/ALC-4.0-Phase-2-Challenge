package khalid.elnagar.travelmantics

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import khalid.elnagar.travelmantics.domain.*
import khalid.elnagar.travelmantics.entities.TravelDeal
import kotlinx.android.synthetic.main.activity_travel.*

private const val PICTURE_REC = 42

class TravelActivity : AppCompatActivity() {
    private val model by
    lazy { ViewModelProviders.of(this).get(MakeDealViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_travel)
        model.loadingLD.observe(this, Observer { loading(it) })
        model.travelDeal.observe(this, Observer { it?.also(::editTravel) })
        intent
            .getParcelableExtra<TravelDeal>(DEAL_EXTRA_INTENT)
            ?.also { model.travelDeal.postValue(it) }

        btnImage.setOnClickListener {
            startActivityForResult(createChooseImageIntent(), PICTURE_REC)
        }

    }

    private fun loading(it: Boolean) {
        progress.visibility = if (it) View.VISIBLE else View.GONE
        btnImage.isEnabled = !it
    }

    private fun createChooseImageIntent() =
        Intent(Intent.ACTION_GET_CONTENT)
            .apply {
                type = "image/jpeg"
                putExtra(Intent.EXTRA_LOCAL_ONLY, true)

            }.let { Intent.createChooser(it, "Insert Picture") }


    private fun editTravel(deal: TravelDeal) {

        if (txtTitle.text.isBlank()) txtTitle.setText(deal.dealTitle)
        if (txtDescription.text.isBlank()) txtDescription.setText(deal.description)
        if (txtPrice.text.isBlank()) txtPrice.setText(deal.price)

        deal.imageURL
            .takeUnless { it == "" }
            ?.let { Glide.with(this).load(it) }
            ?.centerInside()
            ?.into(imageDeal)
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
        Log.d(TAG, message)
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICTURE_REC && resultCode == Activity.RESULT_OK) {
            val imageUri = data?.data!!
            model.uploadImage(imageUri)
        }
    }
}

//viewModel
class MakeDealViewModel(
    val loadingLD: MutableLiveData<Boolean> = false.toMutableLiveData(),
    val travelDeal: MutableLiveData<TravelDeal> = TravelDeal().toMutableLiveData(),
    private val deleteTravelById: DeleteTravelById = DeleteTravelById(loadingLD),
    private val saveDealUseCase: SaveDealUseCase = SaveDealUseCase(loadingLD),
    private val uploadImageUseCase: UploadImageUseCase = UploadImageUseCase(loadingLD)
) : ViewModel() {
    private var uploadTask: StorageTask<UploadTask.TaskSnapshot>? = null
    fun saveDeal(onSuccess: () -> Unit, onFailure: () -> Unit) {
        saveDealUseCase.saveDeal(travelDeal, onSuccess, onFailure)
    }

    fun deleteDeal(onSuccess: () -> Unit, onFailure: () -> Unit) {
        deleteTravelById.invoke(travelDeal.value!!, onSuccess, onFailure)

    }

    fun uploadImage(file: Uri) {
        uploadImageUseCase(file)
            ?.addOnSuccessListener {
                travelDeal.value?.imageName = it.storage.name
                storeDownloadUrl(it)
                Log.d(TAG, "uploadImage \nImage ${travelDeal.value?.imageName} \nURL ${travelDeal.value?.imageURL} ")
            }
            ?.also { uploadTask = it }


    }

    private fun storeDownloadUrl(taskSnapshot: UploadTask.TaskSnapshot) {
        taskSnapshot.storage.downloadUrl
            .addOnCompleteListener {
                with(travelDeal) {
                    value?.imageURL = it.result.toString()
                    postValue(value)
                }
            }
    }

    override fun onCleared() {
        super.onCleared()
        uploadTask?.cancel()
    }
}
