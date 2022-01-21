package kr.co.yeeunlee.own.project1.mywriting

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import kr.co.yeeunlee.own.project1.mywriting.databinding.FragmentStorageBinding


class StorageFragment : Fragment() {
    private lateinit var binding: FragmentStorageBinding
    private val storageViewModel: StorageViewModel by viewModels<StorageViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentStorageBinding.inflate(inflater, container, false)
        initBtnBottle() // 저금통 생성 & 저금통을 누르면
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        storageViewModel.storageBottleSnap.observe(viewLifecycleOwner){
            initBtnBottle()
        }
    }

    private fun initBtnBottle(){}

}