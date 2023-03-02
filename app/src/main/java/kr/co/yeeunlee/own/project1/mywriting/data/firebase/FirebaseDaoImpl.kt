package kr.co.yeeunlee.own.project1.mywriting.data.firebase

import android.content.Intent
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.Transformations
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.firestore.FieldValue
import kr.co.yeeunlee.own.project1.mywriting.LoginStartActivity
import kr.co.yeeunlee.own.project1.mywriting.R
import kr.co.yeeunlee.own.project1.mywriting.data.model.User
import kr.co.yeeunlee.own.project1.mywriting.ui.SplashActivity
import kr.co.yeeunlee.own.project1.mywriting.utils.states.AuthenticationState
import kr.co.yeeunlee.own.project1.mywriting.utils.states.NetworkState
import javax.inject.Inject

class FirebaseDaoImpl @Inject constructor(private val firebaseSettings: FirebaseSettings) { // Query를 통해 DB(Firebase)를 다루는 객체

    private val firebaseFireStore by lazy { firebaseSettings.getFirestore() }
    private val firebaseAuth by lazy { firebaseSettings.getAuthentication() }

    private val dbRefUser by lazy { firebaseFireStore.collection("user") }
    private val dbRefCheck by lazy { firebaseFireStore.collection("check") }

    val user = UserLiveData()
    private val userLiveData = Transformations.map(user) { userWithAuth -> // TODO: user 값 수정될 떄마다 진행할 처리
//        // TODO: 다른 클래스에서 userLiveData 속성이 쓰일 때 여기서 관련 속성 초기화해주기
        // TODO: 토큰처리
    }

    companion object {
        const val BOTTLE_SIZE = 30
        const val SHELF_SIZE = 3
    }

    private fun setUserInformation(user: User, callback: (userStatus: NetworkState) -> Unit) {
        val userEmail = this.user.value?.first?.email ?: run {
            callback(NetworkState.Failed(R.string.service_error))
            return
        }
        dbRefUser.document(userEmail).set(user)
            .addOnSuccessListener {
                this.user.createNewUser(user)
                callback(NetworkState.Success)
            }
            .addOnFailureListener {
                callback(NetworkState.Failed(R.string.service_error))
            }
    }

    fun setNewUser(    // TODO: 닉네임 생성, 회원가입 완료 과정에서 호출
        username: String,
        userEmail: String,
        callback: (userStatus: NetworkState) -> Unit
    ) {
        val user = User()
        callback(NetworkState.Loaded)
        user.email = userEmail
        user.name = username
        dbRefCheck.document("name")
            .update("name", FieldValue.arrayUnion(username))    // 닉네임 등록
            .addOnSuccessListener {
                setUserInformation(user) { callback(it) } // setUserInformation에서 상태를 받아서 해당 callback으로 전달
            }
            .addOnFailureListener { callback(NetworkState.Failed(R.string.service_error)) }
    }

    fun isLoginState(callback: (loginState: AuthenticationState) -> Unit) {
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            val email = currentUser.email
            if (email != null) {
                callback(AuthenticationState.Authenticated(email))
                user.value
            } else {
                callback(AuthenticationState.InvalidAuthentication)
            }
        } else {
            callback(AuthenticationState.Unauthenticated)
        }
    }

    fun logout(callback: (userStatus: NetworkState) -> Unit) {
        callback(NetworkState.Loaded)
        firebaseSettings.getSignInClient().signOut()
            .addOnSuccessListener { callback(NetworkState.Success) }
            .addOnFailureListener { callback(NetworkState.Failed(R.string.server_error)) }
    }

}