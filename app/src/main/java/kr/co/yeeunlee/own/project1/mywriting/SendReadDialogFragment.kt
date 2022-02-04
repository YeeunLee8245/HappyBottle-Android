package kr.co.yeeunlee.own.project1.mywriting

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import kr.co.yeeunlee.own.project1.mywriting.databinding.FragmentSendReadDialogBinding


class SendReadDialogFragment : DialogFragment() {
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
        return binding.root
    }

}