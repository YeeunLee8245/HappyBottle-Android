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
import androidx.lifecycle.Observer
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.*
import kr.co.yeeunlee.own.project1.mywriting.databinding.FragmentOpenBottleBinding
import kotlin.properties.Delegates

class OpenBottleFragment : Fragment() {
    private lateinit var binding: FragmentOpenBottleBinding
    private val firebaseRepo = FirebaseRepository()
    private val opnBtlViewModel: OpnBtlViewModel by viewModels<OpnBtlViewModel>()
    private var indexLast by Delegates.notNull<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFragmentResultListener("requestKey"){ requestKey, bundle ->
            indexLast = bundle.getInt("bundleKey")
            Log.d("번들 키 값", indexLast.toString())
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
        val btnLi = arrayListOf<Button>(binding.btn1, binding.btn2, binding.btn3, binding.btn4, binding.btn5)
        for (i in 1..btnLi.size){
            btnLi[i-1].setOnClickListener {
                var snapshot: DocumentSnapshot? = null
                CoroutineScope(Dispatchers.Main).launch {
                    snapshot = firebaseRepo.getOpnNoteSnapshot(indexLast - 5 + i)
                    val dialog = ReadDialogFragment(snapshot!!)

                    activity?.supportFragmentManager?.let { fragmentManager ->
                        dialog.show(fragmentManager, "readNote")
                    }
                }

            }
        }
    }

    //TODO("뒤로가기시 보관함 창(프래그먼트로) 가기")

}