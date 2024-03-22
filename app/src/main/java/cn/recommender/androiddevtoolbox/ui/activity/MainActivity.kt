package cn.recommender.androiddevtoolbox.ui.activity

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
import cn.recommender.androiddevtoolbox.ui.fragment.SmallToolsFragment
import cn.recommender.androiddevtoolbox.util.CommonUtils
import cn.recommender.androiddevtoolbox.util.LogUtil
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

    @Inject
    lateinit var smallToolsFragment: SmallToolsFragment

    @Inject
    lateinit var settingsFragment: SettingsFragment

    private lateinit var fragments: List<Fragment>


    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        fragments =
            listOf(appManagerFragment, deviceInfoFragment, smallToolsFragment, settingsFragment)
        setThemeBySp()
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
        val transaction = supportFragmentManager.beginTransaction()

        if (!fragments[position].isAdded) {
            transaction.add(R.id.container, fragments[position])
        }

        for (i in fragments.indices) {
            if (i == position) {
                transaction.show(fragments[i])
            } else {
                if (fragments[i].isAdded) {
                    transaction.hide(fragments[i])
                }
            }
        }

        transaction.setReorderingAllowed(true).commit()
    }

}