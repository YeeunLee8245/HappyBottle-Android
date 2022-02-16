package kr.co.yeeunlee.own.project1.mywriting

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.launch
import okhttp3.internal.wait

class HomeViewModel(application: Application): AndroidViewModel(application){
    private var _userSnapshot:MutableLiveData<DocumentSnapshot> = MutableLiveData<DocumentSnapshot>()
    val userSnapshot: LiveData<DocumentSnapshot> = _userSnapshot
    val mapplication = application

    fun getUserSnapshot() = viewModelScope.launch {
        val firebaseRepo = FirebaseRepository(mapplication)
        firebaseRepo.getUserSnapshot(_userSnapshot)
    }

    fun setUserSnapshot(snapshot: DocumentSnapshot) = viewModelScope.launch {
        _userSnapshot.value = snapshot
    }
}