package kr.co.yeeunlee.own.project1.mywriting

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.co.yeeunlee.own.project1.mywriting.databinding.InfoListItemBinding

class ViewPagerAdapter(infoLi: ArrayList<Int>) : RecyclerView.Adapter<ViewPagerAdapter.ViewHolder>() {
    var itemLi = infoLi

    inner class ViewHolder(view: InfoListItemBinding) : RecyclerView.ViewHolder(view.root) {
            val imageViewInfo = view.imageViewInfo
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : ViewHolder{
        val view = InfoListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewPagerAdapter.ViewHolder, position: Int) {
        holder.imageViewInfo.setImageResource(itemLi[position])
    }

    override fun getItemCount(): Int = itemLi.size

}