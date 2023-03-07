package kr.co.yeeunlee.own.project1.mywriting.ui.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import kr.co.yeeunlee.own.project1.mywriting.R
import kr.co.yeeunlee.own.project1.mywriting.databinding.FragmentNicknameDialogBinding
import kr.co.yeeunlee.own.project1.mywriting.ui.FirebaseViewModel
import kr.co.yeeunlee.own.project1.mywriting.ui.LoginStartActivity
import kr.co.yeeunlee.own.project1.mywriting.utils.NickNameValidChecker
import kr.co.yeeunlee.own.project1.mywriting.utils.states.ActivityState
import kr.co.yeeunlee.own.project1.mywriting.utils.states.ResultState
import timber.log.Timber

class NickNameDialogFragment() : DialogFragment() {
    private var _binding: FragmentNicknameDialogBinding? = null
    private val binding get() = _binding!!
    private val viewmodel: FirebaseViewModel by viewModels({requireActivity()})
    private val activity: LoginStartActivity by lazy { requireActivity() as LoginStartActivity }
    private val nicknameValidChecker = NickNameValidChecker()
    private var email: String? = null

    companion object {
        const val EMAIL_ARGUMENT = "email_argument"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNicknameDialogBinding.inflate(inflater, container, false)
        email = arguments?.getString(EMAIL_ARGUMENT)
        Timber.i("닉네임 이메일 ${email}")
        setScreen()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            completeButton.setOnClickListener { checkAvailable(nameEditText.text.toString()) }
            nameEditText.addTextChangedListener(listenerNameEditText)
            screenProgressBar.isVisible = false
        }
        observeAvailableNickName()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun observeAvailableNickName() {
        viewmodel.availableNickNameStatus.observe(this) { result ->
            when (result) {
                is ResultState.Error -> {
                    activity.loadErrorMessage(result.exception)
                    isProgressButton(false)
                }
                is ResultState.Failed -> {
                    binding.nameEditText.error = getString(result.message)
                    isProgressButton(false)
                }
                ResultState.Loading -> {
                    isProgressButton(true)
                }
                is ResultState.Success -> {
                    email?.let { email ->
                        viewmodel.addNewUser(result.data, email)
                        activity.moveToScreen(ActivityState.Main) // TODO: 계정 생성
                    }
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
        if (checkNickNameLimit(nickname))
            viewmodel.isAvailableNickName(nickname)
    }

    private fun checkNickNameLimit(nickname: String): Boolean {
        val result = nicknameValidChecker.checkCondition(nickname)
        if (!result.first)
            binding.nameEditText.error = getString(R.string.nickname_empty)
        else if (!result.second || !nicknameValidChecker.checkSpecialCharacters(nickname))
            binding.nameEditText.error = getString(R.string.nickname_limit)
        else
            return true
        return false
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

    private val listenerNameEditText = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun afterTextChanged(str: Editable?) {
            if (checkNickNameLimit(str.toString())) {
                binding.nameEditText.error = null
            }
        }

    }
}