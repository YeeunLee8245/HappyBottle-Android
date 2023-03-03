package kr.co.yeeunlee.own.project1.mywriting.utils.states

sealed class ActivityState {
    object Login : ActivityState()
    object Signin: ActivityState()
    object Main : ActivityState()
}