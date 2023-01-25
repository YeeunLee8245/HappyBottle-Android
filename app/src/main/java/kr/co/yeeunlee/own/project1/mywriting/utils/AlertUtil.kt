package kr.co.yeeunlee.own.project1.mywriting.utils

import android.content.Context
import android.content.DialogInterface
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import kr.co.yeeunlee.own.project1.mywriting.R

object AlertUtil {

    fun showItemsDialog(
        context: Context,
        @StringRes title: Int,
        @StringRes positiveText: Int,
        @StringRes negativeText: Int,
        positiveAction: (() -> Unit)?,
        negativeAction: (() -> Unit)?
    ) {
        AlertDialog.Builder(context)
            .setTitle(title)
            .setPositiveButton(positiveText) { dialog, _ ->
                positiveAction?.let { it() }
                dialog.cancel()
            }
            .setNegativeButton(negativeText) { dialog, _ ->
                negativeAction?.let { it() }
                dialog.cancel()
            }
            .setCancelable(false)
            .create()
            .show()
    }

    fun showPositiveDialog(
        context: Context,
        @StringRes title: Int,
        @StringRes msg: Int,
        @StringRes positiveText: Int,
        action: (() -> Unit)?
    ) {
        AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(msg)
            .setCancelable(false)
            .setPositiveButton(positiveText) { dialog, _ ->
                if (action != null) {
                    action()
                }
                dialog.dismiss()
            }
            .create()
            .show()
    }
}