package kr.co.yeeunlee.own.project1.mywriting

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.text.InputFilter
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.Dimension
import androidx.fragment.app.DialogFragment
import kr.co.yeeunlee.own.project1.mywriting.databinding.DialogNameLayoutBinding


class OpinionDialogFragment : DialogFragment() {
    private var _binding: DialogNameLayoutBinding? = null
    private val maxLength = 100
    private val binding get() = _binding!!
    private lateinit var opinionOnBtnClickListener:OpinionOnBtnClickListener

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dpZero:Int = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP
            ,0F,resources.displayMetrics).toInt()
        _binding = DialogNameLayoutBinding.inflate(inflater,container,false)
        binding.textName.setTextSize(TypedValue.COMPLEX_UNIT_DIP,40F)
        binding.textName.text = "앱 서비스 개선을 위한 의견을 입력해주세요.\n동기부여 메시지 또한 \n큰 힘이 됩니다."
        binding.editName.width = dpZero
        binding.editName.height = dpZero
        binding.btnNameDuplicate.width = dpZero
        binding.btnNameDuplicate.height = dpZero
        binding.editName.isEnabled = false
        binding.editName.visibility = View.INVISIBLE
        binding.btnNameDuplicate.isEnabled = false
        binding.btnNameDuplicate.visibility = View.INVISIBLE
        binding.btnNameComplete.text = "앱 의견 남기기"

        dialog?.window!!.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog?.setCanceledOnTouchOutside(false)
        dialog?.setCancelable(true)
        dialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.show()

        binding.btnNameComplete.setOnClickListener {
            val intentGStore: Intent = Intent(Intent.ACTION_VIEW)
            val packageName = "kr.co.yeeunlee.own.project1.mywriting"
            intentGStore.setData(Uri.parse("market://details?id=" + packageName))
            activity!!.startActivity(intentGStore)
        }

        return binding.root
    }

    interface OpinionOnBtnClickListener{
        fun OpinionOnBtnClicked(text: String)
    }

    fun setOpinionBtnListener(buttonClickListener: OpinionOnBtnClickListener){
        this.opinionOnBtnClickListener = buttonClickListener
    }

}