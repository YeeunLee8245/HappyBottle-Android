package kr.co.yeeunlee.own.project1.mywriting

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.*
import kr.co.yeeunlee.own.project1.mywriting.databinding.FragmentOpenBottleBinding
import kotlin.properties.Delegates

class OpenBottleFragment : Fragment() {
    private lateinit var binding: FragmentOpenBottleBinding
    private val firebaseRepo = FirebaseRepository()
    private val opnBtlViewModel: OpnBtlViewModel by viewModels<OpnBtlViewModel>()
    private var orderLast by Delegates.notNull<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFragmentResultListener("requestKey"){ requestKey, bundle ->
            orderLast = bundle.getInt("bundleKey")
            Log.d("번들 키 값", orderLast.toString())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentOpenBottleBinding.inflate(inflater, container, false)
        initBtnRead()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    fun initBtnRead(){
        val btnLi = arrayListOf<Button>(binding.btn1, binding.btn2, binding.btn3, binding.btn4, binding.btn5
            , binding.btn6, binding.btn7, binding.btn8, binding.btn9, binding.btn10, binding.btn11, binding.btn12
            , binding.btn13, binding.btn14, binding.btn15, binding.btn16, binding.btn17, binding.btn18, binding.btn19
            , binding.btn20, binding.btn21, binding.btn22, binding.btn23, binding.btn24, binding.btn25, binding.btn26
            , binding.btn27, binding.btn28, binding.btn29, binding.btn30)
        for (i in 1..btnLi.size){
            btnLi[i-1].setOnClickListener {
                var snapshot: DocumentSnapshot? = null
                CoroutineScope(Dispatchers.Main).launch {
                    snapshot = firebaseRepo.getOpnNoteSnapshot(orderLast - 30 + i) //60 - 30 + 1 = 31
                    val dialog = ReadDialogFragment(snapshot!!, orderLast-29)

                    activity?.supportFragmentManager?.let { fragmentManager ->
                        dialog.show(fragmentManager, "readNote")
                    }
                }

            }
        }
    }

    //TODO("뒤로가기시 보관함 창(프래그먼트로) 가기")

}