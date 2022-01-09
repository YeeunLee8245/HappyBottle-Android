package kr.co.yeeunlee.own.project1.mywriting

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import kr.co.yeeunlee.own.project1.mywriting.databinding.ActivitySignInBinding

class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnNameDuplication.setOnClickListener {
            var name = binding.editLayoutName.editText?.text
            // 키보드 내리고 포커스 없애기
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.editLayoutName.editText?.windowToken,0)
            binding.editLayoutName.editText?.clearFocus()
        }
        binding.btnComplete.setOnClickListener {

        }
    }

    private var listnerEditName = object : TextWatcher{
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            binding.editLayoutName.helperText = "한글 또는 영어로만 입력해주세요."
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            TODO("Not yet implemented")
        }

        override fun afterTextChanged(s: Editable?) {
            if (s != null){
                if (s.length > 10){
                    binding.editLayoutName.error = "7글자 이하로 입력해주세요."
                } else{
                    binding.editLayoutName.error = null
                }
            }
        }

    }
}