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
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.co.yeeunlee.own.project1.mywriting.databinding.ActivityLoginStartBinding
import org.threeten.bp.LocalDateTime

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
    private val settings = firestoreSettings {
        isPersistenceEnabled = true
        setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED).build()
    }
    private val fireRepo = FirebaseRepository()

    // 회원가입 Intent 결과, 무조건 전역으로 생성(아니면 에러)
    private val getSignInResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        // 액티비티 반환 결과
        if(it.resultCode == Activity.RESULT_OK){
            user = it.data?.getParcelableExtra("user")!!
            Log.d("인증메일 받음","$user")
            //TODO("회원가입 성공, 계정 정보를 통해 앱 메인 접속")
            mAuth.createUserWithEmailAndPassword(user.email, user.password!!) // 이메일 계정 등록/로그인
                .addOnCompleteListener{ task ->
                    Log.d("사용자 이메일로 계정 등록1", "${mAuth.currentUser!!.email}")
                    user.password = null
                    CoroutineScope(Dispatchers.Main).launch { // 코루틴 플로우 뒤에 동작되어야 하는 건 반드시 스코프 괄호 안에 적자.
                        user.token = fireRepo.getToken()
                        db.collection("user").document(user.email)
                            .set(user)
                            .addOnCompleteListener {
                                db.collection("check").document("name")
                                    .update("name",FieldValue.arrayUnion(user.name))
                                    .addOnSuccessListener {
                                        Log.d("db성공",user.name.toString())
                                        startActivity(intentMain)
                                        finish()
                                    }
                            }
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

    init {
        db.firestoreSettings = settings // 캐시와 오프라인 지속성 설정
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginStartBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setScreen()

        intentMain = Intent(this,MainActivity::class.java)

        binding.btnLogin.setOnClickListener { logIn(binding.editEmail.text.toString(), binding.editPW.text.toString()) }
        binding.btnSign.setOnClickListener {
            val signInIntent = Intent(this, SignInActivity::class.java) // 회원가입
            getSignInResult.launch(signInIntent)
        }
        binding.btnGoogleSign.setOnClickListener { signIn() }
    }

    override fun onStart() {    // 자동 로그인
        super.onStart()

        val account = mAuth.currentUser

        Log.e("기존 계정정보","${mAuth.currentUser?.email}")
        if (account != null) {
            Toast.makeText(this@LoginStartActivity, "구글 계정 로그인 성공${account.email}", Toast.LENGTH_SHORT).show()
            CoroutineScope(Dispatchers.Default).launch {
                fireRepo.setToken()
                startActivity(intentMain)
                finish()    // 로그인 시작창은 스택에서 삭제
            }
        }else {Toast.makeText(this@LoginStartActivity, "계정 로그인 필요", Toast.LENGTH_SHORT).show()}
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
                                startActivity(intentMain)
                                finish()
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
                    val dialog = NameLayoutDialog(this)
                    dialog.showDialog()
                    dialog.setOnClickListener(object : NameLayoutDialog.OnDialogClickListener{
                        override fun onClicked(name: String) {
                            mAuth.signInWithCredential(credential) // 비동기 주의
                                .addOnSuccessListener {
                                    Log.d("name",name+"${mAuth.currentUser?.email}")
                                    CoroutineScope(Dispatchers.Default).launch {
                                        val token:String = fireRepo.setToken()
                                        user = User(
                                            name, mAuth.currentUser!!.email!!, false,
                                            null, 0, 0, token)
                                        db.collection("user").document(user.email)
                                            .set(user)
                                            .addOnCompleteListener {
                                                Log.d("db성공", user.name.toString())
                                                startActivity(intentMain)
                                                finish()
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