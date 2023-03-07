package kr.co.yeeunlee.own.project1.mywriting.ui.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import kr.co.yeeunlee.own.project1.mywriting.databinding.FragmentNicknameDialogBinding
import kr.co.yeeunlee.own.project1.mywriting.ui.FirebaseViewModel
import kr.co.yeeunlee.own.project1.mywriting.ui.LoginStartActivity
import kr.co.yeeunlee.own.project1.mywriting.utils.states.ActivityState
import kr.co.yeeunlee.own.project1.mywriting.utils.states.ResultState

class NickNameDialogFragment() : DialogFragment() {
    private var _binding: FragmentNicknameDialogBinding? = null
    private val binding get() = _binding!!
    private val viewmodel: FirebaseViewModel by viewModels({requireActivity()})
    private val activity: LoginStartActivity by lazy { requireActivity() as LoginStartActivity }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNicknameDialogBinding.inflate(inflater, container, false)
        setScreen()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            completeButton.setOnClickListener { checkAvailable(nameEditText.text.toString()) }
            screenProgressBar.isVisible = false
        }
        observeAvailableNickName()
    }

    private fun observeAvailableNickName() {
        viewmodel.availableNickNameStatus.observe(this) {
            when (it) {
                is ResultState.Error -> {
                    activity.loadErrorMessage(it.exception)
                    isProgressButton(false)
                }
                is ResultState.Failed -> {
                    activity.loadErrorMessage(Throwable(getString(it.message)))
                    isProgressButton(false)
                }
                ResultState.Loading -> {
                    isProgressButton(true)
                }
                is ResultState.Success -> {
                    activity.moveToScreen(ActivityState.Main)
                    isProgressButton(false)
                }
            }
        }
    }

    private fun setScreen() {
        dialog?.apply {
            setCanceledOnTouchOutside(false)
            setCancelable(true)
            window?.apply {
                setLayout(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT
                )
                setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }
        }?.show()
    }

    private fun checkAvailable(nickname: String) {
        viewmodel.isAvailableNickName(nickname)
    }

    private fun closeKeyboard() {

    }

    private fun isProgressButton(valid: Boolean) {
        if (valid) {
            binding.screenProgressBar.isVisible = true
            binding.completeButton.isEnabled = false
        } else {
            binding.screenProgressBar.isVisible = false
            binding.completeButton.isEnabled = true
        }
    }


//    private lateinit var onClickListener: OnDialogClickListener
//    private var nameListenerLi:ArrayList<ListenerRegistration> = arrayListOf<ListenerRegistration>()
//    private val db = SplashActivity.db
//    private var limitName:Boolean = false
//    private var limitText:Boolean = false

//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        _binding = DialogNameLayoutBinding.inflate(inflater,container,false)

//        val editName = binding.editName
//        val btnNameDuplicate = binding.btnNameDuplicate
//        val btnNameComplete = binding.btnNameComplete
//        lateinit var name: String
//
//        editName.addTextChangedListener(listnerEditName)
//
//        btnNameDuplicate.setOnClickListener {
//            // 키보드 내리고 포커스 맞추기
//            val imm =
//                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//            imm.hideSoftInputFromWindow(editName.windowToken, 0)
//            editName.clearFocus()
////            if (limitName == false){
////                editName.error = "특수문자, 공백 제외 7 글자 이하"
////                return@setOnClickListener
////            }
//
//            var nameListener:ListenerRegistration? = null
//            nameListener = db.collection("check").document("name").addSnapshotListener { document, error ->
//                nameListenerLi.add(nameListener!!)
//                val li = document!!.get("name") as List<String>
//                limitName = false
//                name = editName.text.toString()
//                Log.d("별명", name)
//
//                if ((li.contains(name.toString()) == false) and (name != "") and (limitText == true)) {
//                    limitName = true
//                    Toast.makeText(context, "가능한 별명입니다.", Toast.LENGTH_SHORT).show()
//                } else if (name == "")
//                    editName.error="별명을 입력해주세요"
//                else if (limitText == false)
//                    editName.error="특수문자, 공백 제외 7 글자 이하"
//                else // valid == false
//                    editName.error="이미 존재하는 별명입니다."
//                Log.d("별명 valid", "$name $limitName")
//                if (error != null){
//                    AlertDialog.Builder(activity)
//                        .setTitle("서버 오류입니다.")
//                        .setMessage(" 관리자에게 문의해주세요. 오류코드:$error")
//                        .setCancelable(false)
//                        .setPositiveButton("확인", object : DialogInterface.OnClickListener{
//                            override fun onClick(dialog: DialogInterface?, idx: Int) {
//                                dialog!!.dismiss()
//                            }
//                        })
//                        .create()
//                        .show()
//                }
//            }
//        }
//
//        btnNameComplete.setOnClickListener {
//            // 닉네임 중복 확인, 중복 아닐 때만 onClick 과 dismiss 수행
//            if (limitName == true) {
//                nameListenerLi.forEach { it -> it?.remove() }  // null이 아닐 때만 수행
//                onClickListener.onClicked(editName.text.toString())
//                dialog?.dismiss()
//            } else {
//                editName.error = "별명 중복확인을 해주세요."
//            }
//        }
//        return binding.root
//    }

//    override fun onDestroy() {
//        nameListenerLi.forEach { it -> it?.remove() }  // null이 아닐 때만 수행
//        super.onDestroy()
//    }

//    fun setOnClickListener(listener: OnDialogClickListener) {
//        onClickListener = listener
//    }
//
//    interface OnDialogClickListener
//    {
//        fun onClicked(name: String)
//    }
//
//    private val listnerEditName = object : TextWatcher {
//        val pattern = Pattern.compile("^[a-zA-Z0-9가-힣ㄱ-ㅎㅏ-ㅣ\\u318D\\u119E\\u11A2\\u2022\\u2025a\\u00B7\\uFE55]+$") // 공백포함 특수문자 체크
//
//        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//        }
//
//        override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
//            val editName = binding.editName
//            if (s != null){
//                if (pattern.matcher(s.toString()).matches()) {   // 한글, 영어, 숫자만 있을 때
//                    limitText = true
//                    editName.error = null
//                }
//                else{
//                    limitText = false
//                    editName.error = "특수문자, 공백 제외 7 글자 이하"
//                }
//            }
//        }
//
//        override fun afterTextChanged(s: Editable?) {
//            if (limitName == true)  // 다시 타자치면 리셋
//                limitName = false
//        }
//    }
}