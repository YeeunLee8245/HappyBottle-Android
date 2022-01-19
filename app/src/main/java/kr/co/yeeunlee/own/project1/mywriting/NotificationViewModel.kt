package kr.co.yeeunlee.own.project1.mywriting

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.launch

class NotificationViewModel(application: Application): AndroidViewModel(application) {
    private val firebaseRepo: FirebaseRepository = FirebaseRepository()
    private var _commentSnapshot: MutableLiveData<DocumentSnapshot> = MutableLiveData<DocumentSnapshot>()
    val commentSnapshot: LiveData<DocumentSnapshot> = _commentSnapshot

    init {
        getNewCommentSnapshot()
    }

    private fun getNewCommentSnapshot() = viewModelScope.launch {
        firebaseRepo.getNewCommentSnapshot(_commentSnapshot)
    }
}