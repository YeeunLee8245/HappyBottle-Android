package kr.co.yeeunlee.own.project1.mywriting

import android.app.Dialog
import android.content.Context
import android.view.WindowManager
import android.widget.Button

class NameLayoutDialog(context: Context) {
    private val dialog = Dialog(context)
    private lateinit var onClickListener:OnDialogClickListener

    fun setOnClickListener(listener: OnDialogClickListener)
    {
        onClickListener = listener
    }

    fun showDialog()
    {
        dialog.setContentView(R.layout.dialog_name_layout)
        dialog.window!!.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(true)
        dialog.show()
        val btnNameComplete = dialog.findViewById<Button>(R.id.btnNameComplete)
        btnNameComplete.setOnClickListener {
            dialog.dismiss()
        }
        //val edit_name = dialog.findViewById<EditText>(R.id.name_edit)

    }

    interface OnDialogClickListener
    {
        fun onClicked(name: String)
    }
}