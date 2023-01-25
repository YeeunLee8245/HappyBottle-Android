package kr.co.yeeunlee.own.project1.mywriting.ui

import android.app.Application
import androidx.lifecycle.*
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ListenerRegistration
import kr.co.yeeunlee.own.project1.mywriting.data.FirebaseRepository

class HomeViewModel(application: Application): AndroidViewModel(application){
    private var listenerRgst:ListenerRegistration? = null
    private var _userSnapshot:MutableLiveData<DocumentSnapshot> = MutableLiveData<DocumentSnapshot>()
    val userSnapshot: LiveData<DocumentSnapshot> = _userSnapshot
    val mapplication = application

    fun getUserSnapshot(){
        if (listenerRgst == null) {
            val firebaseRepo = FirebaseRepository(mapplication)
            // listenerRgst가 중복생성 됐던 이유는 xml에서 지정한 fragment의 class와 MainActivity에서 생성한
            // class가 일치하지 않았기 때문
            listenerRgst = firebaseRepo.getUserSnapshot(_userSnapshot)
        }
    }

    fun setUserSnapshot(snapshot: DocumentSnapshot){
        _userSnapshot.value = snapshot
    }

    fun getListener():ListenerRegistration? = listenerRgst
}