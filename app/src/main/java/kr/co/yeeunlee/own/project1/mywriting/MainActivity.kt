package kr.co.yeeunlee.own.project1.mywriting

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kr.co.yeeunlee.own.project1.mywriting.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        googleSignInClient = GoogleSignIn.getClient(this, LoginStartActivity.gso)
        binding.btnLogOut.setOnClickListener {
            LoginStartActivity.mAuth.signOut()
            googleSignInClient.signOut()
            val intentLoginStart = Intent(this, LoginStartActivity::class.java)
            startActivity(intentLoginStart)
            finish()
        }
    }
}