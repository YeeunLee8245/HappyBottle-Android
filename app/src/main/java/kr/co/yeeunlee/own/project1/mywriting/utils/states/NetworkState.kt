package kr.co.yeeunlee.own.project1.mywriting.utils.states

sealed class NetworkState {
    object Success : NetworkState()
    object Loaded : NetworkState() // 데이터 로드 됨
    object Loading : NetworkState()
    object Failed : NetworkState()
//    class Error(val message: String?) : NetworkState()
}
