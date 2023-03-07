package kr.co.yeeunlee.own.project1.mywriting.utils

import kr.co.yeeunlee.own.project1.mywriting.R
import java.util.regex.Pattern

class LoginTextValidChecker(val email: String, val password: String) {

    fun checkNotEmpty(): Pair<Boolean, Boolean> {
        var emailValid = true
        var passwordValid = true
        if (email.trim().isEmpty())
            emailValid = false
        if (password.trim().isEmpty())
            passwordValid = false
        return Pair(emailValid, passwordValid)
    }

}


class NickNameValidChecker() {

    fun checkCondition(nickname: String): Pair<Boolean, Boolean> {
        var isCheckEmpty = true
        var isCheckLimit = true
        val str = nickname.trim()
        if (str.isEmpty())
            isCheckEmpty = false
        else if (str.length > 7)
            isCheckLimit = false
        return Pair(isCheckEmpty, isCheckLimit)
    }

    fun checkSpecialCharacters(str: String): Boolean {
        val pattern = Pattern.compile("^[a-zA-Z0-9가-힣ㄱ-ㅎㅏ-ㅣ\\u318D\\u119E\\u11A2\\u2022\\u2025a\\u00B7\\uFE55]+$") // 공백포함 특수문자 체크
        return pattern.matcher(str).matches()
    }

}