package kr.co.yeeunlee.own.project1.mywriting

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Response

class SendViewModel(application: Application): AndroidViewModel(application) {
    private val firebaseRepo: FirebaseRepository = FirebaseRepository()
    private var _myResponce: MutableLiveData<Response<ResponseBody>> = MutableLiveData<Response<ResponseBody>>()
    val myResponce: LiveData<Response<ResponseBody>> = _myResponce


    fun sendNotification(notification:NotificationBody) = viewModelScope.launch {
        firebaseRepo.sendNotification(_myResponce, notification)
    }
}