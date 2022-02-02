package kr.co.yeeunlee.own.project1.mywriting

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.text.Editable
import android.text.Selection.setSelection
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat


class CustomEditTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.editTextStyle // super class의 스타일을 가지고 온다
) : AppCompatEditText(context, attrs, defStyleAttr){
    private val mRect: Rect
    private val mPaint: Paint

//  이 생성자는 LayoutInflter에서 사용됩니다.
    init {
        // Rect와 Paint객체 생성
        // Paint객체에 스타일과 색상 입힘
        mRect = Rect()
        mPaint = Paint()
        mPaint.setStyle(Paint.Style.STROKE)
//        if (CustomConstraintLayout.vaildLine == true)
//            setBackgroundResource(R.drawable.lines)
    }

    override fun onDraw(canvas: Canvas) {
        // View안의 텍스트의 라인수가 몇개인지 가져옵니다.
        val count = lineCount
        val r: Rect = mRect
        val paint: Paint = mPaint
        // EditText의 모든 라인에 밑줄을 그립니다
        Log.d("초기화 변동","들어옴")
        if (CustomConstraintLayout.vaildLine == true) {
            //setBackgroundResource(R.drawable.lines)
            for (i in 0 until count) {
                // 현재 텍스트 라인의 베이스라인 좌표를 가져옵니다.
                val baseline = getLineBounds(i, r)
                //Paint객체를 이용하여 배경에 밑줄을 그립니다
                canvas.drawLine(
                    r.left.toFloat(),
                    (baseline + 1).toFloat(),
                    r.right.toFloat(),
                    (baseline + 1).toFloat(),
                    paint
                )
            }
        }
        super.onDraw(canvas)
    }

}

