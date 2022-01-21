package kr.co.yeeunlee.own.project1.mywriting

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.google.firebase.firestore.DocumentSnapshot
import kr.co.yeeunlee.own.project1.mywriting.databinding.FragmentSendBinding


class SendFragment : Fragment() {
    private lateinit var binding: FragmentSendBinding
    private val sendViewModel: SendViewModel by viewModels<SendViewModel>()

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

        sendViewModel.sendSnapshot.observe(viewLifecycleOwner){
            initSend(it)
        }
    }

    private fun initBtnSend(){}

    private fun initSend(snapshot: DocumentSnapshot){}

}