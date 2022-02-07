package kr.co.yeeunlee.own.project1.mywriting

import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kr.co.yeeunlee.own.project1.mywriting.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    companion object{
        const val HOME_TAG = "HomeFragment"
        const val STORAGE_TAG = "StorageFragment"
        const val SEND_TAG = "SendFragment"
        const val OPEN_TAG = "OpnBtlFragment"
        const val FCM_URL = "https://fcm.googleapis.com"
        const val SERVER_KEY = "AAAAo5J_z7o:APA91bGez1wfMPVqb21kPXr1AUB_rr52KM3XD4pC1yES5X0-0gJlJlqZ65ANzjcSIMOnx360oDOq4PADxLAW7dbyVvW82d3rp304te3T8RuVVleef74omnENUvj9H1nW8Rr3LCFhnoSY"
        const val CONTENT_TYPE = "application/json"
    }
    private lateinit var binding:ActivityMainBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var user:User
    private var valueService: String? = null
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

        googleSignInClient = GoogleSignIn.getClient(this, LoginStartActivity.gso)
        changeFragment(HOME_TAG, homeFragment)
        binding.btnHome.setOnClickListener { changeFragment(HOME_TAG, homeFragment)}
        binding.btnStorage.setOnClickListener { changeFragment(STORAGE_TAG, storageFragment) }
        binding.btnSend.setOnClickListener { changeFragment(SEND_TAG, sendFragment) }
        binding.btnClose.setOnClickListener { changeDrawer() }
        binding.btnLogout.setOnClickListener { // 로그아웃
            logout()
            val intentLoginStart = Intent(this, LoginStartActivity::class.java)
            startActivity(intentLoginStart)
            finish()
        }
        binding.btnBan.setOnClickListener { // 탈퇴
            CoroutineScope(Dispatchers.Main).launch {
                homeFragment.userDelete()
            }
        }

        valueService = intent.getStringExtra("service")
        Log.d("서비스 프래그먼트", valueService.toString())
        if (valueService != null){
            changeFragment(SEND_TAG, sendFragment)
            valueService = null
        }
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

    fun changeOpnBtlFragment(index: Int){
        supportFragmentManager.setFragmentResult("requestKey"
            ,  bundleOf("bundleKey" to index))
        supportFragmentManager.beginTransaction().replace(R.id.fragment, OpenBottleFragment())
            .commit()
        currentTag = OPEN_TAG
    }

    fun changeDrawer(){
        if (!binding.drawerSetting.isDrawerOpen(Gravity.RIGHT)){
            binding.drawerSetting.openDrawer(Gravity.RIGHT)
            Log.d("슬라이드",binding.drawerSetting.toString())
        }else
            binding.drawerSetting.closeDrawer(Gravity.RIGHT)
    }

    fun logout(){
        googleSignInClient.signOut().addOnSuccessListener {
            LoginStartActivity.mAuth.signOut()
        }
    }



}