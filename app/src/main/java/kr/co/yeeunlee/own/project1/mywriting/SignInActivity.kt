package kr.co.yeeunlee.own.project1.mywriting

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.co.yeeunlee.own.project1.mywriting.databinding.ActivitySignInBinding
import kr.co.yeeunlee.own.project1.mywriting.ui.LoginStartActivity
import kr.co.yeeunlee.own.project1.mywriting.ui.SplashActivity
import kr.co.yeeunlee.own.project1.mywriting.utils.AlertUtil
import java.util.regex.Pattern

class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding
    private lateinit var connection: NetworkConnection
    private val mAuth = SplashActivity.mAuth
    private val db = SplashActivity.db
    private val imgIdx:Int = (0..7).random()
    private val imgLi = arrayListOf(R.drawable.blue, R.drawable.green, R.drawable.mint
        , R.drawable.orange, R.drawable.pink, R.drawable.purple, R.drawable.sky
        , R.drawable.yellow)
    private val nameListenerLi:ArrayList<ListenerRegistration> = ArrayList<ListenerRegistration>()
    private val emailListenerLi:ArrayList<ListenerRegistration> = ArrayList<ListenerRegistration>()
    private var map = hashMapOf<String,Boolean>("email" to false, "name" to false,
        "password" to false)
    private var limitName:Boolean = false
    private var limitText:Boolean = false



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        connection = NetworkConnection(applicationContext)
        connection.observe(this){ isConnected ->
            checkNetwork()
        }
        binding.imageProfil.setImageResource(imgLi[imgIdx])
        binding.btnNameDupli.setOnClickListener {
            var name = binding.editName.text
            duplicateName(name.toString())
        }
        binding.btnComplete.setOnClickListener {
            completeCheck(binding.editEmail.text.toString())
        }
        binding.editName.addTextChangedListener(listnerEditName)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intentLogin = Intent(this, LoginStartActivity::class.java)
        intentLogin.action = android.content.Intent.ACTION_MAIN
        intentLogin.addCategory(android.content.Intent.CATEGORY_LAUNCHER)
        intentLogin.flags = android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intentLogin)
        finish()
    }

    override fun onDestroy() {
        connection.unregister()
        emailListenerLi.forEach { it.remove() }
        nameListenerLi.forEach { it.remove() }
        super.onDestroy()
    }

    private fun duplicateName (name: String){
        // 키보드 내리고 포커스 없애기
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.editName.windowToken,0)
        binding.editName.clearFocus()

