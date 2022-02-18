package kr.co.yeeunlee.own.project1.mywriting

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import kr.co.yeeunlee.own.project1.mywriting.databinding.ActivityInfoViewpagerBinding

class InfoViewpagerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInfoViewpagerBinding
    private val infoArr = arrayListOf(R.drawable.info2, R.drawable.info4, R.drawable.info5,
        R.drawable.info6, R.drawable.info8, R.drawable.info9, R.drawable.info10,
        R.drawable.info11, R.drawable.info12)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInfoViewpagerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.viewPagerInfo.adapter = ViewPagerAdapter(infoArr)
        binding.viewPagerInfo.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.dotIndicator.setViewPager2(binding.viewPagerInfo)
        binding.btnInfoClose.setOnClickListener { finish() }

    }

}