package kr.co.yeeunlee.own.project1.mywriting

import android.content.Context
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.WindowManager
import androidx.viewpager2.widget.ViewPager2
import kr.co.yeeunlee.own.project1.mywriting.databinding.ActivityInfoViewpagerBinding
import kr.co.yeeunlee.own.project1.mywriting.databinding.ActivityMainBinding

class InfoViewpagerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInfoViewpagerBinding
    private val infoArr = arrayListOf(R.drawable.info1, R.drawable.info2, R.drawable.info3,
        R.drawable.info4, R.drawable.info5, R.drawable.info6, R.drawable.info7, R.drawable.info8,
        R.drawable.info9, R.drawable.info10, R.drawable.info11, R.drawable.info12)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInfoViewpagerBinding.inflate(layoutInflater)

        val widthPix: Int = Resources.getSystem().displayMetrics.widthPixels
        val heightPix: Int = Resources.getSystem().displayMetrics.heightPixels
        val density: Float = Resources.getSystem().displayMetrics.density
        Log.d("화면 크기","$widthPix, $heightPix, $density")
        setContentView(binding.root)

        binding.viewPagerInfo.adapter = ViewPagerAdapter(infoArr)
        binding.viewPagerInfo.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.dotIndicator.setViewPager2(binding.viewPagerInfo)

    }

}