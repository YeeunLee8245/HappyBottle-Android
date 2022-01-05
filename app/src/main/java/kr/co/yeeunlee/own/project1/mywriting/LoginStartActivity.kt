package kr.co.yeeunlee.own.project1.mywriting

import android.R.attr.data
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import kr.co.yeeunlee.own.project1.mywriting.databinding.ActivityLoginStartBinding


private lateinit var binding:ActivityLoginStartBinding

class LoginStartActivity : AppCompatActivity() {
    // google용 Intent 결과, 무조건 전역으로 생성(아니면 에러)
    private val getResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        // 액티비티 반환 결과
        val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(it.data)
        handleSignInResult(task)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginStartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {  }
        binding.btnSign.setOnClickListener {  }
        binding.btnGoogleSign.setOnClickListener { signIn() }
    }

    override fun onStart() {    // 자동 로그인
        super.onStart()
        val account = GoogleSignIn.getLastSignedInAccount(this)
        account?.run {
            Toast.makeText(this@LoginStartActivity, "구글 계정 로그인 성공", Toast.LENGTH_SHORT).show()
            // 인텐트 추가}
            Toast.makeText(this@LoginStartActivity, "계정 로그인 필요", Toast.LENGTH_SHORT).show()
            //updateUI(account)
        }
    }

    private fun signIn(){
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        val mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        val googleSignIntent:Intent = mGoogleSignInClient.signInIntent
        getResult.launch(googleSignIntent)
    }
    private fun handleSignInResult(completedTask:Task<GoogleSignInAccount>){
        try {
            val account = completedTask.getResult(ApiException::class.java)
            //updateUI(account)
        } catch (e:ApiException){
            Log.e("구글 계정 정보","signInResult:failed code=" + e.getStatusCode())
            //updateUI(account)
        }
    }
}