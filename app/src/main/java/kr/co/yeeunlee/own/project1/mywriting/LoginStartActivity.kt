package kr.co.yeeunlee.own.project1.mywriting

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.co.yeeunlee.own.project1.mywriting.databinding.ActivityLoginStartBinding

class LoginStartActivity : AppCompatActivity() {
    companion object {
        const val INFO_TAG = 1004
        const val PROFILE_IMG_TAG = "profileImg"
        const val NAME_TAG = "username"
    }
    private lateinit var binding:ActivityLoginStartBinding
    private lateinit var intentMain:Intent
    private lateinit var user: User
    private val gso = SplashActivity.gso
    private val db = SplashActivity.db
    private val mAuth = SplashActivity.mAuth

    // 회원가입 Intent 결과, 무조건 전역으로 생성(아니면 에러)
    private val getSignInResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        // 액티비티 반환 결과
        Log.d("인증메일 받음0","${it.toString()}")
        if(it.resultCode == Activity.RESULT_OK){
            user = it.data?.getParcelableExtra("user")!!
            Log.d("인증메일 받음","$user")
            //TODO("회원가입 성공, 계정 정보를 통해 앱 메인 접속")
            mAuth.createUserWithEmailAndPassword(user.email, user.password!!) // 이메일 계정 등록/로그인
                .addOnCompleteListener{ task ->
                    Log.d("사용자 이메일로 계정 등록1", "${mAuth.currentUser!!.email}")
                    user.password = null
                    CoroutineScope(Dispatchers.Main).launch { // 코루틴 플로우 뒤에 동작되어야 하는 건 반드시 스코프 괄호 안에 적자.
                        FirebaseMessaging.getInstance().token.addOnSuccessListener {token ->
                            Log.d("인증메일 받음2","$user")
                            user.token = token
                            db.collection("user").document(user.email)
                                .set(user)
                                .addOnCompleteListener {
                                    db.collection("check").document("name")
                                        .update("name",FieldValue.arrayUnion(user.name))
                                        .addOnSuccessListener {
                                            Log.d("db성공", user.name.toString())
                                            intentMain.putExtra("INFO_TAG", INFO_TAG)
                                            intentMain.putExtra(NAME_TAG, user.name)
                                            intentMain.putExtra(PROFILE_IMG_TAG, user.profileImg)
                                            startActivity(intentMain)   // 정보 액티비티 추가
                                            finish()

                                        }
                                }
                        }   // 실패했을 때 동작 넣어주기

                    }

                }
        }
    }
    // google용 Intent 결과
    private val getgoogleResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
        try {
            val account = task.getResult(ApiException::class.java)!!
            Log.e("구글 계정 정보1",account.email!!)
            firebaseAuthWithGoogle(account.idToken!!)
        } catch (e:ApiException){
            Log.e("구글 로그인 실패","signInResult:failed code=" + e.getStatusCode())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginStartBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setScreen()

        intentMain = Intent(this,MainActivity::class.java)
        intentMain.action = Intent.ACTION_MAIN
        intentMain.addCategory(Intent.CATEGORY_LAUNCHER)
        intentMain.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP



        binding.btnLogin.setOnClickListener { logIn(binding.editEmail.text.toString(), binding.editPW.text.toString()) }
        binding.btnSign.setOnClickListener {
            val signInIntent = Intent(this, SignInActivity::class.java) // 회원가입
            signInIntent.action = Intent.ACTION_MAIN
            signInIntent.addCategory(Intent.CATEGORY_LAUNCHER)
            signInIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            getSignInResult.launch(signInIntent)
        }
        binding.btnGoogleSign.setOnClickListener { signIn() }
    }

    private fun logIn(email: String, password: String){
        if (email == ""){
            binding.editEmail.error = "이메일을 입력해주세요."
        }
        else{
            db.collection("user").document(email).addSnapshotListener { document, error ->
                if (document!!.exists() == true) {
                    if (password == ""){
                        binding.editPW.error = "비밀번호를 입력해주세요."
                    }else{
                        mAuth.signInWithEmailAndPassword(email, password)
                            .addOnSuccessListener {
                                CoroutineScope(Dispatchers.Main).launch {
                                    val fireRepo = FirebaseRepository(this@LoginStartActivity)
                                    val userData = fireRepo.getNameImgSnapshot()
                                    Log.d("계정 오류잡기1", mAuth.currentUser?.email.toString()+"  "+email)
                                    Log.d("계정 오류잡기2", userData.toString())
                                    intentMain.putExtra(NAME_TAG, userData.get("name").toString())
                                    intentMain.putExtra(PROFILE_IMG_TAG, userData.get("profileImg").toString().toInt())
                                    startActivity(intentMain)
                                    finish()
                                }
                            }
                            .addOnFailureListener {
                                binding.editEmail.error = "잘못된 이메일 또는 비밀번호입니다."
                            }
                    }
                }else{
                    binding.editEmail.error = "잘못된 이메일 또는 비밀번호입니다."
                }
            }
        }
        if (password == ""){
            binding.editPW.error = "비밀번호를 입력해주세요."
        }
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
                    val dialog = NameLayoutDialog()
                    this.supportFragmentManager.let { fragmentManager ->
                        dialog.show(fragmentManager, "nameCreate")
                    }
                    dialog.setOnClickListener(object : NameLayoutDialog.OnDialogClickListener{
                        override fun onClicked(name: String) {
                            val fireRepo = FirebaseRepository(this@LoginStartActivity)
                            mAuth.signInWithCredential(credential) // 비동기 주의
                                .addOnSuccessListener {
                                    Log.d("name",name+"${mAuth.currentUser?.email}")
                                    CoroutineScope(Dispatchers.Default).launch {
                                        val token:String = fireRepo.setToken()
                                        user = User(
                                            name, mAuth.currentUser!!.email!!, false,
                                            null, 0, 0, token, (0..7).random())
                                        db.collection("user").document(user.email)
                                            .set(user)
                                            .addOnCompleteListener {
                                                CoroutineScope(Dispatchers.Main).launch {
                                                    Log.d("db성공", user.name.toString())
                                                    val userData = fireRepo.getNameImgSnapshot()
                                                    Log.d("계정 오류잡기2", userData.toString())
                                                    intentMain.putExtra(NAME_TAG, userData.get("name").toString())
                                                    intentMain.putExtra(PROFILE_IMG_TAG, userData.get("profileImg").toString().toInt())
                                                    intentMain.putExtra("INFO_TAG", INFO_TAG)
                                                    startActivity(intentMain)   // 정보 액티비티 추가
                                                    finish()
                                                }
                                            }
                                            .addOnFailureListener { e -> Log.e("db실패", "${e}") }
                                        db.collection("check").document("name")
                                            .update("name",FieldValue.arrayUnion(user.name))
                                    }
                                }
                        }
                    })
                    return@addOnCompleteListener // 절대 지우면 안됨, if로 들어오면 밑에 if문들은 실행 X
                }
                else{    // false, 기존 계정 데이터 데베에서 받아오기
                    Log.d("기존 계정","${newUser}")
                    Log.d("task:","${task.exception}")
                    Log.d("task:","${task.result}")
                    Log.d("task:","${task.isSuccessful}")
                    if (!task.isSuccessful) {
                        Toast.makeText(this, "Sorry auth failed.", Toast.LENGTH_SHORT)
                            .show()
                        Log.e("구글 로그인 실패 계정 정보","${mAuth.currentUser}")
                    }else {
                        CoroutineScope(Dispatchers.Main).launch {
                            val fireRepo = FirebaseRepository(this@LoginStartActivity)
                            val userData = fireRepo.getNameImgSnapshot()
                            Log.d("계정 오류잡기2", userData.toString())
                            intentMain.putExtra(NAME_TAG, userData.get("name").toString())
                            intentMain.putExtra(PROFILE_IMG_TAG, userData.get("profileImg").toString().toInt())
                            startActivity(intentMain)
                            finish()    // 로그인 시작창은 스택에서 삭제
                        }
                    }
                }
            }
    }

    private fun setScreen(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){ // Android 11
            window.setDecorFitsSystemWindows(false)
            val controller = window.insetsController
            if(controller != null){
                controller.hide(
                    WindowInsets.Type.statusBars() or
                            WindowInsets.Type.navigationBars())
                WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        }else{
            window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_IMMERSIVE
                            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_FULLSCREEN
                    )
        }
    }
}