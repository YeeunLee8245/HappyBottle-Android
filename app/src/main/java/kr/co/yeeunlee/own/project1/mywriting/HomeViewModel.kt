package kr.co.yeeunlee.own.project1.mywriting

import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.launch
import okhttp3.internal.wait

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
}