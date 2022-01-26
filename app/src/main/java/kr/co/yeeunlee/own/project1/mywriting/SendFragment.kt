package kr.co.yeeunlee.own.project1.mywriting

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.co.yeeunlee.own.project1.mywriting.databinding.FragmentSendBinding


class SendFragment : Fragment() {
    private lateinit var binding: FragmentSendBinding
    private val firebaseRepo = FirebaseRepository()
    private val sendViewModel: SendViewModel by viewModels<SendViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBtnSend()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSendBinding.inflate(inflater, container, false)
        initBtnSend() // 도착한 쪽지 터치
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        sendViewModel.sendSnapshot.observe(viewLifecycleOwner){
//            initSend(it)
//        }
    }

    private fun initBtnSend(){
        binding.btnSend.setOnClickListener {
            val dialog = SendDialogFragment()
            val firebaseRepo = FirebaseRepository()
            dialog.setSendBtnListener(object : SendDialogFragment.SendOnBtnClickListener{
                override fun SendOnBtnClicked(receiver: String, textEditNote: String) {
                    CoroutineScope(Dispatchers.Default).launch {
                        if (true == firebaseRepo.setSendNoteAdd(receiver, textEditNote)) {
                            Toast.makeText(context, "전송 완료", Toast.LENGTH_SHORT).show()
                            //TODO("서비스 추가하기(상대방에게 푸시 알림 전송)")
                        }
                    }
                }
            })
            activity?.supportFragmentManager?.let { fragmentManager ->
                dialog.show(fragmentManager, "sendNote")
            }
        }
    }

    private fun initSend(snapshot: DocumentSnapshot){}

}