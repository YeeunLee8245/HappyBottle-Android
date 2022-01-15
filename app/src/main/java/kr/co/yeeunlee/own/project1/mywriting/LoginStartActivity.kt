package kr.co.yeeunlee.own.project1.mywriting

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kr.co.yeeunlee.own.project1.mywriting.databinding.ActivityLoginStartBinding

class LoginStartActivity : AppCompatActivity() {
    companion object {
        val gso:GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("702537519034-81ob9l15coeebj4kgu1cakoub43555aa.apps.googleusercontent.com")
            .requestEmail()
            .build()
        val mAuth:FirebaseAuth = Firebase.auth
        val db = Firebase.firestore
    }
    private lateinit var binding:ActivityLoginStartBinding
    private lateinit var intentMain:Intent
    private lateinit var user: User

    // 회원가입 Intent 결과, 무조건 전역으로 생성(아니면 에러)
    private val getSignInResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        // 액티비티 반환 결과
        user = it.data?.getParcelableExtra("user")!!
        Log.d("인증메일 받음","$user")
        if(it.resultCode == Activity.RESULT_OK){
            //TODO("회원가입 성공, 계정 정보를 통해 앱 메인 접속")
            mAuth.createUserWithEmailAndPassword(user.email!!, user.password!!) // 이메일 계정 등록/로그인
                .addOnCompleteListener{ task ->
                    Log.d("사용자 이메일로 계정 등록1", "${mAuth.currentUser!!.email}")
                    user.password = null
                    db.collection("user").document(user!!.email!!)
                        .set(user!!)
                        .addOnCompleteListener {
                            db.collection("check").document("name")
                                .update("name",FieldValue.arrayUnion(user!!.name!!))
                                .addOnSuccessListener {
                                    Log.d("db성공",user!!.name.toString())
                                    startActivity(intentMain)
                                    finish()
                                }
                        }
                }
            //finish()
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
            val signInIntent = Intent(this, SignInActivity::class.java) // 회원가입
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
                val newUser = task.getResult().additionalUserInfo?.isNewUser
                if (true == newUser) {
                    Log.d("첫 계정", "${newUser}")
                    // 닉네임 생성 다이얼로그 띄우기, 계정 삭제 후 닉네임 생성 성공시에만 계정 등록
                    mAuth.currentUser!!.delete()
                    mAuth.signOut()
                    GoogleSignIn.getClient(this, gso).signOut()
                    val dialog = NameLayoutDialog(this)
                    dialog.showDialog()
                    dialog.setOnClickListener(object : NameLayoutDialog.OnDialogClickListener{
                        override fun onClicked(name: String) {
                            mAuth.signInWithCredential(credential) // 비동기 주의
                                .addOnSuccessListener {
                                    Log.d("name",name+"${mAuth.currentUser?.email}")
                                    user = User(name,mAuth.currentUser!!.email!!,false,null)
                                    db.collection("user").document(user.email!!)
                                        .set(user)
                                        .addOnCompleteListener {
                                            Log.d("db성공",user.name.toString())
                                            startActivity(intentMain)
                                            finish()
                                        }
                                        .addOnFailureListener { e -> Log.e("db실패","${e}") }
                                    db.collection("check").document("name")
                                        .update("name",FieldValue.arrayUnion(user.name!!))
                                }
                        }
                    })

                    // 계정 인증 삭제 & Google 로그아웃
//                    mAuth.currentUser?.delete()
//                    mAuth.signOut()
//                    GoogleSignIn.getClient(this, LoginStartActivity.gso).signOut()
//                    dialog.setOnClickListener()
                    return@addOnCompleteListener // 절대 지우면 안됨, if로 들어오면 밑에 if문들은 실행 X
                }
                else    // false, 기존 계정 데이터 데베에서 받아오기
                    Log.d("기존 계정","${newUser}")
                Log.d("task:","${task.exception}")
                Log.d("task:","${task.result}")
                Log.d("task:","${task.isSuccessful}")
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    //intentMain.putExtra("user",user)
                    startActivity(intentMain)
                    finish()    // 로그인 시작창은 스택에서 삭제
                } else {
                    Toast.makeText(this, "Sorry auth failed.", Toast.LENGTH_SHORT)
                        .show()
                    Log.e("구글 로그인 실패 계정 정보","${mAuth.currentUser}")
                }

            }
    }

    //private fun duplateName(name:String){}


}