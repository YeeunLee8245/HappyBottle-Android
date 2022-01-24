package kr.co.yeeunlee.own.project1.mywriting

import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.launch

class StorageViewModel:ViewModel() {
    private val firebaseRepo: FirebaseRepository = FirebaseRepository()
    private var __stgBottleLi = ArrayList<BottleList>()
    private var _stgBottleLi: MutableLiveData<ArrayList<BottleList>> = MutableLiveData<ArrayList<BottleList>>()
    val stgBtSnapLi:LiveData<ArrayList<BottleList>> = _stgBottleLi

    init {
        _stgBottleLi.value = __stgBottleLi
        //getStorageBottleLi()
    }

    fun getStorageBottleLi(){
        firebaseRepo.getStorageBottle(_stgBottleLi, __stgBottleLi)
    }

    fun oneMore(){
        getStorageBottleLi()
    }

    fun add(bottleList: BottleList){
        __stgBottleLi.add(bottleList)
        _stgBottleLi.value = __stgBottleLi
    }

    fun getSize(): Int{
        return __stgBottleLi.size
    }
}

data class BottleList(var first: Int? = null, var second: Int? = null, var third: Int? = null)