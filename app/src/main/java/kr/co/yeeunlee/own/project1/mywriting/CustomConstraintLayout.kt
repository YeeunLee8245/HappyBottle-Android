package kr.co.yeeunlee.own.project1.mywriting

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import androidx.constraintlayout.widget.ConstraintLayout

class CustomConstraintLayout(context:Context, attrs:AttributeSet) : ConstraintLayout(context, attrs){
    companion object{
        var vaildLine = true
    }
    private val background = arrayListOf(R.drawable.memo_blue, R.drawable.memo_pink, R.drawable.memo_six_blue
        , R.drawable.memo_six_pink, R.drawable.memo_six_yellow, R.drawable.memo_yellow, R.drawable.post_blue
        , R.drawable.post_pink, R.drawable.post_yellow)
    init {
        Log.d("초기화2","2")
        //TODO(랜덤으로 배경 초기화)
        val idx = (0..8).random()
        vaildLine = idx < 6 //  true/false
        setBackgroundResource(background[idx])
    }
}