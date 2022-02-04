package kr.co.yeeunlee.own.project1.mywriting

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import kr.co.yeeunlee.own.project1.mywriting.databinding.ItemSendRecyclerBinding
import kr.co.yeeunlee.own.project1.mywriting.databinding.ItemStorageRecyclerBinding

class SendAdapter(private var postLi: ArrayList<Note>)
    : RecyclerView.Adapter<SendAdapter.ViewHolder>(){
    private val firebaseRepo = FirebaseRepository()

    inner class ViewHolder(view: ItemSendRecyclerBinding): RecyclerView.ViewHolder(view.root){
        val txtFromName = view.txtFromName
        val txtPreview = view.txtPreview
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ItemSendRecyclerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        postLi!!.get(position).let { item ->
            with(holder){
                txtFromName.text = item.name
                txtPreview.text = if(item.text.length < 8) item.text
                else item.text.substring(0,7) + "..."
                itemView.setOnClickListener {
                    itemClickListener.onItemClick(it, position)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return postLi.size
    }

    fun setData(newData: ArrayList<Note>){
        postLi = newData
        notifyDataSetChanged()
    }

    interface OnItemClickListener{
        fun onItemClick(v: View, position: Int)
    }

    fun setItemClickListener(onItemClickListener: OnItemClickListener){
        this.itemClickListener = onItemClickListener
    }

    private lateinit var itemClickListener: OnItemClickListener
}