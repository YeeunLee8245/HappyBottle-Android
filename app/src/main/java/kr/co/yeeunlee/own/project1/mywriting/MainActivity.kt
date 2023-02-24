package kr.co.yeeunlee.own.project1.mywriting

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.co.yeeunlee.own.project1.mywriting.data.FirebaseRepository
import kr.co.yeeunlee.own.project1.mywriting.databinding.ActivityMainBinding
import kr.co.yeeunlee.own.project1.mywriting.ui.HomeFragment
import kr.co.yeeunlee.own.project1.mywriting.ui.SplashActivity

class MainActivity : AppCompatActivity() {
    companion object{
        const val DELETE_TAG = "DeleteUser"
        const val USER_NAME = "UserName"
        const val HOME_TAG = "HomeFragment"
        const val STORAGE_TAG = "StorageFragment"
        const val SEND_TAG = "SendFragment"
        const val OPEN_TAG = "OpnBtlFragment"
        const val FCM_URL = "https://fcm.googleapis.com"
        const val SERVER_KEY = "AAAAo5J_z7o:APA91bGez1wfMPVqb21kPXr1AUB_rr52KM3XD4pC1yES5X0-0gJlJlqZ65ANzjcSIMOnx360oDOq4PADxLAW7dbyVvW82d3rp304te3T8RuVVleef74omnENUvj9H1nW8Rr3LCFhnoSY"
        const val CONTENT_TYPE = "application/json"
        const val OPINION_TAG = "opinionDialog"
        val profileImgLi = arrayListOf(R.drawable.home_blue, R.drawable.home_green, R.drawable.home_mint
            , R.drawable.home_orange, R.drawable.home_pink, R.drawable.home_purple, R.drawable.home_sky
            , R.drawable.home_yellow)
    }
    private lateinit var binding:ActivityMainBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var connection: NetworkConnection
    private var valueService: String? = null
    private var waitTime = 0L // 백버튼 2번 시간 간격
    private val homeFragment = HomeFragment()
    private val storageFragment = StorageFragment()
    private val sendFragment = SendFragment()
    private val tagMap = mapOf(HOME_TAG to R.color.home_background, STORAGE_TAG to R.color.storage_background
    , SEND_TAG to R.color.open_bottle_background, OPEN_TAG to R.color.open_bottle_background) // mapof는 변경 불가능(hashmap은 가능)
    var currentTag:String = HOME_TAG

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        val tran = supportFragmentManager.beginTransaction()
//        tran.add(R.id.fragment, homeFragment)
//        tran.commit()
//        binding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//        connection = NetworkConnection(applicationContext)
//        connection.observe(this){ isConnected ->
//            if (isConnected){
//            }else{
//                makeAlterDialog()
//            }
//        }
//
//        googleSignInClient = GoogleSignIn.getClient(this, SplashActivity.gso)
//        binding.btnHome.setOnClickListener { changeFragment(HOME_TAG, homeFragment)}
//        binding.btnStorage.setOnClickListener { changeFragment(STORAGE_TAG, storageFragment) }
//        binding.btnSend.setOnClickListener { changeFragment(SEND_TAG, sendFragment) }
//        binding.btnClose.setOnClickListener { changeDrawer("close") }
//        binding.btnLogout.setOnClickListener { // 로그아웃
//            btnLogout()
//        }
//        binding.btnBan.setOnClickListener { // 탈퇴
//            btnDelete()
//        }
//        binding.switchBell.setOnCheckedChangeListener { _, isChecked ->
//            val fireRepo = FirebaseRepository(this)
//            if (isChecked){
//                CoroutineScope(Dispatchers.Main).launch {
//                    fireRepo.setPushAlarm(true)
//                    fireRepo.setToken()
//                }
//            }else {
//                CoroutineScope(Dispatchers.Main).launch {
//                    fireRepo.setPushAlarm(false)
//                }
//            }
//        }
//        binding.btnOpinion.setOnClickListener { // 의견 보내기
//            btnOpinion()
//        }
//
//        binding.btnLogoutSub.setOnClickListener { btnLogout() }
//        binding.btnOpinionSub.setOnClickListener { btnOpinion() }
//        binding.btnBanSub.setOnClickListener { btnDelete() }
//
//        valueService = intent.getStringExtra("service")
//        if (valueService != null){
//            changeFragment(SEND_TAG, sendFragment)
//            valueService = null
//        }
//
//        if ((intent.getIntExtra("INFO_TAG", 0)) == LoginStartActivity.INFO_TAG)
//            openInfoActivity()
    }

    override fun onDestroy() {
        connection.unregister()
        homeFragment.getListener()?.remove()
        storageFragment.getListener()?.remove()
        sendFragment.getListener()?.remove()
        super.onDestroy()
    }

    override fun onBackPressed() {
        if (currentTag == OPEN_TAG){
            var tran = supportFragmentManager.beginTransaction()
            tagMap[STORAGE_TAG]!!.let { binding.constraintLayoutBack.setBackgroundResource(it) }
            tran.replace(R.id.fragment, storageFragment)
            tran.commit()
            currentTag = STORAGE_TAG
        }
        else if (System.currentTimeMillis() - waitTime >= 1500){    // 1.5초
            waitTime = System.currentTimeMillis()
            Toast.makeText(this,"뒤로가기 버튼을 한번 더 누르면 종료됩니다.",Toast.LENGTH_SHORT).show()
        }else
            finish()
    }

    private fun changeFragment(fragmentTag: String, fragment: Fragment){
        val tran = supportFragmentManager.beginTransaction()

        if (currentTag != fragmentTag){
            currentTag = fragmentTag
            tagMap[currentTag]!!.let { binding.constraintLayoutBack.setBackgroundResource(it) }
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

    fun changeDrawer(userToken: String){
        if (!binding.drawerSetting.isDrawerOpen(Gravity.RIGHT)){
            binding.switchBell.isChecked = if (userToken == "false") false else true
            binding.drawerSetting.openDrawer(Gravity.RIGHT)
        }else
            binding.drawerSetting.closeDrawer(Gravity.RIGHT)
    }

    private fun logout(){
        googleSignInClient.signOut().addOnSuccessListener {
            SplashActivity.mAuth.signOut()
            val intentLoginStart = Intent(this, LoginStartActivity::class.java)
            startActivity(intentLoginStart)
            finish()
        }
    }

    private fun makeAlterDialog() {
        AlertDialog.Builder(this)
            .setTitle("인터넷 연결을 확인할 수 없습니다...")
            .setCancelable(false)
            .setItems(arrayOf("재접속","종료"), object : DialogInterface.OnClickListener{
                override fun onClick(dialog: DialogInterface?, idx: Int) {
                    if (idx == 0){
                        dialog!!.dismiss()
                        if (connection.value == false){ // 미연결시 다시 연결
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

    private fun openInfoActivity(){
        val infoIntent = Intent(this, InfoViewpagerActivity::class.java)
        infoIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(infoIntent)
    }

    private fun btnLogout(){
        AlertDialog.Builder(this)
            .setTitle("로그아웃하시겠습니까?")
            .setCancelable(false)
            .setItems(arrayOf("로그아웃하기","취소"), object : DialogInterface.OnClickListener{
                override fun onClick(dialog: DialogInterface?, idx: Int) {
                    dialog!!.dismiss()
                    if (idx == 0)
                        logout()
                }
            })
            .create()
            .show()
    }

    private fun btnOpinion(){
        val dialog = OpinionDialogFragment(OPINION_TAG)
        this.supportFragmentManager.let { fragmentManager ->
            dialog.show(fragmentManager, "opinion")
        }
    }

    private fun btnDelete(){
        AlertDialog.Builder(this)
            .setTitle("서버에 있는 계정의 모든 데이터가 삭제됩니다.")
            .setCancelable(false)
            .setItems(arrayOf("탈퇴하기","취소"), object : DialogInterface.OnClickListener{
                override fun onClick(dialog: DialogInterface?, idx: Int) {
                    if (idx == 0){
                        val intentSplash = Intent(this@MainActivity, SplashActivity::class.java)
                        intentSplash.putExtra(DELETE_TAG,DELETE_TAG)
                        intentSplash.putExtra(USER_NAME, getUserName())
                        intentSplash.action = Intent.ACTION_MAIN
                        intentSplash.addCategory(Intent.CATEGORY_LAUNCHER)
                        intentSplash.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        dialog!!.dismiss()
                        startActivity(intentSplash)
                        finish()
                    }else if (idx == 1)
                        dialog!!.dismiss()
                }
            })
            .create()
            .show()
    }

    fun getUserName(): String = intent.getStringExtra(LoginStartActivity.NAME_TAG)?:"오류"
    fun getProfileImg(): Int = intent.getIntExtra(LoginStartActivity.PROFILE_IMG_TAG, 0)
}