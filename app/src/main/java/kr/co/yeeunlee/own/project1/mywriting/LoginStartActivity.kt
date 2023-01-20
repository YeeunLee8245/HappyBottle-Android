package kr.co.yeeunlee.own.project1.mywriting

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.co.yeeunlee.own.project1.mywriting.SplashActivity.Companion.gso
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
    private lateinit var connection: NetworkConnection
    private val db = SplashActivity.db
    private val mAuth = SplashActivity.mAuth

    // google용 Intent 결과
    private val getgoogleResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
        try {
            val account = task.getResult(ApiException::class.java)!!
            firebaseAuthWithGoogle(account.idToken!!)
        } catch (e:ApiException){}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginStartBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setScreen()

        connection = NetworkConnection(applicationContext)
        connection.observe(this){ isConnected ->
            if (isConnected){
            }else{
                makeAlterDialog()
            }
        }
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
            startActivity(signInIntent)
            finish()
        }
        binding.btnGoogleSign.setOnClickListener { signIn() }
        binding.textPrivateUrl.setOnClickListener { moveToPrivateUrl() }
    }

    override fun onDestroy() {
        super.onDestroy()
        connection.unregister()
    }

    private fun moveToPrivateUrl() {
        val i = Intent(Intent.ACTION_VIEW, Uri.parse("https://yeeunlee8245.github.io/etchappy_bank_privacy_policy/"));
        startActivity(i);
    }

    private fun logIn(email: String, password: String){
        if (email == ""){
            binding.editEmail.error = "이메일을 입력해주세요."
        }
        else{
            var userListener:ListenerRegistration? = null
            userListener = db.collection("user").document(email).addSnapshotListener { document, error ->
                if (document!!.exists() == true) {
                    if (password == ""){
                        binding.editPW.error = "비밀번호를 입력해주세요."
                    }else{
                        mAuth.signInWithEmailAndPassword(email, password)
                            .addOnSuccessListener {
                                userListener!!.remove()
                                CoroutineScope(Dispatchers.Main).launch {
                                    val fireRepo = FirebaseRepository(this@LoginStartActivity)
                                    val userData = fireRepo.getNameImgSnapshot()
                                    fireRepo.setToken(3)
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
                                    CoroutineScope(Dispatchers.Default).launch {
                                        val token:String = fireRepo.setToken(2)
                                        user = User(
                                            name, mAuth.currentUser!!.email!!, false,
                                            null, 0, 0, token, (0..7).random())
                                        db.collection("user").document(user.email)
                                            .set(user)
                                            .addOnCompleteListener {
                                                CoroutineScope(Dispatchers.Main).launch {
                                                    val userData = fireRepo.getNameImgSnapshot()
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
                    }else { // 기존 구글 계정 있을시
                        CoroutineScope(Dispatchers.Main).launch {
                            val fireRepo = FirebaseRepository(this@LoginStartActivity)
                            val userData = fireRepo.getNameImgSnapshot()
                            fireRepo.setToken(1)
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

    private fun makeAlterDialog() {
        AlertDialog.Builder(this)
            .setTitle("인터넷 연결을 확인할 수 없습니다...")
            .setCancelable(false)
            .setItems(arrayOf("재접속","종료"), object : DialogInterface.OnClickListener{
                override fun onClick(dialog: DialogInterface?, idx: Int) {
                    if (idx == 0){
                        dialog!!.dismiss()
                        if (connection.value == false){ // 미연결시 다시 연결
                            makeAlterDialog()
                        }
                    }else if (idx == 1){
                        finish()
                    }
                }
            })
            .create()
            .show()
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