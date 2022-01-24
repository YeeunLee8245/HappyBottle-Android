package kr.co.yeeunlee.own.project1.mywriting

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kr.co.yeeunlee.own.project1.mywriting.databinding.FragmentStorageBinding
import kr.co.yeeunlee.own.project1.mywriting.databinding.ItemStorageRecyclerBinding


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
        // viewModel 초기화와 동시에 bottle 업데이트 한 번 됨
        //initBtnBottle() // 저금통 생성 & 저금통을 누르면
        //storageViewModel.oneMore()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        storageViewModel.oneMore()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerStorage.layoutManager = LinearLayoutManager(context)
        binding.recyclerStorage.adapter = StorageAdapter(storageViewModel.stgBtSnapLi.value!!)

        storageViewModel.stgBtSnapLi.observe(viewLifecycleOwner, Observer {
            (binding.recyclerStorage.adapter as StorageAdapter).setData(it)
        })

    }


//    private fun initBtnBottle(){
//        val firebaseRepository = FirebaseRepository()
//        firebaseRepository.getStorageBottle()
//    }

    inner class StorageAdapter(private var dataLi: ArrayList<BottleList>):
        RecyclerView.Adapter<StorageAdapter.ViewHolder>(){

        inner class ViewHolder(view: ItemStorageRecyclerBinding): RecyclerView.ViewHolder(view.root){
            val btnBottle1 = view.btnBottle1
            val btnBottle2 = view.btnBottle2
            val btnBottle3 = view.btnBottle3
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            Log.d("보틀 수3", parent.toString())
            val view = ItemStorageRecyclerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            Log.d("보틀 수4", dataLi!!.get(position).toString())
            dataLi!!.get(position).let { item ->
                with(holder){
                    if (item.first == null){
                        btnBottle1.visibility = View.INVISIBLE
                        btnBottle1.isEnabled = false
                    }
                    if (item.second == null){
                        btnBottle2.visibility = View.INVISIBLE
                        btnBottle2.isEnabled = false
                    }
                    if (item.third == null){
                        btnBottle3.visibility = View.INVISIBLE
                        btnBottle3.isEnabled = false
                    }
                }
            }
        }

        override fun getItemCount(): Int {
            return storageViewModel.getSize()
        }

        fun setData(newData: ArrayList<BottleList>){
            dataLi = newData
            notifyDataSetChanged()
        }
    }

}