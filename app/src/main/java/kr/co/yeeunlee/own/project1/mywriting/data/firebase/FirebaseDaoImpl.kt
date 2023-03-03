package kr.co.yeeunlee.own.project1.mywriting.data.firebase

import androidx.lifecycle.Transformations
import com.google.firebase.firestore.FieldValue
import kr.co.yeeunlee.own.project1.mywriting.R
import kr.co.yeeunlee.own.project1.mywriting.data.model.User
import kr.co.yeeunlee.own.project1.mywriting.utils.states.AuthenticationState
import kr.co.yeeunlee.own.project1.mywriting.utils.states.NetworkState
import timber.log.Timber
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
        callback(NetworkState.Loading)
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
        callback(NetworkState.Loading)
        firebaseSettings.getSignInClient().signOut()
            .addOnSuccessListener { callback(NetworkState.Success) }
            .addOnFailureListener { callback(NetworkState.Failed(R.string.server_error)) }
    }

    fun login(email: String, password: String, callback: (networkStatus: NetworkState) -> Unit) {
        callback(NetworkState.Loading)
        firebaseSettings.getAuthentication().signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { callback(NetworkState.Success) }
            .addOnFailureListener {
                if (it.message?.contains("network") == true) {
                    callback(NetworkState.Failed(R.string.network_error_msg))
                } else if (it.message?.contains("password") == true) {
                    callback(NetworkState.Failed(R.string.login_password_error))
                } else { // 아이디 정보 없을 때, it.message = There is no user record corresponding to this identifier. The user may have been deleted.
                    callback(NetworkState.Failed(R.string.login_both_error))
                }
            }
    }

//    fun loginInGoogle(callback: (networkStatus: NetworkState) -> Unit) {
//        callback(NetworkState.Loading)
//        firebaseSettings.
//    }

}