package kr.co.yeeunlee.own.project1.mywriting

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

// week14-2 참고
class UserViewModel(application: Application): AndroidViewModel(application) {
    companion object{
        const val QUEUE_TAG = "UserVolleyRequest"
        const val SERVER_URL = "https://mfp1-dc22e.firebaseapp.com/__/auth/handler"
    }

    //차후에 추가
    data class User(var gso: GoogleSignInOptions, var name:String?, var email:String?, var image:String?)
    val liveUser = MutableLiveData<User>()

    private var queue:RequestQueue

    init {
        queue = Volley.newRequestQueue(application)
    }

    // 데이터베이스에서 값을 받아와서 liveUser에 저장하자.
}