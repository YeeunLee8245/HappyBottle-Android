package kr.co.yeeunlee.own.project1.mywriting

import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.co.yeeunlee.own.project1.mywriting.data.FirebaseRepository
import kr.co.yeeunlee.own.project1.mywriting.databinding.FragmentSendReadDialogBinding
import java.text.SimpleDateFormat
import java.util.*


class SendReadDialogFragment(val note: Note, val mId:Int, val checkPost: LiveData<ArrayList<Note>>
,val parent:SendFragment)
    : DialogFragment() {
    private var _binding: FragmentSendReadDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSendReadDialogBinding.inflate(inflater, container, false)

        binding.customLayout.changeBackground(note.type)
        binding.disableEditNote.invalidate()

        dialog?.window!!.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog?.setCanceledOnTouchOutside(false)
        dialog?.setCancelable(true)
        dialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.show()

        val time = note.time
        val milliseconds = time.seconds * 1000 + time.nanoseconds / 1000000
        val sdf = SimpleDateFormat("yyyy년\nMM월 dd일", Locale.KOREA)
        val netDate = Date(milliseconds)
        val dateSender = sdf.format(netDate).toString()+'\n'+note.name+" 씀"
        binding.disableEditNote.setText(note.text)
        binding.textTime.setText(dateSender)
        binding.btnSave.setOnClickListener {
            AlertDialog.Builder(activity)
                .setTitle("내 저금통에 보관하겠습니까?")
                .setCancelable(false)
                .setItems(arrayOf("보관하기","취소"), object : DialogInterface.OnClickListener{
                    override fun onClick(dialog: DialogInterface?, idx: Int) {
                        dialog!!.dismiss()
                        if (idx == 0){
                            savePost()
                        }
                    }
                })
                .create()
                .show()
        }

        binding.btnDelete.setOnClickListener {
            AlertDialog.Builder(activity)
                .setTitle("삭제하시겠습니까?")
                .setCancelable(false)
                .setItems(arrayOf("삭제하기","취소"), object : DialogInterface.OnClickListener{
                    override fun onClick(dialog: DialogInterface?, idx: Int) {
                        dialog!!.dismiss()
                        if (idx == 0){
                            deletePost()
                        }
                    }
                })
                .create()
                .show()
        }

        return binding.root
    }

    private fun savePost(){
        val fireRepo = FirebaseRepository(activity!!)
        CoroutineScope(Dispatchers.Main).launch {
            fireRepo.setNoteAdd("",0,note)
            fireRepo.deletePostNote(note)
            dismiss()
        }
    }

    private fun deletePost(){
        val fireRepo = FirebaseRepository(activity!!)
        CoroutineScope(Dispatchers.Main).launch {
            fireRepo.deletePostNote(note)
            dismiss()
        }

    }
}