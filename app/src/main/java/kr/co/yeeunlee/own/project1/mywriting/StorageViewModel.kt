package kr.co.yeeunlee.own.project1.mywriting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.launch

class StorageViewModel:ViewModel() {
    private val firebaseRepo: FirebaseRepository = FirebaseRepository()
    private var _storageBottleSnap: MutableLiveData<DocumentSnapshot> = MutableLiveData<DocumentSnapshot>()
    val storageBottleSnap:LiveData<DocumentSnapshot> = _storageBottleSnap

    init {

    }

    private fun getStorageBottleSnap() = viewModelScope.launch {
        firebaseRepo.getStorageBottle(_storageBottleSnap)
    }
}