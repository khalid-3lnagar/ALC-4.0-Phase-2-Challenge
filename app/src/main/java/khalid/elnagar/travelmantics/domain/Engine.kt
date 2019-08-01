package khalid.elnagar.travelmantics.domain

import androidx.lifecycle.MutableLiveData

fun <T> T.toMutableLiveData(): MutableLiveData<T> = MutableLiveData<T>().also { it.postValue(this) }

val Any.TAG: String get() = javaClass.simpleName
