package kr.co.yeeunlee.own.project1.mywriting

import android.content.Context
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
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import kr.co.yeeunlee.own.project1.mywriting.databinding.DialogFragmentSendBinding

class SendDialogFragment(val userName: String): DialogFragment() {
    private var _binding: DialogFragmentSendBinding? = null
    private var vaild:Boolean = false
    private val binding get() = _binding!!
    private val db = LoginStartActivity.db
    private lateinit var sendOnBtnClickListener: SendOnBtnClickListener

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DialogFragmentSendBinding.inflate(inflater, container, false)
        dialog?.window!!.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog?.setCanceledOnTouchOutside(false)
        dialog?.setCancelable(true)
        dialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.show()

        binding.editNote.addTextChangedListener(listener1)

        binding.btnPush.setOnClickListener {
            val text = binding.editNote.text.toString()
            // 키보드 내리고 포커스 맞추기
            val imm =
                this.context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.editNote.windowToken, 0)
            binding.editNote.clearFocus()

            if(vaild == true){
                if(text != ""){
                    val receiver = binding.editReceiver.text.toString()
                    Log.d("타입",CustomConstraintLayout.type.toString())
                    sendOnBtnClickListener.SendOnBtnClicked(receiver, text, CustomConstraintLayout.type)
                    dismiss()
                }else Toast.makeText(context, "내용을 입력해주세요.", Toast.LENGTH_SHORT).show()
            }else Toast.makeText(context, "사용자 찾기를 완료해주세요.", Toast.LENGTH_SHORT).show()
        }
        binding.btnCheck.setOnClickListener {
            vaild = false
            dupliReceiver(binding.editReceiver.text.toString())}
        binding.btnCancle.setOnClickListener {dismiss()}

        return binding.root
    }

    private fun dupliReceiver(receiver: String){
        val editReceiver = binding.editReceiver
        db.collection("check").document("name").get()
            .addOnSuccessListener { document ->
                val li = document.get("name") as List<String>
                Log.d("별명", receiver)

                if ((receiver != "") and (li.contains(receiver) == false) ) {
                    editReceiver.error = "존재하지 않는 사용자입니다."
                } else if (receiver == "")
                    editReceiver.error = "별명을 입력해주세요."
                else if (receiver == userName)
                    editReceiver.error = "스스로에게 쪽지를 보낼 수는 없습니다."
                else { // valid == false
                    vaild = true
                    editReceiver.error = null
                    Toast.makeText(context, "사용자 확인 완료!", Toast.LENGTH_SHORT).show()
                    // 키보드 내리고 포커스 맞추기
                    val imm =
                        this.context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(binding.editNote.windowToken, 0)
                    binding.editNote.clearFocus()
                }
                Log.d("별명 valid", "$receiver $vaild")
            }
            .addOnFailureListener { Log.d("별명 찾기 실패", "${it}") }
    }

    interface SendOnBtnClickListener
    {
        fun SendOnBtnClicked(receiver: String, textEditNote: String, type: Int)
    }

    fun setSendBtnListener(buttonClickListener: SendOnBtnClickListener){
        this.sendOnBtnClickListener = buttonClickListener
    }

    val listener1 = object : TextWatcher {
        var previousStr = ""
        override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
            previousStr = s.toString()
        }

        override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
            Log.d("초기화 글자 개수", s.toString().length.toString())
            binding.textWordWrite.setText((binding.editNote.length()-5).toString()+"/100")
            Log.d("초기화 문자",s.toString())
            var str = s.toString()
            if (str != "") {
                if (str.substring(str.length-1) == "\n") {
                    Log.d("초기화 문자2", "true")
                    val newLine = str.filter { c -> c == '\n'}.count()
                    if (newLine < 5){
                        binding.editNote.setText(previousStr+'\n')
                        binding.textWordWrite.invalidate()
                    }
                }
            }
        }

        override fun afterTextChanged(s: Editable?) {
            Log.d("초기화 개행 개수", binding.editNote.lineCount.toString())

        }

    }
}