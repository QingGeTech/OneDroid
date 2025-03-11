package tech.qingge.androiddevtoolbox.ui.activity

import android.os.Bundle
import android.util.Log
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import tech.qingge.androiddevtoolbox.BuildConfig
import tech.qingge.androiddevtoolbox.R
import tech.qingge.androiddevtoolbox.base.BaseActivity
import tech.qingge.androiddevtoolbox.data.local.sp.SpApi
import tech.qingge.androiddevtoolbox.data.net.ApiService
import tech.qingge.androiddevtoolbox.data.net.CheckUpdate
import tech.qingge.androiddevtoolbox.databinding.ActivityMainBinding
import tech.qingge.androiddevtoolbox.ui.fragment.AppManagerFragment
import tech.qingge.androiddevtoolbox.ui.fragment.DeviceInfoFragment
import tech.qingge.androiddevtoolbox.ui.fragment.SettingsFragment
import tech.qingge.androiddevtoolbox.ui.fragment.ToolsFragment
import tech.qingge.androiddevtoolbox.util.CommonUtil
import tech.qingge.androiddevtoolbox.util.DoublePressBackExit
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {

    @Inject
    lateinit var spApi: SpApi

    @Inject
    lateinit var appManagerFragment: AppManagerFragment

    @Inject
    lateinit var deviceInfoFragment: DeviceInfoFragment

    @Inject
    lateinit var toolsFragment: ToolsFragment

    @Inject
    lateinit var settingsFragment: SettingsFragment

    @Inject
    lateinit var apiService: ApiService

    private val fragmentTags = listOf("AppManager", "DeviceInfo", "SmallTools", "Settings")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.bnv.setOnItemSelectedListener {
            switchFragmentByBottomNav(it.itemId)
            true
        }
        binding.bnv.selectedItemId = spApi.getLastBottomItemId()
        onBackPressedDispatcher.addCallback(DoublePressBackExit(this))

        checkUpdate()

    }

    private fun checkUpdate() {
        //TODO: 放在Service
        lifecycleScope.launch {
            Log.d("Thread", "Thread: ${Thread.currentThread().name}")
            val commonResp =
                apiService.checkUpdate(CheckUpdate.Body(BuildConfig.VERSION_NAME))
        }
    }

    override fun updatePadding() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val statusBarInsets = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            val navigationBarInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
            v.updatePadding(
                v.paddingLeft, statusBarInsets.top, v.paddingRight, 0
            )
            binding.bnv.updatePadding(0, 0, 0, navigationBarInsets.bottom)
            WindowInsetsCompat.CONSUMED
        }
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