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
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import kr.co.yeeunlee.own.project1.mywriting.databinding.DialogFragmentSendBinding

class SendDialogFragment(val userName: String): DialogFragment() {
    private var _binding: DialogFragmentSendBinding? = null
    private var vaild:Boolean = false
    private val binding get() = _binding!!
    private val db = SplashActivity.db
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
        dialog?.setCancelable(false)
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
                    sendOnBtnClickListener.SendOnBtnClicked(receiver, text, CustomConstraintLayout.type)
                    dismiss()
                }else Toast.makeText(context, "내용을 입력해주세요.", Toast.LENGTH_SHORT).show()
            }else Toast.makeText(context, "사용자 찾기를 완료해주세요.", Toast.LENGTH_SHORT).show()
        }
        binding.btnCheck.setOnClickListener {
            vaild = false
            dupliReceiver(binding.editReceiver.text.toString())}
        binding.btnCancle.setOnClickListener {
            AlertDialog.Builder(activity)
                .setTitle("작성 중인 쪽지가 소멸됩니다.")
                .setCancelable(false)
                .setItems(arrayOf("나가기","계속 쓰기"), object : DialogInterface.OnClickListener{
                    override fun onClick(dialog: DialogInterface?, idx: Int) {
                        dialog!!.dismiss()
                        if (idx == 0){
                            dismiss()
                        }
                    }
                })
                .create()
                .show()
        }

        return binding.root
    }

    private fun dupliReceiver(receiver: String){
        val editReceiver = binding.editReceiver
        db.collection("check").document("name").get()
            .addOnSuccessListener { document ->
                val li = document.get("name") as List<String>

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
            }
            .addOnFailureListener { e -> makeErrorAlter(e) }
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
            binding.textWordWrite.setText((binding.editNote.length()-5).toString()+"/200")
            var str = s.toString()
            if (str != "") {
                if (str.substring(str.length-1) == "\n") {
                    val newLine = str.filter { c -> c == '\n'}.count()
                    if (newLine < 5){
                        binding.editNote.setText(previousStr+'\n')
                        binding.textWordWrite.invalidate()
                    }
                }
            }
        }

        override fun afterTextChanged(s: Editable?) {
        }

    }

    private fun makeErrorAlter(e:Exception){
        AlertDialog.Builder(activity)
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
}