//        if (limitName == false){
//            binding.editName.error = "특수문자, 공백 제외 7 글자 이하"
//            return
//        }
        var nameListener:ListenerRegistration? = null
        nameListener = db.collection("check").document("name").addSnapshotListener { document, error ->
            nameListenerLi.add(nameListener!!)
            val li = document!!.get("name") as List<String>
            map["name"] = false
            if ((li.contains(name) == false) and (name != "") and (limitText == true)) {
                map["name"] = true
                binding.editName.error = null
                Toast.makeText(this,"가능한 별명입니다.", Toast.LENGTH_SHORT).show()
            }
            else if (name == "")
                binding.editName.error="별명을 입력해주세요"
            else if (limitText == false)
                binding.editName.error="특수문자, 공백 제외 7 글자 이하"
            else // valid == false
                binding.editName.error="이미 존재하는 별명입니다."
            if (error != null){
                AlertDialog.Builder(this)
                    .setTitle("서버 오류입니다.")
                    .setMessage(" 관리자에게 문의해주세요. 오류코드:$error")
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
    }

    private fun completeCheck(email:String){
        map["email"] = false
        // 이메일 중복 확인
        var pattern = android.util.Patterns.EMAIL_ADDRESS
        if (pattern.matcher(email).matches()) { // 정규 이메일 맞음
            binding.editEmail.error = null
            var emailListener:ListenerRegistration? = null
            emailListener = db.collection("user").document(email).addSnapshotListener { document, error ->
                // 중복
                emailListenerLi.add(emailListener!!)
                if (document!!.exists()) {
                    map["email"] = false
                    binding.editEmail.error = "이미 가입된 이메일 입니다."
                    completeElse()
                } else {
                    map["email"] = true
                    completeElse()
                }
            }
        }
        else {
            binding.editEmail.error = "올바른 이메일 형식을 입력해주세요."
            completeElse()
        }
    }

    private fun completeElse(){
        if (map["name"] == false) {
            binding.editName.error = "별명 중복확인을 해주세요."
        }
        completePassWord(
            binding.editPW.text.toString(),
            binding.editPWCheck.text.toString()
        )

        val result = map.filterValues { it == true }
        if (result.size == 3) { // 모두 true일 때 인증 가입 완료
            val inputName = binding.editName.text.toString()
            val inputEmail = binding.editEmail.text.toString()
            val inputPassword = binding.editPW.text.toString()
            val intentStart = Intent(this, LoginStartActivity::class.java)
            //val token: String = fireRepo.setToken()
            val user = User(
                inputName, inputEmail, true, inputPassword,
                0, 0,"", imgIdx)
            completeSignin(user)
        }
    }

    private fun completePassWord(password:String, checkPassword:String){
        map["password"] = false
        if (password == "")
            binding.editPW.error = "비밀번호를 입력해주세요."
        else if (password.count() < 6)
            binding.editPW.error = "여섯 글자 이상으로 입력해주세요."
        else{
            if (password.equals(checkPassword)) {
                map["password"] = true
                binding.editPW.error = null
                binding.editPWCheck.error = null
            }
            else
                binding.editPWCheck.error = "비밀번호를 다시 한번 정확히 입력해주세요."
        }

        if (checkPassword == "")
            binding.editPWCheck.error = "비밀번호 확인을 입력해주세요."

    }


    private val listnerEditName = object : TextWatcher{
        val pattern = Pattern.compile("^[a-zA-Z0-9가-힣ㄱ-ㅎㅏ-ㅣ\\u318D\\u119E\\u11A2\\u2022\\u2025a\\u00B7\\uFE55]+$") // 공백포함 특수문자 체크

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
            if (s != null){
                if (pattern.matcher(s.toString()).matches()) {   // 한글, 영어, 숫자만 있을 때
                    limitText = true
                    binding.editName.error = null
                }
                else{
                    limitText = false
                    binding.editName.error = "특수문자, 공백 제외 7 글자 이하"
                }
            }
        }

        override fun afterTextChanged(s: Editable?) {
            if ( map["name"] == true) {  // 다시 타자치면 리셋
                limitName = false
                map["name"] = false
            }
        }
    }

    private fun completeSignin(loginUser: User){
        emailListenerLi.forEach { it.remove() }
        nameListenerLi.forEach { it.remove() }
        emailListenerLi.clear()
        nameListenerLi.clear()
        //TODO("회원가입 성공, 계정 정보를 통해 앱 메인 접속")
        mAuth.createUserWithEmailAndPassword(loginUser.email, loginUser.password!!) // 이메일 계정 등록/로그인
            .addOnCompleteListener{ task ->
                loginUser.password = null
                CoroutineScope(Dispatchers.Main).launch { // 코루틴 플로우 뒤에 동작되어야 하는 건 반드시 스코프 괄호 안에 적자.
                    FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
                        loginUser.token = token
                        db.collection("user").document(loginUser.email)
                            .set(loginUser)
                            .addOnCompleteListener {
                                binding.editEmail.error = null
                                db.collection("check").document("name")
                                    .update("name", FieldValue.arrayUnion(loginUser.name))
                                    .addOnSuccessListener {
                                        binding.editName.error = null
                                        val intentMain = Intent(this@SignInActivity,MainActivity::class.java)
                                        intentMain.action = Intent.ACTION_MAIN
                                        intentMain.addCategory(Intent.CATEGORY_LAUNCHER)
                                        intentMain.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                                        intentMain.putExtra("INFO_TAG", LoginStartActivity.INFO_TAG)
                                        intentMain.putExtra(LoginStartActivity.NAME_TAG, loginUser.name)
                                        intentMain.putExtra(LoginStartActivity.PROFILE_IMG_TAG, loginUser.profileImg)
                                        startActivity(intentMain)   // 정보 액티비티 추가
                                        finish()

                                    }
                            }.addOnFailureListener {
                                AlertUtil.showPositiveDialog(
                                    this@SignInActivity,
                                    R.string.network_error_title,
                                    R.string.network_error_msg,
                                    R.string.confirm,
                                    null
                                )
//                                AlertDialog.Builder(this@SignInActivity)
//                                    .setTitle("서버 오류입니다.")
//                                    .setMessage(" 관리자에게 문의해주세요. 오류코드:$it")
//                                    .setCancelable(false)
//                                    .setPositiveButton("확인", object : DialogInterface.OnClickListener{
//                                        override fun onClick(dialog: DialogInterface?, idx: Int) {
//                                            dialog!!.dismiss()
//                                        }
//                                    })
//                                    .create()
//                                    .show()
                            }
                    }

                }

            }
    }

    private fun checkNetwork() {
        if (connection.value == false) {
            makeAlterDialog()
        }
    }

    private fun makeAlterDialog() {
        AlertUtil.showItemsDialog(
            this,
            R.string.internet_error_title,
            R.string.network_retry,
            R.string.network_finish,
            ::checkNetwork,
            null
        )
//        AlertDialog.Builder(this)
//            .setTitle("인터넷 연결을 확인할 수 없습니다...")
//            .setCancelable(false)
//            .setItems(arrayOf("재접속","종료"), object : DialogInterface.OnClickListener{
//                override fun onClick(dialog: DialogInterface?, idx: Int) {
//                    if (idx == 0){
//                        dialog!!.dismiss()
//                        if (connection.value == false){ // 미연결시 다시 연결
//                            makeAlterDialog()
//                        }
//                    }else if (idx == 1){
//                        finish()
//                    }
//                }
//            })
//            .create()
//            .show()
    }
}