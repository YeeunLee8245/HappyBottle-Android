package kr.co.yeeunlee.own.project1.mywriting.utils

import android.content.Context
import android.widget.Toast
import kr.co.yeeunlee.own.project1.mywriting.R

object ToastUtil {
    fun showToast(context: Context, message: String?) {
        Toast.makeText(context, message ?: context.getString(R.string.confirm), Toast.LENGTH_SHORT).show()
    }
}