package kr.co.yeeunlee.own.project1.mywriting

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.co.yeeunlee.own.project1.mywriting.databinding.FragmentSendReadDialogBinding
import java.text.SimpleDateFormat
import java.util.*


class SendReadDialogFragment(val note: Note, val mId:Int, val checkPost: LiveData<ArrayList<Note>>
,val parent:SendFragment)
    : DialogFragment() {
    private var _binding: FragmentSendReadDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

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
            savePost()
        }

        binding.btnDelete.setOnClickListener { deletePost() }

        return binding.root
    }

    private fun savePost(){
        val fireRepo = FirebaseRepository()
        CoroutineScope(Dispatchers.Main).launch {
            fireRepo.setNoteAdd("",0,note)
            fireRepo.deletePostNote(note)
            dismiss()
        }
        Log.d("보내기 size, mId", checkPost.value!!.size.toString()+"  $mId")

    }

    private fun deletePost(){
        val fireRepo = FirebaseRepository()
        CoroutineScope(Dispatchers.Main).launch {
            fireRepo.deletePostNote(note)
            dismiss()
        }

    }
}