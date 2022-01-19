package kr.co.yeeunlee.own.project1.mywriting

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.DocumentSnapshot
import kr.co.yeeunlee.own.project1.mywriting.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val homeViewModel: HomeViewModel by viewModels<HomeViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // LiveDate의 value가 it으로 들어감
        homeViewModel.userSnapshot.observe(viewLifecycleOwner){
            initUserView(it)
        }
        homeViewModel.noteSnapshot.observe(viewLifecycleOwner){
            initBottleView(it)  // 데이터 변경시 보틀 상태 데이터와 UI 업데이트
        }
    }

    private fun initUserView(snapshot: DocumentSnapshot){
        binding.apply {
            imgUser.text = if (snapshot["img"] == null) "프로필 나중에 추가" else "널이여야만 함"
            txtName.text = snapshot["name"].toString()
            txtStatus.text = if (snapshot["status"]== null) "상메 나중에 추가" else "널이여야만 함"
        }
    }

    private fun initBottleView(snapshot: DocumentSnapshot){

    }
}