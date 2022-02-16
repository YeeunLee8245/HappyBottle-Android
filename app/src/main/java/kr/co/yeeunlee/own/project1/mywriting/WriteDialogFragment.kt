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
import kr.co.yeeunlee.own.project1.mywriting.databinding.DialogFragmentWriteBinding

class WriteDialogFragment: DialogFragment() {
    private var _binding: DialogFragmentWriteBinding? = null
    private val binding get() = _binding!!
    private lateinit var onButtonClickListener: OnButtonClickListener

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DialogFragmentWriteBinding.inflate(inflater,container,false)
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
            getEditNote()
        }
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

    private fun getEditNote(){
        val text = binding.editNote.text.toString()
        // 키보드 내리고 포커스 맞추기
        val imm =
            this.context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.editNote.windowToken, 0)
        binding.editNote.clearFocus()
        if (text == "\n\n\n\n\n"){
            Toast.makeText(this.context,"내용을 입력해주세요.", Toast.LENGTH_SHORT).show()
        }else{
            onButtonClickListener.onButtonClicked(text,CustomConstraintLayout.type)
            dismiss()
        }
    }

    interface OnButtonClickListener
    {
        fun onButtonClicked(textEditNote: String, type:Int)
    }

    fun setButtonClickListener(buttonClickListener: OnButtonClickListener){ // 클릭 이벤트 객체 설정
        this.onButtonClickListener = buttonClickListener
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
}