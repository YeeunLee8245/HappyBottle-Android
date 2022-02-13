package kr.co.yeeunlee.own.project1.mywriting

import android.content.Context
import androidx.lifecycle.*
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.launch

class HomeViewModel(): ViewModel(){
    private var _userSnapshot:MutableLiveData<DocumentSnapshot> = MutableLiveData<DocumentSnapshot>()
    val userSnapshot: LiveData<DocumentSnapshot> = _userSnapshot
//    private var _noteSnapshot:MutableLiveData<DocumentSnapshot> = MutableLiveData<DocumentSnapshot>()
//    val noteSnapshot: LiveData<DocumentSnapshot> = _noteSnapshot

    init {
        //getUserSnapshot()   // 프로필, 보틀 정보
        //getBottleSnapshot()
    }

    fun getUserSnapshot(context: Context) = viewModelScope.launch {
        val firebaseRepo: FirebaseRepository = FirebaseRepository(context)
       firebaseRepo.getUserSnapshot(_userSnapshot)
    }

    fun setUserSnapshot(snapshot: DocumentSnapshot) = viewModelScope.launch {
        _userSnapshot.value = snapshot
    }

//    private fun getBottleSnapshot() = viewModelScope.launch {
//        firebaseRepo.getBottleSnapshot(_noteSnapshot)
//    }
}