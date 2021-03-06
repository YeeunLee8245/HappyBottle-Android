package kr.co.yeeunlee.own.project1.mywriting

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.co.yeeunlee.own.project1.mywriting.databinding.ActivitySignInBinding
import java.util.regex.Pattern

class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding
    private lateinit var connection: NetworkConnection
    private val mAuth = SplashActivity.mAuth
    private val db = SplashActivity.db
    private val imgIdx:Int = (0..7).random()
    private val imgLi = arrayListOf(R.drawable.blue, R.drawable.green, R.drawable.mint
        , R.drawable.orange, R.drawable.pink, R.drawable.purple, R.drawable.sky
        , R.drawable.yellow)
    private val nameListenerLi:ArrayList<ListenerRegistration> = ArrayList<ListenerRegistration>()
    private val emailListenerLi:ArrayList<ListenerRegistration> = ArrayList<ListenerRegistration>()
    private var map = hashMapOf<String,Boolean>("email" to false, "name" to false,
        "password" to false)
    private var limitName:Boolean = false
    private var limitText:Boolean = false



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        connection = NetworkConnection(applicationContext)
        connection.observe(this){ isConnected ->
            if (isConnected){
            }else{
                makeAlterDialog()
            }
        }
        binding.imageProfil.setImageResource(imgLi[imgIdx])
        binding.btnNameDupli.setOnClickListener {
            var name = binding.editName.text
            duplicateName(name.toString())
        }
        binding.btnComplete.setOnClickListener {
            completeCheck(binding.editEmail.text.toString())
        }
        binding.editName.addTextChangedListener(listnerEditName)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intentLogin = Intent(this,LoginStartActivity::class.java)
        intentLogin.action = android.content.Intent.ACTION_MAIN
        intentLogin.addCategory(android.content.Intent.CATEGORY_LAUNCHER)
        intentLogin.flags = android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intentLogin)
        finish()
    }

    override fun onDestroy() {
        connection.unregister()
        emailListenerLi.forEach { it.remove() }
        nameListenerLi.forEach { it.remove() }
        super.onDestroy()
    }

    private fun duplicateName (name: String){
        // ????????? ????????? ????????? ?????????
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.editName.windowToken,0)
        binding.editName.clearFocus()

