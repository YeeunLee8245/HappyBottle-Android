package kr.co.yeeunlee.own.project1.mywriting.utils

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog

object AlertUtil {

    fun showItemsDialog(context: Context, msg: String, itemList: Array<String>, action: DialogInterface.OnClickListener) {
        AlertDialog.Builder(context)
            .setTitle(msg)
            .setCancelable(false)
            .setItems(itemList, action)
            .create()
            .show()
    }
}