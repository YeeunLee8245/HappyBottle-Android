package kr.co.yeeunlee.own.project1.mywriting.ui.base

import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.viewbinding.ViewBinding
import kr.co.kumoh.d134.isl.base.BaseContractView
import kr.co.yeeunlee.own.project1.mywriting.LoginStartActivity
import kr.co.yeeunlee.own.project1.mywriting.MainActivity
import kr.co.yeeunlee.own.project1.mywriting.SignInActivity
import kr.co.yeeunlee.own.project1.mywriting.ui.FirebaseViewModel
import kr.co.yeeunlee.own.project1.mywriting.utils.ErrorAlertDialog
import kr.co.yeeunlee.own.project1.mywriting.utils.LoadingDialogUtil
import kr.co.yeeunlee.own.project1.mywriting.utils.states.ActivityState

abstract class BaseActivity<VB : ViewBinding>(
    val bindingFactory: (LayoutInflater) -> VB
) : AppCompatActivity(),
    BaseContractView {

    private var _mBinding: VB? = null
    protected val mBinidng get() = _mBinding!!
    protected val mViewModel: FirebaseViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
        _mBinding = bindingFactory(layoutInflater)
        setContentView(mBinidng.root)
        // LiveData 관찰을 위한 세팅.

//        mBinidng.lifecycleOwner = this
//
//        mViewModel.apply {
//            isLoading.observe(this@BaseActivity) {
//                if (it) {
//                    LoadingDialogUtil.shared().showLoading(this@BaseActivity)
//                } else {
//                    LoadingDialogUtil.shared().hideLoading()
//                }
//            }

//            resReuslt.observe(this@BaseActivity) {
//                loadResultCode(it)
//            }

//            error.observe(this@BaseActivity) {
//                loadErrorMessage(it)
//            }
//        }

        subscribeUi()
    }

    override fun onDestroy() {
        super.onDestroy()
        _mBinding = null
    }

//    override fun loadResultCode(resResult: ResponseResult?) {
//        ResultCodeUtil.setResult(this@BaseActivity, resResult)
//    }

    override fun loadErrorMessage(e: Throwable) {
        ErrorAlertDialog.showErrorMessage(this@BaseActivity, e.message) {}
    }

    override fun onBackPressed() {
        super.onBackPressed()
        // overridePendingTransition etc..
    }

    override fun moveToScreen(screen: ActivityState) {
        var intent: Intent? = null
        when (screen) {
            ActivityState.Main -> intent = Intent(this, MainActivity::class.java)
            ActivityState.Login -> intent = Intent(this, LoginStartActivity::class.java)
            ActivityState.Signin -> intent = Intent(this, SignInActivity::class.java)
        }
        startActivity(intent)
        finish()
    }
}