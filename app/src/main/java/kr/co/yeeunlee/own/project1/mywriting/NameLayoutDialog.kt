package kr.co.yeeunlee.own.project1.mywriting

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import java.util.regex.Pattern

class NameLayoutDialog(context: Context) {
    private val dialog = Dialog(context)
    private lateinit var onClickListener: OnDialogClickListener
    private val db = LoginStartActivity.db
    private val context = context
    private var limitName:Boolean = false

    init { // init의 중요성
        dialog.setContentView(R.layout.dialog_name_layout)
    }

    fun setOnClickListener(listener: OnDialogClickListener) {
        onClickListener = listener
    }

    fun showDialog() {
        dialog.window!!.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(true)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        val editName = dialog.findViewById<EditText>(R.id.editName)
        val btnNameDuplicate = dialog.findViewById<Button>(R.id.btnNameDuplicate)
        val btnNameComplete = dialog.findViewById<Button>(R.id.btnNameComplete)
        lateinit var name: String

        editName.addTextChangedListener(listnerEditName)

        btnNameDuplicate.setOnClickListener {
            // 키보드 내리고 포커스 맞추기
            val imm =
                this.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(editName.windowToken, 0)
            editName.clearFocus()
            if (limitName == false){
                editName.error = "특수문자, 공백 제외 7 글자 이하"
                return@setOnClickListener
            }
            db.collection("check").document("name").get()
                .addOnSuccessListener { document ->
                    val li = document.get("name") as List<String>
                    limitName = false
                    name = editName.text.toString()
                    Log.d("별명", name)

                    if ((li.contains(name.toString()) == false) and (name != "")) {
                        limitName = true
                        Toast.makeText(context, "가능한 별명입니다.", Toast.LENGTH_SHORT).show()
                    } else if (name == "")
                        editName.error="별명을 입력해주세요"
                    else // valid == false
                        editName.error="이미 존재하는 별명입니다."
                    Log.d("별명 valid", "$name $limitName")
                }
                .addOnFailureListener { Log.d("별명 등록 실패", "${it}") }
        }

        btnNameComplete.setOnClickListener {
            //Log.d("닉네임",editLayoutName.editText?.text.toString())
            // 닉네임 중복 확인, 중복 아닐 때만 onClick 과 dismiss 수행
            if (limitName == true) {
                onClickListener.onClicked(editName.text.toString())
                dialog.dismiss()
            } else {
                editName.error = "별명 중복확인을 해주세요."
            }
        }

        //val edit_name = dialog.findViewById<EditText>(R.id.name_edit)

    }

    interface OnDialogClickListener
    {
        fun onClicked(name: String)
    }

    private val listnerEditName = object : TextWatcher {
        val pattern = Pattern.compile("^[a-zA-Z0-9가-힣ㄱ-ㅎㅏ-ㅣ\\u318D\\u119E\\u11A2\\u2022\\u2025a\\u00B7\\uFE55]+$") // 공백포함 특수문자 체크

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
            val editName = dialog.findViewById<EditText>(R.id.editName)
            if (s != null){
                if (pattern.matcher(s.toString()).matches()) {   // 한글, 영어, 숫자만 있을 때
                    limitName = true
                    editName.error = null
                }
                else{
                    limitName = false
                    editName.error = "특수문자, 공백 제외 7 글자 이하"
                    Log.d("중복","들어옴")
                }
            }
        }

        override fun afterTextChanged(s: Editable?) {
        }
    }
}