package kr.co.yeeunlee.own.project1.mywriting

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kr.co.yeeunlee.own.project1.mywriting.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var user:User
    private var userEmail:String = LoginStartActivity.mAuth.currentUser!!.email!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        googleSignInClient = GoogleSignIn.getClient(this, LoginStartActivity.gso)
        val docRef = LoginStartActivity.db.collection("user").document(userEmail)
        docRef.get().addOnSuccessListener { dcmSnapshot ->
            user = User(
                dcmSnapshot.get("name")!! as String,
                dcmSnapshot.get("email")!! as String,
                dcmSnapshot.get("vaildPassWord")!! as Boolean,
                dcmSnapshot.get("password") as String?
            )
            Log.d("사용자 확인", user.toString())
        }

        binding.btnLogOut.setOnClickListener {
            logOut()
            val intentLoginStart = Intent(this, LoginStartActivity::class.java)
            startActivity(intentLoginStart)
            finish()
        }
        binding.btnLogDelete.setOnClickListener {   // 계정 탈퇴
            LoginStartActivity.db.collection("user").document(userEmail)    // 비동기 주의
                .delete()
                .addOnCompleteListener {
                    Log.d("db삭제성공", "DocumentSnapshot successfully deleted!")
                    LoginStartActivity.db.collection("check").document("name")
                        .update("name", FieldValue.arrayUnion(user!!.name!!))
                        .addOnSuccessListener {
                            LoginStartActivity.mAuth.currentUser!!.delete()
                            logOut()
                            startActivity(Intent(this,LoginStartActivity::class.java))
                            finish()
                        }
                }
                .addOnFailureListener { e -> Log.w("db삭제실패", "Error deleting document", e) }

        }
    }

    private fun logOut(){
        LoginStartActivity.mAuth.signOut()
        googleSignInClient.signOut()
    }
}