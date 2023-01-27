package kr.co.yeeunlee.own.project1.mywriting.data.firebase

import androidx.lifecycle.LiveData
import kr.co.yeeunlee.own.project1.mywriting.data.model.User
import kr.co.yeeunlee.own.project1.mywriting.utils.states.AuthenticationState

class UserLiveData : LiveData<Pair<User?, AuthenticationState>>() {
}