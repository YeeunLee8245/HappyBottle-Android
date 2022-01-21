package kr.co.yeeunlee.own.project1.mywriting

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kr.co.yeeunlee.own.project1.mywriting.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    companion object{
        const val HOME_TAG = "HomeFragment"
        const val STORAGE_TAG = "StorageFragment"
        const val SEND_TAG = "SendFragment"
    }
    private lateinit var binding:ActivityMainBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var user:User
    private var userEmail:String = LoginStartActivity.mAuth.currentUser!!.email!!
    private val homeFragment = HomeFragment()
    private val storageFragment = StorageFragment()
    private val sendFragment = SendFragment()
    private val arrayTag = arrayListOf(HOME_TAG, STORAGE_TAG, SEND_TAG)
    var currentTag:String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        changeFragment(HOME_TAG, homeFragment)
        binding.btnHome.setOnClickListener { changeFragment(HOME_TAG, homeFragment)}
        binding.btnStorage.setOnClickListener { changeFragment(STORAGE_TAG, storageFragment) }
        binding.btnSend.setOnClickListener { changeFragment(SEND_TAG, sendFragment) }
    }

    private fun changeFragment(fragmentTag: String, fragment: Fragment){
        val tran = supportFragmentManager.beginTransaction()

        if (currentTag == "") {
            currentTag = fragmentTag
            tran.add(R.id.fragment,fragment )
            return
        }

        if (currentTag != fragmentTag){
            currentTag = fragmentTag
            tran.replace(R.id.fragment, fragment)
            tran.commit()
        }
    }
}