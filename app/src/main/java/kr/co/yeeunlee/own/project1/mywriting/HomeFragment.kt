package kr.co.yeeunlee.own.project1.mywriting

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kr.co.yeeunlee.own.project1.mywriting.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private val homeViewModel: HomeViewModel by viewModels<HomeViewModel>()
    private val imgBottle = arrayListOf(R.drawable.bottle_50, R.drawable.bottle_70, R.drawable.bottle_100)
    private val userEmail = LoginStartActivity.mAuth.currentUser?.email.toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("액티비티",activity.toString())
        googleSignInClient = GoogleSignIn.getClient(activity, LoginStartActivity.gso)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater,container,false)
        initBtnWrite()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        homeViewModel.getUserSnapshot() // 화면 뜰 때마다 업뎃, 보틀은 등록할 때 업뎃해주니 문제X
        // 이렇게 하는 것보다 나중에 프로필 수정 버튼을 누르면 업뎃 해주는 걸로 바꾸자. 이건 부담이다.
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // LiveDate의 value가 it으로 들어감
        homeViewModel.userSnapshot.observe(viewLifecycleOwner){
            initUserView(it)
        }
//        homeViewModel.noteSnapshot.observe(viewLifecycleOwner){
//            initBottleView(it)  // 데이터 변경시 보틀 상태 데이터와 UI 업데이트
//        }
    }

    private fun initUserView(snapshot: DocumentSnapshot){
        val num = snapshot["numNote"].toString().toInt()
        binding.apply {
//            imgUser.text = if (snapshot["img"] == null) "프로필 나중에 추가" else "널이여야만 함"
            txtName.text = snapshot["name"].toString()
            txtStatus.text = if (snapshot["status"]== null) "상메 나중에 추가" else "널이여야만 함"
        }
        Log.d("bottle",num.toString())
//        when (num%5){
//            in 0..1 -> binding..setImageResource(imgBottle[0])
//            in 2..3 -> {
//                binding.imgBottle.setImageResource(imgBottle[1])
//                Log.d("bottle2",num.toString())
//            }
//            else -> binding.imgBottle.setImageResource(imgBottle[2])
//        }
    }

    private fun initBtnWrite(){
        binding.btnWrite.setOnClickListener {
            val dialog = WriteDialogFragment()
            val firebaseRepo: FirebaseRepository = FirebaseRepository()
            //TODO("다이얼로그 프래그먼트 생성,onClickListener로 정보 넘기기")
            dialog.setButtonClickListener(object : WriteDialogFragment.OnButtonClickListener{
                override fun onButtonClicked(textEditNote: String, type:Int) {
                    CoroutineScope(Dispatchers.Default).launch {
                        // 보틀 업데이트
                        homeViewModel.setUserSnapshot(firebaseRepo.setNoteAdd(textEditNote, type))
                    }
                }
            })
            activity?.supportFragmentManager?.let { fragmentManager ->
                dialog.show(fragmentManager, "writeNote")
            }
            //firebaseRepo.setNoteAdd(newNote)
        }
        binding.btnLogout.setOnClickListener {  // 로그아웃
            logout()
            val intentLoginStart = Intent(activity, LoginStartActivity::class.java)
            startActivity(intentLoginStart)
            activity!!.finish()
        }
        binding.btnLogDelete.setOnClickListener {   // 계정 탈퇴
            userDelete()
        }
    }

    private fun logout(){
        googleSignInClient.signOut().addOnSuccessListener {
            LoginStartActivity.mAuth.signOut()
        }
    }

    private fun userDelete(){
        LoginStartActivity.db.collection("user").document(userEmail)    // 비동기 주의
            .delete()
            .addOnCompleteListener {
                Log.d("db삭제성공", "DocumentSnapshot successfully deleted!")
                LoginStartActivity.db.collection("check").document("name")
                    .update("name", FieldValue.arrayRemove(homeViewModel.userSnapshot.value!!.get("name").toString()))
                    .addOnSuccessListener {
                        Log.d("액티비티",activity.toString())
                        LoginStartActivity.mAuth.currentUser!!.delete().addOnSuccessListener {
                            logout()
                            startActivity(Intent(activity,LoginStartActivity::class.java))
                            activity!!.finish()
                        }.addOnFailureListener {
                            Log.d("탈퇴에러",it.toString())
                        }
                    }
            }
            .addOnFailureListener { e -> Log.w("db삭제실패", "Error deleting document", e) }
    }
}