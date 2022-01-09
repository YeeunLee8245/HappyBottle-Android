package kr.co.yeeunlee.own.project1.mywriting

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kr.co.yeeunlee.own.project1.mywriting.databinding.ActivityLoginStartBinding

object Environment

class LoginStartActivity : AppCompatActivity() {
    companion object {
        val gso:GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("702537519034-81ob9l15coeebj4kgu1cakoub43555aa.apps.googleusercontent.com")
            .requestEmail()
            .build()
        val mAuth:FirebaseAuth = Firebase.auth
    }
    private lateinit var binding:ActivityLoginStartBinding
    private lateinit var intentMain:Intent

    // 회원가입 Intent 결과, 무조건 전역으로 생성(아니면 에러)
    private val getSignInResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        // 액티비티 반환 결과
        if(it.resultCode == Activity.RESULT_OK){
            TODO("회원가입 성공, 계정 정보를 통해 앱 메인 접속")
            finish()
        }
    }
    // google용 Intent 결과
    private val getgoogleResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
        try {
            val account = task.getResult(ApiException::class.java)!!
            Log.e("구글 계정 정보1","${account.email}")
            firebaseAuthWithGoogle(account.idToken!!)
        } catch (e:ApiException){
            Log.e("구글 로그인 실패","signInResult:failed code=" + e.getStatusCode())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginStartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intentMain = Intent(this,MainActivity::class.java)

        binding.btnLogin.setOnClickListener {

        }
        binding.btnSign.setOnClickListener {
            val signInIntent = Intent(this, SignInActivity::class.java)
            getSignInResult.launch(signInIntent)
        }
        binding.btnGoogleSign.setOnClickListener { signIn() }
    }

    override fun onStart() {    // 자동 로그인
        super.onStart()
        //val account = GoogleSignIn.getLastSignedInAccount(this)
        val account = mAuth.currentUser

        Log.e("기존 계정정보","${mAuth.currentUser?.email}")
        if (account != null) {
            Toast.makeText(this@LoginStartActivity, "구글 계정 로그인 성공${account.email}", Toast.LENGTH_SHORT).show()
            startActivity(intentMain)
            finish()    // 로그인 시작창은 스택에서 삭제
        }else {Toast.makeText(this@LoginStartActivity, "계정 로그인 필요", Toast.LENGTH_SHORT).show()}
        //updateUI(account)
    }

    private fun signIn(){
        val mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        val googleSignIntent:Intent = mGoogleSignInClient.signInIntent
        getgoogleResult.launch(googleSignIntent)
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) {task ->
                Log.d("task:","${task.exception}")
                Log.d("task:","${task.result}")
                Log.d("task:","${task.isSuccessful}")
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user: FirebaseUser? = mAuth.currentUser
                    intentMain.putExtra("user",user)
                    startActivity(intentMain)
                    finish()    // 로그인 시작창은 스택에서 삭제
                } else {
                    Toast.makeText(this, "Sorry auth failed.", Toast.LENGTH_SHORT)
                        .show()
                    Log.e("구글 로그인 실패 계정 정보","${mAuth.currentUser}")
                }

            }
    }
}