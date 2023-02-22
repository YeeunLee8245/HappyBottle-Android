package kr.co.yeeunlee.own.project1.mywriting.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kr.co.yeeunlee.own.project1.mywriting.data.repository.Repository
import kr.co.yeeunlee.own.project1.mywriting.utils.states.FragmentState
import kr.co.yeeunlee.own.project1.mywriting.utils.states.NetworkState
import javax.inject.Inject

@HiltViewModel // Hilt에서 ViewModel을 등록하는 것으로 화면(EntryPoint)를 찾고 의존 객체를 정의할 수 있음
class FirebaseViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    private val _userInfoStatus = MutableLiveData<NetworkState>()
    val userInfoStatus: LiveData<NetworkState> = _userInfoStatus

    private val _fragmentState = MutableLiveData<Pair<FragmentState, Boolean>>()
    val fragmentState: LiveData<Pair<FragmentState, Boolean>> = _fragmentState

    fun setFragmentState(fragmentState: FragmentState, valid: Boolean = true) {
        _fragmentState.value = Pair(fragmentState, valid)
    }

    fun setNewUser(username: String, userEmail: String) {
        repository.setNewUser(username, userEmail) {
            _userInfoStatus.value = it
            if (it == NetworkState.Loaded) setFragmentState(FragmentState.START)
        }
    }
}