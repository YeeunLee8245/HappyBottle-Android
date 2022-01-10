package kr.co.yeeunlee.own.project1.mywriting

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import com.google.android.material.textfield.TextInputLayout

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
        val editLayoutName = dialog.findViewById<TextInputLayout>(R.id.editLayoutName)
        btnNameComplete.setOnClickListener {
            //Log.d("닉네임",editLayoutName.editText?.text.toString())
            // 닉네임 중복 확인, 중복 아닐 때만 onClick 과 dismiss 수행
            onClickListener.onClicked(editLayoutName.editText?.text.toString())
            dialog.dismiss()
        }

        //val edit_name = dialog.findViewById<EditText>(R.id.name_edit)

    }

    interface OnDialogClickListener
    {
        fun onClicked(name: String)
    }
}