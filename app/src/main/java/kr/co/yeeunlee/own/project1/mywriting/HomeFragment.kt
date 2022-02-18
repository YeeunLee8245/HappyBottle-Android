package kr.co.yeeunlee.own.project1.mywriting

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.co.yeeunlee.own.project1.mywriting.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {
    companion object{
        const val FILL_TAG = "fillBottleDialog"
    }
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by viewModels<HomeViewModel>()
    private val imgBottle = arrayListOf(R.drawable.bottle_0,R.drawable.bottle_1,R.drawable.bottle_2,
        R.drawable.bottle_3,R.drawable.bottle_4,R.drawable.bottle_5,R.drawable.bottle_6,
        R.drawable.bottle_7,R.drawable.bottle_8,R.drawable.bottle_9,R.drawable.bottle_10,
        R.drawable.bottle_11,R.drawable.bottle_12,R.drawable.bottle_13,R.drawable.bottle_14,
        R.drawable.bottle_15,R.drawable.bottle_16,R.drawable.bottle_17,R.drawable.bottle_18,
        R.drawable.bottle_19,R.drawable.bottle_20,R.drawable.bottle_21,R.drawable.bottle_22,
        R.drawable.bottle_23,R.drawable.bottle_24,R.drawable.bottle_25,R.drawable.bottle_26,
        R.drawable.bottle_27,R.drawable.bottle_28,R.drawable.bottle_29,R.drawable.bottle_30)
    private var userToken:String = "false"
    private var vaildModify:Boolean = false
    private var attachVaild:Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("하나액티비티값", activity.toString()+"  "+context.toString())
        attachVaild = true
        homeViewModel.getUserSnapshot()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater,container,false)
        initBtnWrite()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // LiveDate의 value가 it으로 들어감
        homeViewModel.userSnapshot.observe(viewLifecycleOwner){
            initUserView(it)
        }

        binding.btnModify.setOnClickListener {
            if (vaildModify == false)
                modifyStatus()
            else completeStatus()
        }
        binding.btnSetting.setOnClickListener {
            (activity as MainActivity?)!!.changeDrawer(userToken)

        }
        binding.btnInstruction.setOnClickListener { openInfoActivity() }
        binding.txtName.text = (activity as MainActivity).getUserName()
        binding.imgUser.setImageResource(MainActivity.profileImgLi[(activity as MainActivity).getProfileImg()])
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

    private fun initUserView(snapshot: DocumentSnapshot){
        val num = snapshot["numNote"].toString().toInt()
        var sumBottle = num%30  // 최신 보틀에 있는 쪽지 개수
        var strMemo = "$sumBottle/30"
        if (num == 0){
            strMemo = "0/30"
            sumBottle = 0
        }else{
            if (sumBottle == 0){
                strMemo = "30/30"
                sumBottle = 30
            }
        }

        binding.apply {
            userToken = snapshot["token"].toString()
            txtStatus.setText(snapshot["statusMsg"].toString())
            imgViewBottle.setImageResource(imgBottle[sumBottle])
            textBottle.text = strMemo
        }
    }

    private fun initBtnWrite(){ // 닉네임과 프로필 사진을 최초에 한 번만 초기화하면 안된다. 프래그먼트는 화면이 바뀔 때마다 다시 초기화되어 생성된다.
        binding.btnWrite.setOnClickListener {
            val dialog = WriteDialogFragment()
            val firebaseRepo: FirebaseRepository = FirebaseRepository(activity!!)
            //TODO("다이얼로그 프래그먼트 생성,onClickListener로 정보 넘기기")
            dialog.setButtonClickListener(object : WriteDialogFragment.OnButtonClickListener{
                override fun onButtonClicked(textEditNote: String, type:Int) {
                    CoroutineScope(Dispatchers.Main).launch {
                        // 보틀 업데이트
                        homeViewModel.setUserSnapshot(firebaseRepo.setNoteAdd(textEditNote, type))
                        val numNote = homeViewModel.userSnapshot.value!!.get("numNote").toString().toInt()
                        if ((numNote%30) == 0){
                            val fillDialog = OpinionDialogFragment(FILL_TAG)
                            activity!!.supportFragmentManager?.let { fragmentManager ->
                                if (null == fragmentManager.findFragmentByTag("fillBottle"))
                                    fillDialog.show(fragmentManager, "fillBottle")
                            }
                        }
                    }
                }
            })
            activity?.supportFragmentManager?.let { fragmentManager ->
                if (null == fragmentManager.findFragmentByTag("writeNote")) // 중복 생성 방지
                    dialog.show(fragmentManager, "writeNote")
            }
        }

    }

    private fun modifyStatus(){ // 수정 모드
        vaildModify = true
        binding.btnModify.setBackgroundResource(R.drawable.btn_check)
        binding.txtStatus.isEnabled = true
        binding.txtStatus.requestFocus()    // 키보드 처음 올릴 때 포커스 없으면 안 올라가니까 주의
        val imm = context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager   //키보드 올리기
        imm.showSoftInput(binding.txtStatus, 0)
        binding.txtStatus.setSelection(0)
    }

    private fun completeStatus(){ // 수정 완료
        val fireRepo = FirebaseRepository(activity!!)
        vaildModify = false
        binding.btnModify.setBackgroundResource(R.drawable.btn_modify)
        binding.txtStatus.isEnabled = false
        binding.txtStatus.clearFocus()
        val imm = context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager   //키보드 들어가기
        imm.hideSoftInputFromWindow(binding.txtStatus.windowToken, 0)
        fireRepo.setUserStatusMsg(binding.txtStatus.text.toString())    // 데베 업뎃
    }

    private fun openInfoActivity(){
        val infoIntent = Intent(activity, InfoViewpagerActivity::class.java)
        infoIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(infoIntent)
    }

    fun getListener():ListenerRegistration? {
        if (attachVaild == true)
            return homeViewModel.getListener()
        return null
    }
}