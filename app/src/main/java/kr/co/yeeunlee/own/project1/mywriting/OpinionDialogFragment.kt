package kr.co.yeeunlee.own.project1.mywriting

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import kr.co.yeeunlee.own.project1.mywriting.databinding.DialogNameLayoutBinding
import kr.co.yeeunlee.own.project1.mywriting.ui.HomeFragment


class OpinionDialogFragment(val typeDialog:String) : DialogFragment() {
    private var _binding: DialogNameLayoutBinding? = null
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

        binding.editName.width = dpZero
        binding.editName.height = dpZero
        binding.btnNameDuplicate.width = dpZero
        binding.btnNameDuplicate.height = dpZero
        binding.editName.isEnabled = false
        binding.editName.visibility = View.INVISIBLE
        binding.btnNameDuplicate.isEnabled = false
        binding.btnNameDuplicate.visibility = View.INVISIBLE

        if (typeDialog == MainActivity.OPINION_TAG){
            binding.textName.text = "앱 서비스 개선을 위한 의견을 입력해주세요.\n동기부여 메시지 또한 \n큰 힘이 됩니다."
            binding.btnNameComplete.text = "앱 의견 남기기"
            binding.btnNameComplete.setOnClickListener {
                val intentGStore: Intent = Intent(Intent.ACTION_VIEW)
                val packageName = "kr.co.yeeunlee.own.project1.mywriting"
                intentGStore.setData(Uri.parse("market://details?id=" + packageName))
                activity!!.startActivity(intentGStore)
            }
        }else if (typeDialog == HomeFragment.FILL_TAG){
            binding.textName.text = "저금통 하나가 꽉 찼어요!\n꽉 찬 저금통의 쪽지를 보관함에서 확인해보세요!"
            binding.btnNameComplete.text = "확인"
            binding.btnNameComplete.setOnClickListener {
                dismiss()
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

    interface OpinionOnBtnClickListener{
        fun OpinionOnBtnClicked(text: String)
    }

    fun setOpinionBtnListener(buttonClickListener: OpinionOnBtnClickListener){
        this.opinionOnBtnClickListener = buttonClickListener
    }

}