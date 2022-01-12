package kr.co.yeeunlee.own.project1.mywriting

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import kr.co.yeeunlee.own.project1.mywriting.databinding.ActivitySignInBinding
import java.util.regex.Pattern

class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding
    private val db = LoginStartActivity.db
    private var map = hashMapOf<String,Boolean>("email" to false, "name" to false,
        "password" to false)
    private var limitName:Boolean = false
    private lateinit var user:User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.btnNameDupli.setOnClickListener {
            var name = binding.editLayoutName.editText?.text
            duplicateName(name.toString())
        }
        binding.btnComplete.setOnClickListener {
            completeEmail(binding.editLayoutEmail.editText?.text.toString())




        }
        binding.editLayoutName.editText?.addTextChangedListener(listnerEditName)
    }

    private fun duplicateName (name: String){
        // 키보드 내리고 포커스 없애기
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.editLayoutName.editText?.windowToken,0)
        binding.editLayoutName.editText?.clearFocus()

        if (limitName == false){
            binding.editLayoutName.error = "특수문자, 공백 제외 7 글자 이하"
            return
        }
        db.collection("check").document("name").get()
            .addOnSuccessListener { document ->
                val li = document.get("name") as List<String>
                map["name"] = false
                Log.d("별명",name.toString())
                if ((li.contains(name) == false) and (name != "") ) {
                    map["name"] = true
                    binding.editLayoutName.error = null
                    Toast.makeText(this,"가능한 별명입니다.", Toast.LENGTH_SHORT).show()
                }
                else if (name == "")
                    binding.editLayoutName.error="별명을 입력해주세요"
                else // valid == false
                    binding.editLayoutName.error="이미 존재하는 별명입니다."
                Log.d("별명 valid","$name ${map["name"]}")
            }
            .addOnFailureListener { Log.d("별명 등록 실패","${it}") }
    }

    private fun completeEmail(email:String):Boolean {
        // 이메일 중복 확인
        var pattern = android.util.Patterns.EMAIL_ADDRESS
        if (pattern.matcher(email).matches()) { // 정규 이메일 맞음
            binding.editLayoutEmail.error = null
            db.collection("user").document(email).addSnapshotListener { document, error ->
                // 중복
                Log.d("이메일 리스너", "${document!!.exists()}")
                Log.d("이메일 리스너", "${error}")
                if (document.exists()) {
                    map["email"] = false
                    binding.editLayoutEmail.error = "이미 가입된 이메일 입니다."
                } else {
                    Log.d("이메일 리스너", "${map}")
                    map["email"] = true
                    Log.d("이메일 리스너", "${map}")
                    if (map["name"] == false) {
                        binding.editLayoutName.error = "별명 중복확인을 해주세요."
                    }
                    completePassWord(
                        binding.editLayoutPassWord.editText?.text.toString(),
                        binding.editLayoutPassWordCheck.editText?.text.toString()
                    )

                    Log.d("인증메일전송", "$map")
                    val result = map.filterValues { it == true }
                    Log.d("인증메일전송0", "${result.size}")
                    if (result.size == 3) {
                        // 모두 true일 때 인증 이메일 전송
                        Log.d("인증메일전송1", "$map")
                        // start로 액티비티 전환 후 finish
                        val inputName = binding.editLayoutName.editText?.text.toString()
                        val inputEmail = binding.editLayoutEmail.editText?.text.toString()
                        val inputPassword = binding.editLayoutPassWord.editText?.text.toString()
                        val intentStart = Intent(this, LoginStartActivity::class.java)
                        user = User(inputName, inputEmail, true, inputPassword)
                        Log.d("인증메일전송2", "$user")
                        intentStart.putExtra("user", user)

                        setResult(RESULT_OK, intentStart)
                        finish()
                    }
                }
            }
        }
        else
            binding.editLayoutEmail.error = "올바른 이메일 형식을 입력해주세요."

        return true
    }

    private fun completePassWord(password:String, checkPassword:String){
        map["password"] = false
        if (password == "")
            binding.editLayoutPassWord.error = "비밀번호를 입력해주세요."

        if (checkPassword == "")
            binding.editLayoutPassWordCheck.error = "비밀번호 확인을 입력해주세요."
        else if (password.equals(checkPassword)) {
            map["password"] = true
            binding.editLayoutPassWord.error = null
            binding.editLayoutPassWordCheck.error = null
        }
        else
            binding.editLayoutPassWordCheck.error = "비밀번호를 다시 한번 정확히 입력해주세요."
    }


    private val listnerEditName = object : TextWatcher{
        val pattern = Pattern.compile("^[a-zA-Z0-9가-힣ㄱ-ㅎㅏ-ㅣ\\u318D\\u119E\\u11A2\\u2022\\u2025a\\u00B7\\uFE55]+$") // 공백포함 특수문자 체크

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
            if (s != null){
                if (pattern.matcher(s.toString()).matches()) {   // 한글, 영어, 숫자만 있을 때
                    limitName = true
                    binding.editLayoutName.error = null
                }
                else{
                    limitName = false
                    binding.editLayoutName.error = "특수문자, 공백 제외 7 글자 이하"
                }
            }
        }

        override fun afterTextChanged(s: Editable?) {
        }
    }
}