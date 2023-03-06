package kr.co.yeeunlee.own.project1.mywriting.utils.states

import androidx.annotation.StringRes

sealed class AuthenticationState {
    class Authenticated(val email: String) : AuthenticationState()
    object Unauthenticated : AuthenticationState()
    class InvalidAuthentication(@StringRes val message: Int) : AuthenticationState()
}