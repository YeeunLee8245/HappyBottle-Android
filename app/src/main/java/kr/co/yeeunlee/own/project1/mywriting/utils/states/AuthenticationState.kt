package kr.co.yeeunlee.own.project1.mywriting.utils.states

sealed class AuthenticationState {
    class Authenticated(val email: String) : AuthenticationState()
    object Unauthenticated : AuthenticationState()
    object InvalidAuthentication : AuthenticationState()
}