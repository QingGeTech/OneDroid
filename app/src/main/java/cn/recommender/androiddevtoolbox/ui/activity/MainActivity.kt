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
import cn.recommender.androiddevtoolbox.data.local.sp.SpApi
import cn.recommender.androiddevtoolbox.databinding.ActivityMainBinding
import cn.recommender.androiddevtoolbox.ui.fragment.AppManagerFragment
import cn.recommender.androiddevtoolbox.ui.fragment.DeviceInfoFragment
import cn.recommender.androiddevtoolbox.util.CommonUtils
import cn.recommender.androiddevtoolbox.util.LogUtil
import com.google.android.material.color.DynamicColors
import com.google.android.material.color.DynamicColorsOptions
import com.google.android.material.color.HarmonizedColors
import com.google.android.material.color.HarmonizedColorsOptions
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    @Inject
    lateinit var spApi: SpApi

    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var appManagerFragment: AppManagerFragment

    @Inject
    lateinit var deviceInfoFragment: DeviceInfoFragment

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
            LogUtil.d("statusBarInsets:$statusBarInsets")
            v.updatePadding(v.paddingLeft, statusBarInsets.top, v.paddingRight, v.paddingBottom)
            WindowInsetsCompat.CONSUMED
        }


    }

    private fun setThemeBySp() {
        if (spApi.getTheme() != -1) {
            setTheme(spApi.getTheme())
        }
    }

    private fun switchFragment(position: Int) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, if (position == 0) appManagerFragment else deviceInfoFragment)
            .setReorderingAllowed(true).commit()
    }

}