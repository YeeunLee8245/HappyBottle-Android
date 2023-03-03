package kr.co.yeeunlee.own.project1.mywriting.utils

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import dagger.hilt.android.scopes.ActivityScoped
import kr.co.yeeunlee.own.project1.mywriting.data.firebase.FirebaseSettings
import javax.inject.Inject

@ActivityScoped
class GoogleSignInHelper @Inject constructor(
    private val applicationContext: Context,
    private val firebaseSettings: FirebaseSettings
) {

    fun getGoogleSignIntent(): Intent {
        val googleSignInClient = GoogleSignIn.getClient(applicationContext, firebaseSettings.gso)
        return googleSignInClient.signInIntent
    }

    fun getAccountResult(intent: Intent?, exception: Class<ApiException>): GoogleSignInAccount {
        val task = GoogleSignIn.getSignedInAccountFromIntent(intent)
        return task.getResult(exception)
    }
}