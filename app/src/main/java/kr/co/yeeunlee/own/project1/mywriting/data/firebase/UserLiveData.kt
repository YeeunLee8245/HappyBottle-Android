package kr.co.yeeunlee.own.project1.mywriting.data.firebase

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import kr.co.yeeunlee.own.project1.mywriting.data.model.User
import kr.co.yeeunlee.own.project1.mywriting.ui.SplashActivity
import kr.co.yeeunlee.own.project1.mywriting.ui.SplashActivity.Companion.db
import kr.co.yeeunlee.own.project1.mywriting.utils.states.AuthenticationState
import kr.co.yeeunlee.own.project1.mywriting.utils.states.NetworkState
import javax.inject.Inject

class UserLiveData : LiveData<Pair<User?, AuthenticationState>>() {

    private var uiScope: CoroutineScope? = null

    @Inject
    lateinit var firebaseSettings: FirebaseSettings

    private val firebaseAuth by lazy { firebaseSettings.getAuthentication() }
    private val firebaseFireStore by lazy { firebaseSettings.getFirestore() }
    private val dbRefUser by lazy { firebaseFireStore.collection("user") }

    private val authStateListener = FirebaseAuth.AuthStateListener { firebasAuth ->
        firebasAuth.currentUser?.email?.let { email ->
            uiScope?.launch {
                val user = getUser(email) ?: User()
                this@UserLiveData.value = Pair(user, AuthenticationState.Authenticated(email))
                updateUserToken(email)
            }
        } ?: run {
            this@UserLiveData.value = Pair(null, AuthenticationState.Unauthenticated)
        }
    }

    private suspend fun getUser(email: String): User? {
        val user: User?
        try {
            val snapshot = dbRefUser.document(email).get().await()
            user = snapshot.toObject(User::class.java)
            Log.d("확인 getUser", user.toString())
        } catch (e: FirebaseFirestoreException) {
            return null
        }
        return user
    }

    private suspend fun updateUserToken(email: String) {
        var token: String?
        val user = this.value?.first
        user?.token?.let {
            if (it == "false") { // TODO: 테스트 필요
                return@let
            }

            token = updateFcmToken()
            token?.let {
                dbRefUser.document(email).update("token", token)
            }
        }

    }

    private suspend fun updateFcmToken(): String? {
        val token: String
        try {
            token = FirebaseMessaging.getInstance().token.await()
            Log.d("확인 updateFcmToken", token.toString())
        } catch (e: FirebaseException) {
            return null
        }
        return token
    }

    fun createNewUser(user: User) {
        user.email?.let { email ->
            if (user.name == null) return
            this.value = Pair(user, AuthenticationState.Authenticated(email))
            uiScope?.launch {
                updateUserToken(email)
            }
        }

    }

//    suspend fun getNameImgSnapshot(
//        callback: ((networkState: NetworkState) -> Unit)
//    ): DocumentSnapshot {
//        val userEmail = SplashActivity.mAuth.currentUser?.email ?: ""
//        var snapshot: DocumentSnapshot? = null
//        coroutineScope {
//            dbRefUser.document(userEmail)
//                .get().addOnSuccessListener {   // 왜 get이 자꾸 null로 반환될까?
//                    snapshot = it
//                }
//                .addOnFailureListener {
//                    //ToastUtil.showToast(it)
//                    callback.invoke(NetworkState.Failed)
//                }
//        }.await()
//        return snapshot!!
//    }

    fun getUserSnapshot(_userSnapshot: MutableLiveData<DocumentSnapshot>): ListenerRegistration {
        val userEmail = SplashActivity.mAuth.currentUser?.email ?: ""
        var listenerRgst: ListenerRegistration? = null
        listenerRgst = dbRefUser.document(userEmail)   // 변경이 있으면 다시 업뎃
            .addSnapshotListener(MetadataChanges.INCLUDE) { snapshot, e ->
                Log.d("하나만 수행", "하나만")
                // 동시에 두 번 동작되는 이유: 쓰기 보류 중, 쓰기 보류 중 아님 상태를 연속으로 두 번 알림(field, note)
                // < 다큐먼트 안에 컬렉션도 있어서인듯..
                if (snapshot?.get("name") == null)  // 탈퇴할 때 에러방지
                    return@addSnapshotListener
                _userSnapshot.value = snapshot
            }
        return listenerRgst
    }

//    fun setUserStatusMsg(
//        newStatus: String,
//        callback: ((networkState: NetworkState) -> Unit)
//    ) {
//        val userEmail = SplashActivity.mAuth.currentUser?.email ?: ""
//        dbRefUser.document(userEmail)
//            .update("statusMsg", newStatus).addOnFailureListener {
//                //makeToast(it)
//                callback.invoke(NetworkState.Failed)
//            }
//    }

    suspend fun setToken(i: Int = 100): String {
        val userEmail = SplashActivity.mAuth.currentUser?.email.toString()
        var token: String = ""
        coroutineScope {
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.d("서비스 토큰", "토큰 등록에 실패함")
                    return@addOnCompleteListener
                }
                token = task.result
                Log.d("어디로", i.toString() + "  " + userEmail)
                dbRefUser.document(userEmail).get().addOnSuccessListener { document ->
                    val userName = document!!.get("name").toString()    // 사용자 이름 초기화
                    Log.d("서비스 토큰 변경", userName + token)
                    if (document == null) return@addOnSuccessListener

                    if (document["token"].toString() == "false") {
                        Log.d("서비스 토큰", "푸시 알림 거부")
                        return@addOnSuccessListener
                    } else if (document["token"].toString() != token) {
                        // 이 부분을 await으로 주었더니 정상 작동이 안된 이유는 addSnapshotListener가 사용되는 메서드 역시 비동기이기 때문.
                        // addSnapshotListener은 데베가 업데이트 되는대로 여러번 실행될 수 있기 때문에 await이 사용될 수 없다.
                        // await은 단일 결과만 기다리기 때문에 addSnapshotListener의 메서드와 패러다임이 맞지 않다고 한다.
                        db.collection("user").document(userEmail).update("token", token)
                            .addOnSuccessListener {
                                Log.d("서비스 토큰 변경success", "정상 작동")
                            }
//                            .addOnFailureListener { makeToast(it) }
                        return@addOnSuccessListener
                    }
                }

            }
//                .addOnFailureListener { makeToast(it) }
        }.await()


        return token
    }

//    suspend fun getToken(
//        receiver: String,
//        callback: ((networkState: NetworkState) -> Unit)
//    ): String {
//        var token: String? = null
//        coroutineScope {
//            dbRefUser.whereEqualTo("name", receiver)
//                .get().addOnSuccessListener { dcms ->
//                    for (dcm in dcms) {
//                        token = dcm.get("token").toString()
//                    }
//                }
//                .addOnFailureListener {
//                    //makeToast(it)
//                    callback.invoke(NetworkState.Failed)
//                }
//        }.await()
//        return token.toString()
//    }

    override fun onActive() {
        uiScope = CoroutineScope(Job() + Dispatchers.Main) // TODO: Job() 삭제해보기
        firebaseAuth.addAuthStateListener(authStateListener)
    }

    override fun onInactive() {
        firebaseAuth.removeAuthStateListener(authStateListener)
        uiScope?.cancel()
    }
}