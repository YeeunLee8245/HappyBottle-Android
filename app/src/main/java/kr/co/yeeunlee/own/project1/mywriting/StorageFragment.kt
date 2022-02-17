package kr.co.yeeunlee.own.project1.mywriting

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kr.co.yeeunlee.own.project1.mywriting.databinding.FragmentStorageBinding
import kr.co.yeeunlee.own.project1.mywriting.databinding.ItemStorageRecyclerBinding


class StorageFragment : Fragment() {
    private lateinit var binding: FragmentStorageBinding
    private val storageViewModel: StorageViewModel by viewModels<StorageViewModel>()
    private val bottleColorLi = arrayListOf(R.drawable.bottle_lightgreen, R.drawable.bottle_mint, R.drawable.bottle_green
        , R.drawable.bottle_blue, R.drawable.bottle_purple, R.drawable.bottle_orange
        , R.drawable.bottle_sky, R.drawable.bottle_red, R.drawable.bottle_pink
        , R.drawable.bottle_darkblue)
    private var zeroBottle:MutableLiveData<Boolean> = MutableLiveData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        storageViewModel.getStorageBottleLi(zeroBottle)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStorageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerStorage.layoutManager = LinearLayoutManager(context)
        binding.recyclerStorage.adapter = StorageAdapter(storageViewModel.stgBtSnapLi.value!!)

        storageViewModel.stgBtSnapLi.observe(viewLifecycleOwner, Observer {
            (binding.recyclerStorage.adapter as StorageAdapter).setData(it)
        })
        zeroBottle.observe(viewLifecycleOwner){
            if (it == true){
                visibilityText(true)
            }else
                visibilityText(false)
        }

    }

    private fun visibilityText(vaild: Boolean){
        if (vaild == true) {
            binding.textStorage.visibility = View.VISIBLE
            binding.textStorage.isEnabled = true
        }else{
            binding.textStorage.visibility = View.INVISIBLE
            binding.textStorage.isEnabled = false
        }
    }

    inner class StorageAdapter(private var dataLi: ArrayList<BottleList>):
        RecyclerView.Adapter<StorageAdapter.ViewHolder>(){

        inner class ViewHolder(view: ItemStorageRecyclerBinding): RecyclerView.ViewHolder(view.root){
            val btnBottle1 = view.btnBottle1
            val btnBottle2 = view.btnBottle2
            val btnBottle3 = view.btnBottle3
            val text1 = view.text1
            val text2 = view.text2
            val text3 = view.text3
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = ItemStorageRecyclerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            dataLi!!.get(position).let { item ->
                with(holder){
                    if (item.first == null){
                        btnBottle1.visibility = View.INVISIBLE
                        btnBottle1.isEnabled = false
                    }else{
                        text1.setText((item.first!!/30).toString())
                        btnBottle1.setBackgroundResource(bottleColorLi[item.first!!/30%11])
                        btnBottle1.setOnClickListener {
                            (activity as MainActivity).changeOpnBtlFragment(item.first!!)
                        }
                    }
                    if (item.second == null){
                        btnBottle2.visibility = View.INVISIBLE
                        btnBottle2.isEnabled = false
                    }else{
                        text2.setText((item.second!!/30).toString())
                        btnBottle2.setBackgroundResource(bottleColorLi[item.second!!/30%11])
                        btnBottle2.setOnClickListener {
                            (activity as MainActivity).changeOpnBtlFragment(item.second!!)
                        }
                    }
                    if (item.third == null){
                        btnBottle3.visibility = View.INVISIBLE
                        btnBottle3.isEnabled = false
                    }else{
                        text3.setText((item.third!!/30).toString())
                        btnBottle3.setBackgroundResource(bottleColorLi[item.third!!/30%11])
                        btnBottle3.setOnClickListener {
                            (activity as MainActivity).changeOpnBtlFragment(item.third!!)
                        }
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