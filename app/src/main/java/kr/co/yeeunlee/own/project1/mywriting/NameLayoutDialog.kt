package kr.co.yeeunlee.own.project1.mywriting

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.firebase.firestore.ListenerRegistration
import kr.co.yeeunlee.own.project1.mywriting.databinding.DialogNameLayoutBinding
import java.util.regex.Pattern

class NameLayoutDialog() : DialogFragment() {
    private var _binding: DialogNameLayoutBinding? = null
    private val binding get() = _binding!!
    private lateinit var onClickListener: OnDialogClickListener
    private var nameListenerLi:ArrayList<ListenerRegistration> = arrayListOf<ListenerRegistration>()
    private val db = SplashActivity.db
    private var limitName:Boolean = false
    private var limitText:Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DialogNameLayoutBinding.inflate(inflater,container,false)

        val editName = binding.editName
        val btnNameDuplicate = binding.btnNameDuplicate
        val btnNameComplete = binding.btnNameComplete
        lateinit var name: String

        editName.addTextChangedListener(listnerEditName)

        btnNameDuplicate.setOnClickListener {
            // 키보드 내리고 포커스 맞추기
            val imm =
                activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(editName.windowToken, 0)
            editName.clearFocus()
//            if (limitName == false){
//                editName.error = "특수문자, 공백 제외 7 글자 이하"
//                return@setOnClickListener
//            }

            var nameListener:ListenerRegistration? = null
            nameListener = db.collection("check").document("name").addSnapshotListener { document, error ->
                nameListenerLi.add(nameListener!!)
                val li = document!!.get("name") as List<String>
                limitName = false
                name = editName.text.toString()
                Log.d("별명", name)

                if ((li.contains(name.toString()) == false) and (name != "") and (limitText == true)) {
                    limitName = true
                    Toast.makeText(context, "가능한 별명입니다.", Toast.LENGTH_SHORT).show()
                } else if (name == "")
                    editName.error="별명을 입력해주세요"
                else if (limitText == false)
                    editName.error="특수문자, 공백 제외 7 글자 이하"
                else // valid == false
                    editName.error="이미 존재하는 별명입니다."
                Log.d("별명 valid", "$name $limitName")
                if (error != null){
                    AlertDialog.Builder(activity)
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

        btnNameComplete.setOnClickListener {
            // 닉네임 중복 확인, 중복 아닐 때만 onClick 과 dismiss 수행
            if (limitName == true) {
                nameListenerLi.forEach { it -> it?.remove() }  // null이 아닐 때만 수행
                onClickListener.onClicked(editName.text.toString())
                dialog?.dismiss()
            } else {
                editName.error = "별명 중복확인을 해주세요."
            }
        }
        dialog?.window!!.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog?.setCanceledOnTouchOutside(false)
        dialog?.setCancelable(true)
        dialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.show()

        return binding.root
    }

    override fun onDestroy() {
        nameListenerLi.forEach { it -> it?.remove() }  // null이 아닐 때만 수행
        super.onDestroy()
    }

    fun setOnClickListener(listener: OnDialogClickListener) {
        onClickListener = listener
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
            val editName = binding.editName
            if (s != null){
                if (pattern.matcher(s.toString()).matches()) {   // 한글, 영어, 숫자만 있을 때
                    limitText = true
                    editName.error = null
                }
                else{
                    limitText = false
                    editName.error = "특수문자, 공백 제외 7 글자 이하"
                }
            }
        }

        override fun afterTextChanged(s: Editable?) {
            if (limitName == true)  // 다시 타자치면 리셋
                limitName = false
        }
    }
}