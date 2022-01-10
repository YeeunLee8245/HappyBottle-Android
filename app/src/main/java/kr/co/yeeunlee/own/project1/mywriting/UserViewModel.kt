package kr.co.yeeunlee.own.project1.mywriting

import android.app.Application
import android.os.Parcelable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import kotlinx.parcelize.Parcelize
import java.io.Serializable

// week14-2 참고
class UserViewModel(application: Application): AndroidViewModel(application) {
    companion object{
        const val QUEUE_TAG = "UserVolleyRequest"
        const val SERVER_URL = "https://mfp1-dc22e.firebaseapp.com/__/auth/handler"
    }

    val liveUser = MutableLiveData<User>()

    private var queue:RequestQueue

    init {
        queue = Volley.newRequestQueue(application)
    }

    // 데이터베이스에서 값을 받아와서 liveUser에 저장하자.
}

//차후에 추가
@Parcelize
data class User(var name:String?, var email:String?, var vaildPassWord:Boolean, var password:String?) : Parcelable