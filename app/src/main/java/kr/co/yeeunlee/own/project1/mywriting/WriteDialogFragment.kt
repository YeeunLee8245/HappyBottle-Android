package kr.co.yeeunlee.own.project1.mywriting

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
        dialog?.setCancelable(true)
        dialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.show()

        binding.btnPush.setOnClickListener {
            getEditNote()
            dismiss()
        }
        binding.btnCancle.setOnClickListener { dismiss() }

        return binding.root
    }

    private fun getEditNote(){
        val text = binding.editNote.text.toString()
        // 키보드 내리고 포커스 맞추기
        val imm =
            this.context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.editNote.windowToken, 0)
        binding.editNote.clearFocus()
        if (text == ""){
            Toast.makeText(this.context,"내용을 입력해주세요.", Toast.LENGTH_SHORT).show()
        }else{onButtonClickListener.onButtonClicked(text)}
    }

    interface OnButtonClickListener
    {
        fun onButtonClicked(textEditNote: String)
    }

    fun setButtonClickListener(buttonClickListener: OnButtonClickListener){ // 클릭 이벤트 객체 설정
        this.onButtonClickListener = buttonClickListener
    }
}