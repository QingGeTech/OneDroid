package cn.recommender.androiddevtoolbox.ui.activity

import android.app.Activity
import android.app.Application
import android.gesture.Prediction
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import cn.recommender.androiddevtoolbox.App
import cn.recommender.androiddevtoolbox.R
import cn.recommender.androiddevtoolbox.base.BaseActivity
import cn.recommender.androiddevtoolbox.databinding.ActivityMainBinding
import cn.recommender.androiddevtoolbox.ui.fragment.AppManagerFragment
import cn.recommender.androiddevtoolbox.ui.fragment.DeviceInfoFragment
import cn.recommender.androiddevtoolbox.util.CommonUtils
import com.google.android.material.color.DynamicColors
import com.google.android.material.color.DynamicColorsOptions
import com.google.android.material.color.HarmonizedColors
import com.google.android.material.color.HarmonizedColorsOptions

class MainActivity : BaseActivity() {

    private val TAG: String = javaClass.name

    private lateinit var binding: ActivityMainBinding

    private val fragments = listOf(AppManagerFragment(), DeviceInfoFragment())

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        setThemeBySp()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        switchFragment(0)
        binding.bnv.setOnItemSelectedListener {
            val position = CommonUtils.findMenuPosition(binding.bnv.menu, it)
            switchFragment(position)
            true
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.container) { v, insets ->
            val statusBarInsets = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            Log.d(TAG, "statusBarInsets:$statusBarInsets")
            v.updatePadding(v.paddingLeft, statusBarInsets.top, v.paddingRight, v.paddingBottom)
            WindowInsetsCompat.CONSUMED
        }


    }

    private fun setThemeBySp() {
        if (App.sp.getTheme() != -1){
            setTheme(App.sp.getTheme())
        }
    }

    private fun switchFragment(position: Int) {
        supportFragmentManager.beginTransaction().replace(R.id.container, fragments[position])
            .setReorderingAllowed(true)
            .commit()
    }

}