package kr.co.yeeunlee.own.project1.mywriting

import android.content.Context
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat


class CustomEditTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.editTextStyle // super class의 스타일을 가지고 온다
) : AppCompatEditText(context, attrs, defStyleAttr){
//  이 생성자는 LayoutInflter에서 사용됩니다.
    init {
        if (CustomConstraintLayout.vaildLine == true)
            setBackgroundResource(R.drawable.lines)
    }
}

