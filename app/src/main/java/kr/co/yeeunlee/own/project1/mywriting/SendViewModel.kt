package kr.co.yeeunlee.own.project1.mywriting

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.launch

class SendViewModel(application: Application): AndroidViewModel(application) {
    private val firebaseRepo: FirebaseRepository = FirebaseRepository()
    private var _sendSnapshot: MutableLiveData<DocumentSnapshot> = MutableLiveData<DocumentSnapshot>()
    val sendSnapshot: LiveData<DocumentSnapshot> = _sendSnapshot

    init {
        getNewSendSnapshot()
    }

    private fun getNewSendSnapshot() = viewModelScope.launch {
        firebaseRepo.getNewCommentSnapshot(_sendSnapshot)
    }
}