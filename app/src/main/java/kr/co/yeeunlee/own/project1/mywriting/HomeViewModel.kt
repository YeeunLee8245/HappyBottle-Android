package kr.co.yeeunlee.own.project1.mywriting

import androidx.lifecycle.*
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.launch

class HomeViewModel: ViewModel(){
    private val firebaseRepo: FirebaseRepository = FirebaseRepository()
    private var _userSnapshot:MutableLiveData<DocumentSnapshot> = MutableLiveData<DocumentSnapshot>()
    val userSnapshot: LiveData<DocumentSnapshot> = _userSnapshot
//    private var _noteSnapshot:MutableLiveData<DocumentSnapshot> = MutableLiveData<DocumentSnapshot>()
//    val noteSnapshot: LiveData<DocumentSnapshot> = _noteSnapshot

    init {
        getUserSnapshot()   // 프로필, 보틀 정보
        //getBottleSnapshot()
    }

    private fun getUserSnapshot() = viewModelScope.launch {
        firebaseRepo.getUserSnapshot(_userSnapshot)
    }

//    private fun getBottleSnapshot() = viewModelScope.launch {
//        firebaseRepo.getBottleSnapshot(_noteSnapshot)
//    }
}