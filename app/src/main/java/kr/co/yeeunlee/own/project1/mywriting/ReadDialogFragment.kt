package kr.co.yeeunlee.own.project1.mywriting

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.google.firebase.firestore.DocumentSnapshot
import kr.co.yeeunlee.own.project1.mywriting.databinding.DialogFragmentReadBinding

class ReadDialogFragment(var snapshot: DocumentSnapshot): DialogFragment() {
    private var _binding: DialogFragmentReadBinding? = null
    private val binding get() = _binding!!
    //private lateinit var readOnBtnClickListener:

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DialogFragmentReadBinding.inflate(inflater, container, false)
        binding.customLayout.changeBackground(snapshot.get("type").toString().toInt())
        binding.disableEditNote.invalidate()

        dialog?.window!!.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog?.setCanceledOnTouchOutside(false)
        dialog?.setCancelable(true)
        dialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.show()

        binding.disableEditNote.setText(snapshot.get("text").toString())
        binding.btnCancle.setOnClickListener { dismiss() }

        return binding.root
    }
}