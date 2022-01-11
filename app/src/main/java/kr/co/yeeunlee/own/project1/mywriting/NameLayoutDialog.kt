package kr.co.yeeunlee.own.project1.mywriting

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import com.google.android.material.textfield.TextInputLayout

class NameLayoutDialog(context: Context) {
    private val dialog = Dialog(context)
    private lateinit var onClickListener:OnDialogClickListener
    private val db = LoginStartActivity.db
    private val context = context

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

        val editLayoutName = dialog.findViewById<TextInputLayout>(R.id.editLayoutName)
        val btnNameDuplicate = dialog.findViewById<Button>(R.id.btnNameDuplicate)
        val btnNameComplete = dialog.findViewById<Button>(R.id.btnNameComplete)
        var name:String? = null
        var valid:Boolean = false

        btnNameDuplicate.setOnClickListener {
            db.collection("check").document("name").get()
                .addOnSuccessListener { document ->
                    val li = document.get("name") as List<String>
                    name = editLayoutName.editText?.text.toString()
                    Log.d("별명",name.toString())
                    if ((li.contains(name.toString()) == false) and (name != null) ) {
                        valid = true
                        Toast.makeText(context,"가능한 별명입니다.",Toast.LENGTH_SHORT).show()
                    }
                    else if (name == null)
                        Toast.makeText(context,"별명을 입력해주세요",Toast.LENGTH_SHORT).show()
                    else // valid == false
                        Toast.makeText(context,"이미 존재하는 별명입니다.",Toast.LENGTH_SHORT).show()
                    Log.d("별명 valid","$name $valid")
                }
                .addOnFailureListener { Log.d("별명 등록 실패","${it}") }
        }

        btnNameComplete.setOnClickListener {
            //Log.d("닉네임",editLayoutName.editText?.text.toString())
            // 닉네임 중복 확인, 중복 아닐 때만 onClick 과 dismiss 수행
            if (valid == true) {
                onClickListener.onClicked(editLayoutName.editText?.text.toString())
                dialog.dismiss()
            }
        }

        //val edit_name = dialog.findViewById<EditText>(R.id.name_edit)

    }

    interface OnDialogClickListener
    {
        fun onClicked(name: String)
    }
}