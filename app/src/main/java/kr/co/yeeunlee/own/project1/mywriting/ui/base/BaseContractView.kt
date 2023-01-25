package kr.co.kumoh.d134.isl.base

interface BaseContractView {
    fun subscribeUi()
    fun getLayoutRes(): Int
//    fun loadResultCode(resResult: ResponseResult?)
    fun loadErrorMessage(e: Throwable)
}