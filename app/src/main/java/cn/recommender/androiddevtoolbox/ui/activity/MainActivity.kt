package cn.recommender.androiddevtoolbox.ui.activity

import android.app.Activity
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import cn.recommender.androiddevtoolbox.R
import cn.recommender.androiddevtoolbox.base.BaseActivity
import cn.recommender.androiddevtoolbox.data.local.sp.SpApi
import cn.recommender.androiddevtoolbox.databinding.ActivityMainBinding
import cn.recommender.androiddevtoolbox.ui.fragment.AppManagerFragment
import cn.recommender.androiddevtoolbox.ui.fragment.DeviceInfoFragment
import cn.recommender.androiddevtoolbox.ui.fragment.SettingsFragment
import cn.recommender.androiddevtoolbox.ui.fragment.ToolsFragment
import cn.recommender.androiddevtoolbox.util.ColorUtil
import cn.recommender.androiddevtoolbox.util.CommonUtil
import cn.recommender.androiddevtoolbox.util.LogUtil
import cn.recommender.androiddevtoolbox.util.ViewUtil
import com.google.android.material.color.DynamicColors
import com.google.android.material.color.DynamicColors.*
import com.google.android.material.color.DynamicColorsOptions
import com.google.android.material.color.HarmonizedColors
import com.google.android.material.color.HarmonizedColorsOptions
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {

    @Inject
    lateinit var spApi: SpApi

//    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var appManagerFragment: AppManagerFragment

    @Inject
    lateinit var deviceInfoFragment: DeviceInfoFragment

    @Inject
    lateinit var toolsFragment: ToolsFragment

    @Inject
    lateinit var settingsFragment: SettingsFragment

    private val fragmentTags = listOf("AppManager", "DeviceInfo", "SmallTools", "Settings")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.bnv.setOnItemSelectedListener {
            switchFragmentByBottomNav(it.itemId)
            true
        }
        binding.bnv.selectedItemId = spApi.getLastBottomItemId()

        window.navigationBarColor = ViewUtil.getColorByStyledAttr(this, R.attr.colorSurfaceContainer)

    }


    private fun switchFragmentByBottomNav(itemId: Int) {

        val position = CommonUtil.findMenuPosition(binding.bnv.menu, itemId)

        var targetFragment: Fragment = appManagerFragment
        when (position) {
            0 -> targetFragment = appManagerFragment
            1 -> targetFragment = deviceInfoFragment
            2 -> targetFragment = toolsFragment
            3 -> targetFragment = settingsFragment
        }

        switchFragment(targetFragment, fragmentTags[position])
        spApi.setLastBottomItemId(itemId)
    }


    private fun switchFragment(fragment: Fragment, tag: String) {
        val transaction = supportFragmentManager.beginTransaction()

        supportFragmentManager.fragments.forEach {
            if (it.tag != tag) {
                transaction.hide(it)
            }
        }

        val targetFragment = supportFragmentManager.findFragmentByTag(tag)
        if (targetFragment == null) {
            transaction.add(R.id.container, fragment, tag)
        } else {
            transaction.show(targetFragment)
        }

        transaction.setReorderingAllowed(true).commit()
    }

}