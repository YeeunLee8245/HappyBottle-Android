package kr.co.yeeunlee.own.project1.mywriting

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.setFragmentResultListener
import kr.co.yeeunlee.own.project1.mywriting.databinding.FragmentOpenBottleBinding

class OpenBottleFragment : Fragment() {
    private lateinit var binding: FragmentOpenBottleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFragmentResultListener("requestKey"){ requestKey, bundle ->
            val index = bundle.getInt("bundleKey")
            Log.d("번들 키 값", index.toString())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentOpenBottleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }


}