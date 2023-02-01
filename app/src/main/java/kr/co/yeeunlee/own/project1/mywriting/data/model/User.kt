package kr.co.yeeunlee.own.project1.mywriting.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User( // TODO: isOnLine으로 동시 접속 막기 => 동시 접속 시도할 때 로그인된 기기 로그아웃할 건지 질문
    var email: String? = null,
    var name: String? = null,
    var numNote: Long = 0,
    var numPost: Long = 0,
    var profileImg: Int = 1,
    var statusMsg: String = "",
    var token: String? = null
) : Parcelable