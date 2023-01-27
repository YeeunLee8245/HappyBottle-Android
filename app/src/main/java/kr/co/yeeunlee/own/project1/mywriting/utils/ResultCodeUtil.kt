package kr.co.yeeunlee.own.project1.mywriting.utils

import android.content.Context
import androidx.appcompat.app.AlertDialog
import kr.co.yeeunlee.own.project1.mywriting.R

class ResultCodeUtil {

    companion object {
        // RESULT_CODE..

//
//        fun setResult(context: Context, result: ResponseResult?) {
//            result?.apply {
//                when(code) {
//
//                }
//            }
//        }

        private fun showAlert(context: Context, message: String, function: () -> Unit) { // TODO: 수정(필요한 곳 찾기)
            AlertDialog.Builder(context)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(R.string.confirm) { dialog, _ ->
                    function()
                    dialog.cancel()
                }
                .show()
                .setCanceledOnTouchOutside(false)
        }

        private fun showToast(context: Context, message: String?) { // TODO: 쪽지 보내기 전송 완료 시와 모든 Toast 메시지
            ToastUtil.showToast(context, message)
        }
    }
}