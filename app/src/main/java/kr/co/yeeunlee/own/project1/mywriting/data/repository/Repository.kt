package kr.co.yeeunlee.own.project1.mywriting.data.repository

import kr.co.yeeunlee.own.project1.mywriting.utils.states.NetworkState

interface Repository {
    fun setUser(
        username: String,
        userEmail: String,
        callback: (userStatus: NetworkState) -> Unit
    )
}