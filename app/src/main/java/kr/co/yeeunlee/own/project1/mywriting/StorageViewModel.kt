package kr.co.yeeunlee.own.project1.mywriting

import android.app.Application
import android.content.Context
import androidx.lifecycle.*
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.launch

class StorageViewModel(application: Application): AndroidViewModel(application) {
    private var listenerRgst: ListenerRegistration? = null
    private var __stgBottleLi = ArrayList<BottleList>()
    private var _stgBottleLi: MutableLiveData<ArrayList<BottleList>> = MutableLiveData<ArrayList<BottleList>>()
    val stgBtSnapLi:LiveData<ArrayList<BottleList>> = _stgBottleLi
    val mapplication = application

    init {
        _stgBottleLi.value = __stgBottleLi
    }

    fun getStorageBottleLi(zeroBottle:MutableLiveData<Boolean>) = viewModelScope.launch {
        val firebaseRepo = FirebaseRepository(mapplication)
        if (listenerRgst == null)
            listenerRgst = firebaseRepo.getStorageBottleLi(_stgBottleLi, __stgBottleLi, zeroBottle)
    }

    fun getSize(): Int{
        return __stgBottleLi.size
    }
}