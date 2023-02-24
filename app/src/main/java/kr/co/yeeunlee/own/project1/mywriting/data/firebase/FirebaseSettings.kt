package kr.co.yeeunlee.own.project1.mywriting.data.firebase

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.app
import com.google.firebase.ktx.options
import com.jakewharton.threetenabp.AndroidThreeTen.init
import dagger.hilt.android.qualifiers.ApplicationContext
import kr.co.yeeunlee.own.project1.mywriting.R
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseSettings @Inject constructor(private val applicationContext: Context) {

    private val settings = firestoreSettings {
        isPersistenceEnabled = true
        setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED).build()
    }

    fun getFirestore(): FirebaseFirestore {
        val firestore = FirebaseFirestore.getInstance()
        firestore.firestoreSettings = settings
        return firestore
    }

    fun getAuthentication() = FirebaseAuth.getInstance()

    fun getSignInClient(): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(
            applicationContext.resources.getString(
                R.string.google_sign_request_token
            )
        )
            .requestEmail() // TODO: 사용처 의문, 삭제 후 테스트 필요(참고: https://stackoverflow.com/questions/49017777/android-google-sign-in-difference-between-default-sign-in-and-default-game)
            .build()
        return GoogleSignIn.getClient(applicationContext, gso)
    }

}