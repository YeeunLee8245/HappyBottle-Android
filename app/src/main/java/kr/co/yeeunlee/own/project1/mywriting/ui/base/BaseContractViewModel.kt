package kr.co.yeeunlee.own.project1.mywriting.ui.base

interface BaseContractViewModel {
//    fun observeResultCode(resResult: ResponseResult?)
    fun observeErrorMessage(e: Throwable)
}