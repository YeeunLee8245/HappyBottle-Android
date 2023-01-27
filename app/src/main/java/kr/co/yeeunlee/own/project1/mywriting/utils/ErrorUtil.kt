package kr.co.yeeunlee.own.project1.mywriting.utils

import android.content.Context
import androidx.appcompat.app.AlertDialog
import kr.co.yeeunlee.own.project1.mywriting.R

object ErrorUtil {

    fun showErrorMessage(context: Context, message: String?, function: () -> Unit) {
        AlertDialog.Builder(context)
            .setMessage(message ?: context.getString(R.string.service_error))
            .setPositiveButton(R.string.confirm) { dialog, _ ->
                function()
                dialog.cancel() }
            .setCancelable(false)
            .create()
            .show()
    }
}