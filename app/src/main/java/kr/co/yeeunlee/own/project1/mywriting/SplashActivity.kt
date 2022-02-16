package kr.co.yeeunlee.own.project1.mywriting

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {
    companion object{
        val gso: GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("702537519034-81ob9l15coeebj4kgu1cakoub43555aa.apps.googleusercontent.com")
            .requestEmail()
            .build()
        val mAuth: FirebaseAuth = Firebase.auth
        val db = Firebase.firestore
    }
    private val settings = firestoreSettings {
        isPersistenceEnabled = true
        setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED).build()
    }
    init {
        db.firestoreSettings = settings // 캐시와 오프라인 지속성 설정
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_splash)
        super.onCreate(savedInstanceState)

        val fireRepo = FirebaseRepository(this)
        val intentMain = Intent(this,MainActivity::class.java)
        intentMain.action = Intent.ACTION_MAIN
        intentMain.addCategory(Intent.CATEGORY_LAUNCHER)
        intentMain.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP

        val intentStart = Intent(this,LoginStartActivity::class.java)
        intentStart.action = Intent.ACTION_MAIN
        intentStart.addCategory(Intent.CATEGORY_LAUNCHER)
        intentStart.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        val account = mAuth.currentUser
        Log.e("기존 계정정보","${mAuth.currentUser?.email}")
        if (account != null) {
            CoroutineScope(Dispatchers.Main).launch {
                val userData = fireRepo.getNameImgSnapshot()
                fireRepo.setToken()
                Log.d("계정 오류잡기2", userData.toString())
                intentMain.putExtra(LoginStartActivity.NAME_TAG, userData.get("name").toString())
                intentMain.putExtra(LoginStartActivity.PROFILE_IMG_TAG, userData.get("profileImg").toString().toInt())
                Toast.makeText(
                    this@SplashActivity,
                    "구글 계정 로그인 성공${account.email}",
                    Toast.LENGTH_SHORT
                ).show()
                startActivity(intentMain)
                finish()    // 로그인 시작창은 스택에서 삭제
            }
        }else {
            startActivity(intentStart)
            finish()
            Toast.makeText(this@SplashActivity, "계정 로그인 필요", Toast.LENGTH_SHORT).show()
        }

    }
}