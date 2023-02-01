package kr.co.yeeunlee.own.project1.mywriting.utils.states

sealed class NetworkState {
    object Success : NetworkState()
    object Loading : NetworkState()
    object Failed : NetworkState()
    class Error(val message: String?) : NetworkState()
}
