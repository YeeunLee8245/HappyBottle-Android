package kr.co.kumoh.d134.isl.base

import androidx.viewbinding.ViewBinding
import kr.co.yeeunlee.own.project1.mywriting.utils.states.ActivityState

interface BaseContractView {
    fun subscribeUi()
    fun loadErrorMessage(e: Throwable)
    fun moveToScreen(screen: ActivityState)
}