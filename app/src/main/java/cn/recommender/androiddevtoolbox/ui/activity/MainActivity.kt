package cn.recommender.androiddevtoolbox.ui.activity

import android.content.res.Configuration
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import androidx.activity.enableEdgeToEdge
import androidx.core.view.OnApplyWindowInsetsListener
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import cn.recommender.androiddevtoolbox.R
import cn.recommender.androiddevtoolbox.base.BaseActivity
import cn.recommender.androiddevtoolbox.databinding.ActivityMainBinding
import cn.recommender.androiddevtoolbox.ui.fragment.AppManagerFragment
import cn.recommender.androiddevtoolbox.ui.fragment.DeviceInfoFragment
import cn.recommender.androiddevtoolbox.util.CommonUtils

class MainActivity : BaseActivity() {

    private val TAG: String = javaClass.name

    private lateinit var binding: ActivityMainBinding

    private val fragments = listOf(AppManagerFragment, DeviceInfoFragment)

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
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

    private fun switchFragment(position: Int) {
        supportFragmentManager.beginTransaction().replace(R.id.container, fragments[position])
            .setReorderingAllowed(true)
            .commit()
    }

}