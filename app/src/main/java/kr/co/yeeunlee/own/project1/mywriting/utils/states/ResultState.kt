package kr.co.yeeunlee.own.project1.mywriting.utils.states

import androidx.annotation.StringRes
import java.lang.Exception

sealed class ResultState<out T> {
    object Loading : ResultState<Nothing>()
    data class Success<out T>(val data: T) : ResultState<T>()
    data class Failed(@StringRes val message: Int) : ResultState<Nothing>()
    data class Error(val exception: Exception) : ResultState<Nothing>()
}