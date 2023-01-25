package kr.co.yeeunlee.own.project1.mywriting.ui

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kr.co.yeeunlee.own.project1.mywriting.LoginStartActivity
import kr.co.yeeunlee.own.project1.mywriting.MainActivity
import kr.co.yeeunlee.own.project1.mywriting.R
import kr.co.yeeunlee.own.project1.mywriting.data.FirebaseRepository
import kr.co.yeeunlee.own.project1.mywriting.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {
    companion object{
        lateinit var gso: GoogleSignInOptions
        val mAuth: FirebaseAuth = Firebase.auth
        val db = Firebase.firestore
    }
    private val settings = firestoreSettings {
        isPersistenceEnabled = true
        setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED).build()
    }
    private lateinit var binding: ActivitySplashBinding
    init {
        db.firestoreSettings = settings // 캐시와 오프라인 지속성 설정
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(resources.getString(
            R.string.google_sign_request_token
        ))
            .requestEmail()
            .build()
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        super.onCreate(savedInstanceState)

        val fireRepo = FirebaseRepository(this)
        val intentMain = Intent(this, MainActivity::class.java)
        intentMain.action = Intent.ACTION_MAIN
        intentMain.addCategory(Intent.CATEGORY_LAUNCHER)
        intentMain.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP

        val intentStart = Intent(this, LoginStartActivity::class.java)
        intentStart.action = Intent.ACTION_MAIN
        intentStart.addCategory(Intent.CATEGORY_LAUNCHER)
        intentStart.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP

        val account = mAuth.currentUser

        if (intent.getStringExtra(MainActivity.DELETE_TAG) != null){
            CoroutineScope(Dispatchers.Main).launch {
                binding.text.text = "데이터 삭제 중입니다.\n잠시만 기다려주세요."
                userDelete()
            }
        }
        else if (account != null) {
            CoroutineScope(Dispatchers.Main).launch {
                val userData = fireRepo.getNameImgSnapshot()
                Log.d("로그인 정보", account.toString()+"  "+account.email.toString())
                fireRepo.setToken(10)
                if (intent.getStringExtra("service") != null){
                    intentMain.putExtra("service", "service")
                }
                intentMain.putExtra(LoginStartActivity.NAME_TAG, userData.get("name").toString())
                intentMain.putExtra(LoginStartActivity.PROFILE_IMG_TAG, userData.get("profileImg").toString().toInt())
                Toast.makeText(
                    this@SplashActivity,
                    "로그인 성공: ${account.email}",
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

    private suspend fun userDelete() {
        val userEmail = mAuth.currentUser!!.email.toString()
        val googleSignInClient = GoogleSignIn.getClient(this, gso)

        coroutineScope {
            val userName = intent.getStringExtra(MainActivity.USER_NAME)
            db.collection("check").document("name")
                .update("name", FieldValue.arrayRemove(userName))
                .addOnFailureListener {e -> makeErrorAlter(e)}
        }.await()

        coroutineScope {
            db.collection("user").document(userEmail)
                .collection("note").get().addOnSuccessListener {
                    it.documents.forEach {
                        db.runBatch { batch ->
                            batch.delete(it.reference)
                        }.addOnFailureListener {e -> makeErrorAlter(e)}
                    }
                }
            db.collection("user").document(userEmail)
                .collection("postbox").get().addOnSuccessListener {
                    it.documents.forEach {
                        db.runBatch { batch ->
                            batch.delete(it.reference)
                        }.addOnFailureListener { e -> makeErrorAlter(e) }
                    }
                }
        }.await()

        coroutineScope {
            db.collection("user").document(userEmail)    // 비동기 주의
                .delete()
                .addOnFailureListener { e -> makeErrorAlter(e) }
        }.await()

        mAuth.currentUser!!.delete().await()    // 계정 삭제

        coroutineScope {
            Log.d("로그아웃","1")
            googleSignInClient.signOut().addOnSuccessListener {
                Log.d("로그아웃","2")
                val intentStart = Intent(this@SplashActivity, LoginStartActivity::class.java)
                intentStart.action = Intent.ACTION_MAIN
                intentStart.addCategory(Intent.CATEGORY_LAUNCHER)
                intentStart.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intentStart)
                finish()
                Toast.makeText(this@SplashActivity, "계정 로그인 필요", Toast.LENGTH_SHORT).show()
            }
                .addOnFailureListener { e ->
                    Log.d("로그아웃","3"+e.toString())
                    makeErrorAlter(e) }
        }.await()

    }

    private fun makeErrorAlter(e:Exception){
        AlertDialog.Builder(this)
            .setTitle("서버 오류입니다.")
            .setMessage(" 관리자에게 문의해주세요. 오류코드:$e")
            .setCancelable(false)
            .setPositiveButton("확인", object : DialogInterface.OnClickListener{
                override fun onClick(dialog: DialogInterface?, idx: Int) {
                    dialog!!.dismiss()
                }
            })
            .create()
            .show()
    }
}