package kr.co.yeeunlee.own.project1.mywriting.data.repository

import kr.co.yeeunlee.own.project1.mywriting.data.firebase.FirebaseDaoImpl
import kr.co.yeeunlee.own.project1.mywriting.utils.states.AuthenticationState
import kr.co.yeeunlee.own.project1.mywriting.utils.states.NetworkState
import javax.inject.Inject

// 생성자에서 주석 달려면 constructor 키워드 필요
// @Inject: RepositoryImpl 클래스에 제공할 종속 객체(firebaseDaoImpl)를 가져오는 방법을 hilt에 알림
class RepositoryImpl @Inject constructor(private val firebaseDaoImpl: FirebaseDaoImpl) : Repository {

    override fun setNewUser(
        username: String,
        userEmail: String,
        callback: (userStatus: NetworkState) -> Unit
    ) {
        firebaseDaoImpl.setNewUser(username, userEmail, callback)
    }

    override fun isLoginState(callback: (loginState: AuthenticationState) -> Unit) {
        firebaseDaoImpl.isLoginState(callback)
    }

    override fun getUserLiveData() = firebaseDaoImpl.user

    override fun logout(callback: (userStatus: NetworkState) -> Unit) {
        firebaseDaoImpl.logout(callback)
    }

    override fun login(
        email: String,
        password: String,
        callback: (networkStatus: NetworkState) -> Unit
    ) {
        firebaseDaoImpl.login(email, password, callback)
    }

    override fun loginInGoogle(callback: (networkStatus: NetworkState) -> Unit) {

    }
}