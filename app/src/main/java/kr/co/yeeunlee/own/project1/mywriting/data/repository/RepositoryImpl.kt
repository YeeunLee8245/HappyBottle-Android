package kr.co.yeeunlee.own.project1.mywriting.data.repository

import kr.co.yeeunlee.own.project1.mywriting.data.firebase.FirebaseDaoImpl
import kr.co.yeeunlee.own.project1.mywriting.utils.states.NetworkState

class RepositoryImpl(private val firebaseDaoImpl: FirebaseDaoImpl) : Repository {

    override fun setUser(
        username: String,
        userEmail: String,
        callback: (userStatus: NetworkState) -> Unit
    ) {
        firebaseDaoImpl.setUser(username, userEmail, callback)
    }


}