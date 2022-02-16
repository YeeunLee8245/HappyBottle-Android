package kr.co.yeeunlee.own.project1.mywriting

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class StorageViewModel:ViewModel() {
    private var __stgBottleLi = ArrayList<BottleList>()
    private var _stgBottleLi: MutableLiveData<ArrayList<BottleList>> = MutableLiveData<ArrayList<BottleList>>()
    val stgBtSnapLi:LiveData<ArrayList<BottleList>> = _stgBottleLi

    init {
        _stgBottleLi.value = __stgBottleLi
    }

    fun getStorageBottleLi(context: Context, zeroBottle:MutableLiveData<Boolean>) = viewModelScope.launch {
        val firebaseRepo = FirebaseRepository(context)
        firebaseRepo.getStorageBottleLi(_stgBottleLi, __stgBottleLi, zeroBottle)
    }

    fun getSize(): Int{
        return __stgBottleLi.size
    }
}

data class BottleList(var first: Int? = null, var second: Int? = null, var third: Int? = null)