package kr.co.yeeunlee.own.project1.mywriting.utils.states

import androidx.annotation.StringRes

sealed class NetworkState {
    object Success : NetworkState()
    object Loaded : NetworkState() // 데이터 로드 됨
    object Loading : NetworkState()
    class Failed(@StringRes val message: Int) : NetworkState()
//    class Error(val message: String?) : NetworkState()
}
