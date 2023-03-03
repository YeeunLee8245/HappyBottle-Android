package kr.co.yeeunlee.own.project1.mywriting.utils.states

import androidx.annotation.StringRes

sealed class NetworkState {
    object Success : NetworkState() // 데이터 로드 성공
    object Loading : NetworkState() // 데이터 로드 중
    class Failed(@StringRes val message: Int) : NetworkState() // 데이터 로드 실패
//    class Error(val message: String?) : NetworkState()
}
