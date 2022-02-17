package kr.co.yeeunlee.own.project1.mywriting

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.co.yeeunlee.own.project1.mywriting.databinding.FragmentSendBinding


class SendFragment : Fragment() {
    private lateinit var binding: FragmentSendBinding
    private var vaild = MutableLiveData<Boolean>()
    private val sendViewModel: SendViewModel by viewModels<SendViewModel>()
    // 전역 부분에 뷰모델에 접근하는 코드 넣으면 안됨(뷰모델은 프래그먼트가 detached된 상태에서 접근 불가)
    companion object{
        var deletePosition: Int? = null
    }
    init {
        vaild.value = false
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSendBinding.inflate(inflater, container, false)

        initBtnSend() // 도착한 쪽지 터치
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sendAdapter:SendAdapter = SendAdapter(sendViewModel.checkPost.value!!)

        vaild.observe(viewLifecycleOwner){
            checkVaild(it)
        }
        sendViewModel.myResponce.observe(viewLifecycleOwner){
            Log.d("알림을 띄웠습니다.","알림 띄움")
        }


        binding.recyclerSend.layoutManager = LinearLayoutManager(context)
        binding.recyclerSend.adapter = sendAdapter
        sendViewModel.checkPost.observe(viewLifecycleOwner){
            (binding.recyclerSend.adapter as SendAdapter).setData(it)
        }
        sendAdapter.setItemClickListener(object : SendAdapter.OnItemClickListener{
            override fun onItemClick(v: View, position: Int) {
                val checkPost = sendViewModel.checkPost
                deletePosition = position
                val dialog = SendReadDialogFragment(checkPost.value!![position]
                    , (checkPost.value!!.size - position), checkPost, this@SendFragment)
                activity?.supportFragmentManager?.let { fragmentManager ->
                    if (null == fragmentManager.findFragmentByTag("sendReadNote"))
                        dialog.show(fragmentManager, "sendReadNote")
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        sendViewModel.getPostSnapshot()
        vaild.value = false

    }


    private fun initBtnSend(){
        binding.btnSend.setOnClickListener {
            val firebaseRepo = FirebaseRepository(activity!!)
            var name:String? = null
            var dialog:SendDialogFragment? = null

            name = (activity as MainActivity).getUserName()
            dialog = SendDialogFragment(name.toString())
            vaild.value = false
            dialog!!.setSendBtnListener(object : SendDialogFragment.SendOnBtnClickListener{
                override fun SendOnBtnClicked(receiver: String, textEditNote: String, type: Int){
                    CoroutineScope(Dispatchers.Main).launch {
                        val profileImg: Int = (activity as MainActivity).getProfileImg()
                        firebaseRepo.setPostNoteAdd(receiver, textEditNote, type, profileImg, vaild)
                        // 푸시알림 전송
                        Log.d("알림 이름",receiver.toString())
                        val token = firebaseRepo.getToken(receiver.toString())
                        Log.d("알림 토큰",token.toString())
                        val data = NotificationBody.NotificationData(getString(R.string.app_name),
                            name.toString(), textEditNote, profileImg)
                        val body = NotificationBody(token, data)
                        sendViewModel.sendNotification(body)
                    }
                }
            })
            activity?.supportFragmentManager?.let { fragmentManager ->
                if (null == fragmentManager.findFragmentByTag("sendNote"))
                    dialog!!.show(fragmentManager, "sendNote")
            }

        }
    }

    private fun checkVaild(result:Boolean){
        Log.d("result",result.toString())
        if (result == false)
            return
        Toast.makeText(context, "전송 완료!", Toast.LENGTH_SHORT).show()
    }

    override fun onPause() {
        super.onPause()
        vaild.value = false
    }

}