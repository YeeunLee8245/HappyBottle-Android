package kr.co.yeeunlee.own.project1.mywriting

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Response

class SendViewModel(application: Application): AndroidViewModel(application) {
    private var _myResponce: MutableLiveData<Response<ResponseBody>> = MutableLiveData<Response<ResponseBody>>()
    val myResponce: LiveData<Response<ResponseBody>> = _myResponce
    private var __checkPost: ArrayList<Note> = ArrayList<Note>()
    private var _checkPost: MutableLiveData<ArrayList<Note>> = MutableLiveData<ArrayList<Note>>()
    val checkPost: LiveData<ArrayList<Note>> = _checkPost

    init {
        _checkPost.value = __checkPost
    }

    fun sendNotification(context: Context, notification:NotificationBody) = viewModelScope.launch {
        val firebaseRepo: FirebaseRepository = FirebaseRepository(context)
        firebaseRepo.sendNotification(_myResponce, notification)
    }

    fun getPostSnapshot(context: Context) = viewModelScope.launch{
        val firebaseRepo: FirebaseRepository = FirebaseRepository(context)
        firebaseRepo.getPostSnapshot(__checkPost,_checkPost)
    }
}