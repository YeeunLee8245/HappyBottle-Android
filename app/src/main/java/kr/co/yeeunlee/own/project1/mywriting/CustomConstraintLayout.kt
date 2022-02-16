package kr.co.yeeunlee.own.project1.mywriting

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout

class CustomConstraintLayout(context:Context, attrs:AttributeSet) : ConstraintLayout(context, attrs){
    companion object{
        var vaildLine = true
        var type:Int = 0
    }
    private val background = arrayListOf(R.drawable.memo_blue, R.drawable.memo_pink, R.drawable.memo_yellow
        , R.drawable.memo_six_blue, R.drawable.memo_six_pink, R.drawable.memo_six_yellow
        , R.drawable.post_blue, R.drawable.post_pink, R.drawable.post_yellow)
    init {
        val idx = (0..8).random()
        type = idx
        vaildLine = idx < 6 //  true/false
        setBackgroundResource(background[idx])
    }

    fun changeBackground(i:Int){
        setBackgroundResource(background[i])
        type = i
        vaildLine = i < 6
    }
}