package kr.co.yeeunlee.own.project1.mywriting

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.DialogFragment
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.co.yeeunlee.own.project1.mywriting.databinding.DialogFragmentReadBinding
import java.text.SimpleDateFormat
import java.util.*

class ReadDialogFragment(var currentSnapshot: DocumentSnapshot, val orderStart: Int)
    : DialogFragment() {
    private var _binding: DialogFragmentReadBinding? = null
    private val binding get() = _binding!!
    private val orderLast = orderStart + 4
    private var currentOrder:Int = currentSnapshot.id.toInt()
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

        binding.customLayout.changeBackground(currentSnapshot.get("type").toString().toInt())
        binding.disableEditNote.invalidate()

        dialog?.window!!.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog?.setCanceledOnTouchOutside(false)
        dialog?.setCancelable(true)
        dialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.show()

        if (currentOrder == orderLast) {
            binding.btnRight.isEnabled = false
            binding.btnRight.visibility = View.INVISIBLE
        }
        else if (currentOrder == orderStart){
            binding.btnLeft.isEnabled = false
            binding.btnLeft.visibility = View.INVISIBLE
        }

        transTime()
        binding.disableEditNote.setText(currentSnapshot.get("text").toString())
        binding.btnModify.setOnClickListener {
            if (binding.disableEditNote.isEnabled == true)
                completeText()
            else
                modifyText()
        }
        binding.btnCancle.setOnClickListener { dismiss() }

        binding.btnLeft.setOnClickListener { descendOrder() }
        binding.btnRight.setOnClickListener { ascendOrder() }

        return binding.root
    }

    private fun modifyText(){
        binding.btnModify.setBackgroundResource(R.drawable.btn_check)
        binding.disableEditNote.isEnabled = true
        binding.disableEditNote.requestFocus()
        //키보드 보이게 하는 부분
        val imm:InputMethodManager = this.context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.disableEditNote, 0)
        binding.disableEditNote.setSelection(0) // 포커스 맨 첫번째로
    }

    private fun completeText(){
        val fireRepo = FirebaseRepository()
        Log.d("수정","완료")
        // 키보드 들어가게 하는 부분
        val imm = this.context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.disableEditNote.windowToken, 0)
        binding.disableEditNote.clearFocus()

        binding.disableEditNote.isEnabled = false
        binding.btnModify.setBackgroundResource(R.drawable.btn_modify)

        //TODO("수정된 데이터 보내기")
        CoroutineScope(Dispatchers.Main).launch {
            fireRepo.setNoteModify(binding.disableEditNote.text.toString(), currentSnapshot!!.id)
        }

    }

    private fun transTime(){
        val time = currentSnapshot.get("time") as Timestamp
        val milliseconds = time.seconds * 1000 + time.nanoseconds / 1000000
        val sdf = SimpleDateFormat("yyyy년\nMM월 dd일 씀", Locale.KOREA)
        val netDate = Date(milliseconds)
        val date = sdf.format(netDate).toString()
        binding.textTime.setText(date)
        Log.d("수정 날짜", date)
    }

    private fun descendOrder(){
        val fireRepo = FirebaseRepository()
        currentOrder -= 1
        if (currentOrder == orderStart) {
            binding.btnLeft.isEnabled = false
            binding.btnLeft.visibility = View.INVISIBLE
        }
        else{
            binding.btnLeft.isEnabled = true
            binding.btnLeft.visibility = View.VISIBLE
            binding.btnRight.isEnabled = true
            binding.btnRight.visibility = View.VISIBLE
        }
        CoroutineScope(Dispatchers.Main).launch {
            val changeSnapshot:DocumentSnapshot = fireRepo.getOpnNoteSnapshot(currentOrder)
            currentSnapshot = changeSnapshot
            binding.customLayout.changeBackground(currentSnapshot.get("type").toString().toInt())
            binding.disableEditNote.invalidate()
            binding.disableEditNote.setText(currentSnapshot.get("text").toString())
            transTime()
        }

    }

    private fun ascendOrder(){
        val fireRepo = FirebaseRepository()
        currentOrder += 1
        if (currentOrder == orderLast) {
            binding.btnRight.isEnabled = false
            binding.btnRight.visibility = View.INVISIBLE
        }
        else{
            binding.btnRight.isEnabled = true
            binding.btnRight.visibility = View.VISIBLE
            binding.btnLeft.isEnabled = true
            binding.btnLeft.visibility = View.VISIBLE
        }
        CoroutineScope(Dispatchers.Main).launch {
            val changeSnapshot:DocumentSnapshot = fireRepo.getOpnNoteSnapshot(currentOrder)
            currentSnapshot = changeSnapshot
            binding.customLayout.changeBackground(currentSnapshot.get("type").toString().toInt())
            binding.disableEditNote.invalidate()
            binding.disableEditNote.setText(currentSnapshot.get("text").toString())
            transTime()
        }

    }

}