//        if (limitName == false){
//            binding.editName.error = "????????????, ?????? ?????? 7 ?????? ??????"
//            return
//        }
        var nameListener:ListenerRegistration? = null
        nameListener = db.collection("check").document("name").addSnapshotListener { document, error ->
            nameListenerLi.add(nameListener!!)
            val li = document!!.get("name") as List<String>
            map["name"] = false
            if ((li.contains(name) == false) and (name != "") and (limitText == true)) {
                map["name"] = true
                binding.editName.error = null
                Toast.makeText(this,"????????? ???????????????.", Toast.LENGTH_SHORT).show()
            }
            else if (name == "")
                binding.editName.error="????????? ??????????????????"
            else if (limitText == false)
                binding.editName.error="????????????, ?????? ?????? 7 ?????? ??????"
            else // valid == false
                binding.editName.error="?????? ???????????? ???????????????."
            if (error != null){
                AlertDialog.Builder(this)
                    .setTitle("?????? ???????????????.")
                    .setMessage(" ??????????????? ??????????????????. ????????????:$error")
                    .setCancelable(false)
                    .setPositiveButton("??????", object : DialogInterface.OnClickListener{
                        override fun onClick(dialog: DialogInterface?, idx: Int) {
                            dialog!!.dismiss()
                        }
                    })
                    .create()
                    .show()
            }
        }
    }

    private fun completeCheck(email:String){
        map["email"] = false
        // ????????? ?????? ??????
        var pattern = android.util.Patterns.EMAIL_ADDRESS
        if (pattern.matcher(email).matches()) { // ?????? ????????? ??????
            binding.editEmail.error = null
            var emailListener:ListenerRegistration? = null
            emailListener = db.collection("user").document(email).addSnapshotListener { document, error ->
                // ??????
                emailListenerLi.add(emailListener!!)
                if (document!!.exists()) {
                    map["email"] = false
                    binding.editEmail.error = "?????? ????????? ????????? ?????????."
                    completeElse()
                } else {
                    map["email"] = true
                    completeElse()
                }
            }
        }
        else {
            binding.editEmail.error = "????????? ????????? ????????? ??????????????????."
            completeElse()
        }
    }

    private fun completeElse(){
        if (map["name"] == false) {
            binding.editName.error = "?????? ??????????????? ????????????."
        }
        completePassWord(
            binding.editPW.text.toString(),
            binding.editPWCheck.text.toString()
        )

        val result = map.filterValues { it == true }
        if (result.size == 3) { // ?????? true??? ??? ?????? ?????? ??????
            val inputName = binding.editName.text.toString()
            val inputEmail = binding.editEmail.text.toString()
            val inputPassword = binding.editPW.text.toString()
            val intentStart = Intent(this, LoginStartActivity::class.java)
            //val token: String = fireRepo.setToken()
            val user = User(
                inputName, inputEmail, true, inputPassword,
                0, 0,"", imgIdx)
            completeSignin(user)
        }
    }

    private fun completePassWord(password:String, checkPassword:String){
        map["password"] = false
        if (password == "")
            binding.editPW.error = "??????????????? ??????????????????."
        else if (password.count() < 6)
            binding.editPW.error = "?????? ?????? ???????????? ??????????????????."
        else{
            if (password.equals(checkPassword)) {
                map["password"] = true
                binding.editPW.error = null
                binding.editPWCheck.error = null
            }
            else
                binding.editPWCheck.error = "??????????????? ?????? ?????? ????????? ??????????????????."
        }

        if (checkPassword == "")
            binding.editPWCheck.error = "???????????? ????????? ??????????????????."

    }


    private val listnerEditName = object : TextWatcher{
        val pattern = Pattern.compile("^[a-zA-Z0-9???-??????-??????-???\\u318D\\u119E\\u11A2\\u2022\\u2025a\\u00B7\\uFE55]+$") // ???????????? ???????????? ??????

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
            if (s != null){
                if (pattern.matcher(s.toString()).matches()) {   // ??????, ??????, ????????? ?????? ???
                    limitText = true
                    binding.editName.error = null
                }
                else{
                    limitText = false
                    binding.editName.error = "????????????, ?????? ?????? 7 ?????? ??????"
                }
            }
        }

        override fun afterTextChanged(s: Editable?) {
            if ( map["name"] == true) {  // ?????? ???????????? ??????
                limitName = false
                map["name"] = false
            }
        }
    }

    private fun completeSignin(loginUser: User){
        emailListenerLi.forEach { it.remove() }
        nameListenerLi.forEach { it.remove() }
        emailListenerLi.clear()
        nameListenerLi.clear()
        //TODO("???????????? ??????, ?????? ????????? ?????? ??? ?????? ??????")
        mAuth.createUserWithEmailAndPassword(loginUser.email, loginUser.password!!) // ????????? ?????? ??????/?????????
            .addOnCompleteListener{ task ->
                loginUser.password = null
                CoroutineScope(Dispatchers.Main).launch { // ????????? ????????? ?????? ??????????????? ?????? ??? ????????? ????????? ?????? ?????? ??????.
                    FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
                        loginUser.token = token
                        db.collection("user").document(loginUser.email)
                            .set(loginUser)
                            .addOnCompleteListener {
                                binding.editEmail.error = null
                                db.collection("check").document("name")
                                    .update("name", FieldValue.arrayUnion(loginUser.name))
                                    .addOnSuccessListener {
                                        binding.editName.error = null
                                        val intentMain = Intent(this@SignInActivity,MainActivity::class.java)
                                        intentMain.action = Intent.ACTION_MAIN
                                        intentMain.addCategory(Intent.CATEGORY_LAUNCHER)
                                        intentMain.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                                        intentMain.putExtra("INFO_TAG", LoginStartActivity.INFO_TAG)
                                        intentMain.putExtra(LoginStartActivity.NAME_TAG, loginUser.name)
                                        intentMain.putExtra(LoginStartActivity.PROFILE_IMG_TAG, loginUser.profileImg)
                                        startActivity(intentMain)   // ?????? ???????????? ??????
                                        finish()

                                    }
                            }.addOnFailureListener {
                                AlertDialog.Builder(this@SignInActivity)
                                    .setTitle("?????? ???????????????.")
                                    .setMessage(" ??????????????? ??????????????????. ????????????:$it")
                                    .setCancelable(false)
                                    .setPositiveButton("??????", object : DialogInterface.OnClickListener{
                                        override fun onClick(dialog: DialogInterface?, idx: Int) {
                                            dialog!!.dismiss()
                                        }
                                    })
                                    .create()
                                    .show()
                            }
                    }

                }

            }
    }

    private fun makeAlterDialog() {
        AlertDialog.Builder(this)
            .setTitle("????????? ????????? ????????? ??? ????????????...")
            .setCancelable(false)
            .setItems(arrayOf("?????????","??????"), object : DialogInterface.OnClickListener{
                override fun onClick(dialog: DialogInterface?, idx: Int) {
                    if (idx == 0){
                        dialog!!.dismiss()
                        if (connection.value == false){ // ???????????? ?????? ??????
                            makeAlterDialog()
                        }
                    }else if (idx == 1){
                        finish()
                    }
                }
            })
            .create()
            .show()
    }
}