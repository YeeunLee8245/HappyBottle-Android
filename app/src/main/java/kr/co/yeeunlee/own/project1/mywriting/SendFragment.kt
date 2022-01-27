package kr.co.yeeunlee.own.project1.mywriting

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.co.yeeunlee.own.project1.mywriting.databinding.FragmentSendBinding


class SendFragment : Fragment() {
    private lateinit var binding: FragmentSendBinding
    private var vaild = MutableLiveData<Boolean>()
    private val firebaseRepo = FirebaseRepository()
    private val sendViewModel: SendViewModel by viewModels<SendViewModel>()

    init {
        vaild.value = false
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

        Log.d("Recreate","재배치")
        vaild.observe(viewLifecycleOwner){
            checkVaild(it)
        }
//        sendViewModel.sendSnapshot.observe(viewLifecycleOwner){
//            initSend(it)
//        }
        binding.recyclerSend.layoutManager = LinearLayoutManager(context)
        binding.recyclerSend.adapter = SendAdapter(sendViewModel.checkPost.value!!)
        sendViewModel.checkPost.observe(viewLifecycleOwner){
            (binding.recyclerSend.adapter as SendAdapter).setData(it)
        }
    }

    override fun onResume() {
        super.onResume()
        sendViewModel.getPostSnapshot()
        vaild.value = false

    }


    private fun initBtnSend(){
        binding.btnSend.setOnClickListener {
            val dialog = SendDialogFragment()
            val firebaseRepo = FirebaseRepository()
            vaild.value = false
            dialog.setSendBtnListener(object : SendDialogFragment.SendOnBtnClickListener{
                override fun SendOnBtnClicked(receiver: String, textEditNote: String){
                    CoroutineScope(Dispatchers.Main).launch {
                        firebaseRepo.setSendNoteAdd(receiver, textEditNote, vaild)
                    }
                }
            })
            activity?.supportFragmentManager?.let { fragmentManager ->
                dialog.show(fragmentManager, "sendNote")
            }
        }
    }

    private fun initSend(snapshot: DocumentSnapshot){}

    private fun checkVaild(result:Boolean){
        Log.d("result",result.toString())
        if (result == false)
            return
        else
            Toast.makeText(context, "전송 완료!", Toast.LENGTH_SHORT).show()
        //TODO("서비스 추가하기(상대방에게 푸시 알림 전송)")

    }

    override fun onPause() {
        super.onPause()
        vaild.value = false
    }

}