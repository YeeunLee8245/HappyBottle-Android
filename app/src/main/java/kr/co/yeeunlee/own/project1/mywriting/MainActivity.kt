package kr.co.yeeunlee.own.project1.mywriting

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.ktx.auth
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

        binding.btnLogOut.setOnClickListener {
            logOut()
            val intentLoginStart = Intent(this, LoginStartActivity::class.java)
            startActivity(intentLoginStart)
            finish()
        }
        binding.btnLogDelete.setOnClickListener {
            logOut()
            LoginStartActivity.db.collection("user").document(userEmail)
                .delete()
                .addOnCompleteListener { Log.d("db삭제성공", "DocumentSnapshot successfully deleted!") }
                .addOnFailureListener { e -> Log.w("db삭제실패", "Error deleting document", e) }
            TODO("LoginStart화면으로 돌아가게하기")
            finish()
        }
    }

    private fun setup(){
        val db = Firebase.firestore
    }
    private fun logOut(){
        LoginStartActivity.mAuth.signOut()
        googleSignInClient.signOut()
    }
}