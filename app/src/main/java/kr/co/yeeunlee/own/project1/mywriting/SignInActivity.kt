package kr.co.yeeunlee.own.project1.mywriting

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kr.co.yeeunlee.own.project1.mywriting.databinding.ActivitySignInBinding

class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        val actionB = supportActionBar
        actionB?.setDisplayShowCustomEnabled(true)
        actionB?.setDisplayShowTitleEnabled(false)
        actionB?.setDisplayHomeAsUpEnabled(true)
    }
}