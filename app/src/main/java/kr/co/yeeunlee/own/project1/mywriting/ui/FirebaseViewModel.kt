package kr.co.yeeunlee.own.project1.mywriting.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kr.co.yeeunlee.own.project1.mywriting.data.repository.Repository
import kr.co.yeeunlee.own.project1.mywriting.utils.states.*
import javax.inject.Inject

@HiltViewModel // Hilt에서 ViewModel을 등록하는 것으로 화면(EntryPoint)를 찾고 의존 객체를 정의할 수 있음
class FirebaseViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    private val _userStatus = MutableLiveData<NetworkState>()
    val userStatus: LiveData<NetworkState> = _userStatus

    private val _logoutStatus = MutableLiveData<NetworkState>()
    val logoutStatus: LiveData<NetworkState> = _logoutStatus

    private val _loginStatus = MutableLiveData<NetworkState>()
    val loginStatus: LiveData<NetworkState> = _loginStatus

    private val _loginInGoogleStatus = MutableLiveData<NetworkState>()
    val loginInGoogleStatus: LiveData<NetworkState> = _loginInGoogleStatus

    private val _availableEmailStatus = MutableLiveData<ResultState<Pair<String, Boolean>>>()
    val availableEmailStatus: LiveData<ResultState<Pair<String, Boolean>>> = _availableEmailStatus

    private val _availableNickNameStatus = MutableLiveData<ResultState<String>>()
    val availableNickNameStatus: LiveData<ResultState<String>> = _availableNickNameStatus

    private val _activityStatus = MutableLiveData<ActivityState>()
    val activityStatus: LiveData<ActivityState> = _activityStatus

    private val _fragmentStatus = MutableLiveData<FragmentState>()
    val fragmentStatus: LiveData<FragmentState> = _fragmentStatus

    val user = repository.getUserLiveData()

    private val _authenticationStatus = MutableLiveData<AuthenticationState>()
    val authenticationStatus: LiveData<AuthenticationState> = _authenticationStatus

    fun setFragmentState(fragmentState: FragmentState) {
        _fragmentStatus.value = fragmentState
    }

    fun isLoginState() {
        repository.isLoginState {
            _authenticationStatus.value = it
        }
    }

    fun isAvailableEmail(email: String) {
        repository.isAvailableEmail(email) {
            _availableEmailStatus.value = it
        }
    }

    fun isAvailableNickName(nickname: String) {
        repository.isAvailableNickName(nickname) {
            _availableNickNameStatus.value = it
        }
    }

    fun logout() {
        repository.logout() {
            _logoutStatus.value = it
        }
    }

    fun login(email: String, password: String) {
        repository.login(email, password) {
            _loginStatus.value = it
        }
    }

    fun loginInGoogle(email: String, password: String) {
        repository.loginInGoogle {
            _loginInGoogleStatus.value = it
        }
    }

    fun addNewUser(username: String, userEmail: String) {
        repository.setNewUser(username, userEmail) {
            _userStatus.value = it
            if (it == NetworkState.Loading) setFragmentState(FragmentState.Home)
        }
    }
}