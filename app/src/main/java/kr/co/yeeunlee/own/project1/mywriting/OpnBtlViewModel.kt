package kr.co.yeeunlee.own.project1.mywriting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.launch

class OpnBtlViewModel: ViewModel() {    // 필요없을듯
    private val firebaseRepo: FirebaseRepository = FirebaseRepository()
    private var _opnNoteSnapshot: MutableLiveData<DocumentSnapshot> = MutableLiveData<DocumentSnapshot>()
    val opnNoteSnapshot: LiveData<DocumentSnapshot> = _opnNoteSnapshot

    fun getOpnNoteSnapshot(index: Int) = viewModelScope.launch {

    }
}