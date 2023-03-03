package kr.co.yeeunlee.own.project1.mywriting.utils

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