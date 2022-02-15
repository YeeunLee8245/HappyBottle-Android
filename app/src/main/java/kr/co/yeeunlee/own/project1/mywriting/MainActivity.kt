package kr.co.yeeunlee.own.project1.mywriting

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
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
        const val OPINION_TAG = "opinionDialog"
        val profileImgLi = arrayListOf(R.drawable.home_blue, R.drawable.home_green, R.drawable.home_mint
            , R.drawable.home_orange, R.drawable.home_pink, R.drawable.home_purple, R.drawable.home_sky
            , R.drawable.home_yellow)
    }
    private lateinit var binding:ActivityMainBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var connection: NetworkConnection
    private var valueService: String? = null
    private var mAuth = SplashActivity.mAuth
    private val db = SplashActivity.db
    private val homeFragment = HomeFragment()
    private val storageFragment = StorageFragment()
    private val sendFragment = SendFragment()
    private val tagMap = mapOf(HOME_TAG to R.color.home_background, STORAGE_TAG to R.color.storage_background
    , SEND_TAG to R.color.open_bottle_background, OPEN_TAG to R.color.open_bottle_background) // mapof는 변경 불가능(hashmap은 가능)
    var currentTag:String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        connection = NetworkConnection(applicationContext)
        connection.observe(this){ isConnected ->
            if (isConnected){
            }else{
                makeAlterDialog()
            }
        }

        googleSignInClient = GoogleSignIn.getClient(this, SplashActivity.gso)
        changeFragment(HOME_TAG, homeFragment)
        binding.btnHome.setOnClickListener { changeFragment(HOME_TAG, homeFragment)}
        binding.btnStorage.setOnClickListener { changeFragment(STORAGE_TAG, storageFragment) }
        binding.btnSend.setOnClickListener { changeFragment(SEND_TAG, sendFragment) }
        binding.btnClose.setOnClickListener { changeDrawer("close") }
        binding.btnLogout.setOnClickListener { // 로그아웃
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
        binding.btnBan.setOnClickListener { // 탈퇴
            AlertDialog.Builder(this)
                .setTitle("서버에 있는 계정의 모든 데이터가 삭제됩니다.")
                .setCancelable(false)
                .setItems(arrayOf("탈퇴하기","취소"), object : DialogInterface.OnClickListener{
                    override fun onClick(dialog: DialogInterface?, idx: Int) {
                        if (idx == 0){
                            CoroutineScope(Dispatchers.Main).launch {
                                dialog!!.dismiss()
                                Log.d("계정 삭제", SplashActivity.mAuth.currentUser.toString())
                                SplashActivity.mAuth.currentUser!!.delete()
                                userDelete()   // 데베 데이터 삭제 TODO("로딩창 넣어야할듯")
                            }
                        }else if (idx == 1)
                            dialog!!.dismiss()
                    }
                })
                .create()
                .show()
        }
        binding.switchBell.setOnCheckedChangeListener { _, isChecked ->
            val fireRepo = FirebaseRepository(this)
            if (isChecked){
                Log.d("스위치","사용")
                CoroutineScope(Dispatchers.Main).launch {
                    fireRepo.setPushAlarm(true)
                    fireRepo.setToken()
                }
            }else {
                Log.d("스위치", "미사용")
                CoroutineScope(Dispatchers.Main).launch {
                    fireRepo.setPushAlarm(false)
                }
            }
        }
        binding.btnOpinion.setOnClickListener { // 의견 보내기
            val dialog = OpinionDialogFragment(OPINION_TAG)
            this.supportFragmentManager.let { fragmentManager ->
                dialog.show(fragmentManager, "opinion")
            }
        }

        valueService = intent.getStringExtra("service")
        Log.d("서비스 프래그먼트", valueService.toString())
        if (valueService != null){
            changeFragment(SEND_TAG, sendFragment)
            valueService = null
        }

        if ((intent.getIntExtra("INFO_TAG", 0)) == LoginStartActivity.INFO_TAG)
            openInfoActivity()
    }

    override fun onDestroy() {
        connection.unregister()
        super.onDestroy()
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
            tagMap[currentTag]!!.let { binding.constraintLayoutBack.setBackgroundResource(it) }
            tran.replace(R.id.fragment, fragment)
            tran.commit()
        }
    }

    fun changeOpnBtlFragment(index: Int){
        supportFragmentManager.setFragmentResult("requestKey"
            ,  bundleOf("bundleKey" to index))
        supportFragmentManager.beginTransaction().replace(R.id.fragment, OpenBottleFragment())
            .addToBackStack(null)
            .commit()
        currentTag = OPEN_TAG
    }

    fun changeDrawer(userToken: String){
        if (!binding.drawerSetting.isDrawerOpen(Gravity.RIGHT)){
            binding.switchBell.isChecked = if (userToken == "false") false else true
            binding.drawerSetting.openDrawer(Gravity.RIGHT)
            Log.d("슬라이드",binding.drawerSetting.toString())
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

    private suspend fun userDelete() {
        val userEmail = mAuth.currentUser!!.email.toString()
        coroutineScope {
            val userName = getUserName()
            Log.d("사용자 이름", userName)
            db.collection("check").document("name")
                .update("name", FieldValue.arrayRemove(userName))
                .addOnFailureListener { Log.d("실패", "사용자 이름") }
        }.await()

        coroutineScope {
            db.collection("user").document(userEmail)
                .collection("note").get().addOnSuccessListener {
                    it.documents.forEach {
                        db.runBatch { batch ->
                            batch.delete(it.reference)
                        }.addOnFailureListener { Log.d("에러1", it.toString()) }
                    }
                }
            db.collection("user").document(userEmail)
                .collection("postbox").get().addOnSuccessListener {
                    it.documents.forEach {
                        db.runBatch { batch ->
                            batch.delete(it.reference)
                        }.addOnFailureListener { Log.d("에러2", it.toString()) }
                    }
                }
        }.await()

        coroutineScope {
            db.collection("user").document(userEmail)    // 비동기 주의
                .delete()
                .addOnCompleteListener {
                    Log.d("db삭제성공", "DocumentSnapshot successfully deleted!")
                    logout()
                }
                .addOnFailureListener { e -> Log.w("db삭제실패", "Error deleting document", e) }
        }.await()

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

    fun getUserName(): String = intent.getStringExtra(LoginStartActivity.NAME_TAG)?:"오류"
    fun getProfileImg(): Int = intent.getIntExtra(LoginStartActivity.PROFILE_IMG_TAG, 0)
}