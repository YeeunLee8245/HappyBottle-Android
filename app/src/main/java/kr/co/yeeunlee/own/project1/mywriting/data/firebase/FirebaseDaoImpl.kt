package kr.co.yeeunlee.own.project1.mywriting.data.firebase

import com.google.firebase.firestore.FieldValue
import kr.co.yeeunlee.own.project1.mywriting.data.model.User
import kr.co.yeeunlee.own.project1.mywriting.utils.states.NetworkState

class FirebaseDaoImpl { // Query를 통해 DB(Firebase)를 다루는 객체

    private val firebaseFireStore = FirebaseSettings.getFirestore()

    private val dbRefUser by lazy { firebaseFireStore.collection("user") }
    private val dbRefCheck by lazy { firebaseFireStore.collection("check") }

    private val userLiveData = UserLiveData()
//    private val userLiveData = Transformations.map(user) { userWithAuth -> // TODO: user 값 수정될 떄마다 진행할 처리
//        // TODO: 다른 클래스에서 userLiveData 속성이 쓰일 떄 여기서 관련 속성 초기화해주기
//    }

    companion object {
        const val BOTTLE_SIZE = 30
        const val SHELF_SIZE = 3
    }

    private fun setUserInformation(user: User, callback: (userStatus: NetworkState) -> Unit) {
        val userEmail = userLiveData.value?.first?.email ?: run {
            callback(NetworkState.Failed)
            return
        }
        dbRefUser.document(userEmail).set(user)
            .addOnSuccessListener {
                userLiveData.createNewUser(user)
                callback(NetworkState.Success)
            }
            .addOnFailureListener {
                callback(NetworkState.Failed)
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
            .addOnFailureListener { callback(NetworkState.Failed) }
    }


}