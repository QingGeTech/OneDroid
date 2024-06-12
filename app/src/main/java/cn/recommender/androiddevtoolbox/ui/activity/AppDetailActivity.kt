package cn.recommender.androiddevtoolbox.ui.activity

import android.content.Intent
import android.content.pm.PackageInfo
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import cn.recommender.androiddevtoolbox.R
import cn.recommender.androiddevtoolbox.base.BaseActivity
import cn.recommender.androiddevtoolbox.data.local.sp.SpApi
import cn.recommender.androiddevtoolbox.data.local.sys.SysApi
import cn.recommender.androiddevtoolbox.databinding.ActivityAppDetailBinding
import cn.recommender.androiddevtoolbox.ui.adapter.SimpleFragmentVpAdapter
import cn.recommender.androiddevtoolbox.ui.fragment.AppDetailActivityInfoFragment
import cn.recommender.androiddevtoolbox.ui.fragment.AppDetailBasicInfoFragment
import cn.recommender.androiddevtoolbox.ui.fragment.AppDetailPermissionInfoFragment
import cn.recommender.androiddevtoolbox.ui.fragment.AppDetailServiceInfoFragment
import cn.recommender.androiddevtoolbox.ui.fragment.AppDetailSignInfoFragment
import cn.recommender.androiddevtoolbox.util.PackageManagerUtil
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AppDetailActivity : BaseActivity<ActivityAppDetailBinding>() {


    @Inject
    lateinit var sysApi: SysApi

    private lateinit var packageInfo: PackageInfo

    private lateinit var vpAdapter: SimpleFragmentVpAdapter

    @Inject
    lateinit var basicInfoFragment: AppDetailBasicInfoFragment

    @Inject
    lateinit var signInfoFragment: AppDetailSignInfoFragment

    @Inject
    lateinit var activityInfoFragment: AppDetailActivityInfoFragment

    @Inject
    lateinit var serviceInfoFragment: AppDetailServiceInfoFragment

    @Inject
    lateinit var permissionInfoFragment: AppDetailPermissionInfoFragment

    private val titles = listOf(
        R.string.basic_info,
        R.string.sign_info,
        R.string.activity_info,
        R.string.service_info,
        R.string.permission_info,
        R.string.receiver,
        R.string.provider,
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initData()

        initFragment()

        initViews()
    }

    private fun initFragment() {
        val bundle = Bundle()
        bundle.putParcelable("packageInfo", packageInfo)
        basicInfoFragment.arguments = bundle
        signInfoFragment.arguments = bundle
        activityInfoFragment.arguments = bundle
        serviceInfoFragment.arguments = bundle
        permissionInfoFragment.arguments = bundle
    }


    private fun initData() {
        val pkgName = intent.getStringExtra("packageName")
        packageInfo = sysApi.getPackageInfo(pkgName!!)
    }

    private fun initViews() {

        initToolbar()

        initViewPager()

    }

    private fun initViewPager() {
        vpAdapter = SimpleFragmentVpAdapter(
            listOf(
                basicInfoFragment,
                signInfoFragment,
                activityInfoFragment,
                serviceInfoFragment,
                permissionInfoFragment
            ),
            supportFragmentManager,
            lifecycle
        )
        binding.vp.adapter = vpAdapter
        TabLayoutMediator(binding.tabLayout, binding.vp) { tab, position ->
            tab.text = getString(titles[position])
        }.attach()
    }

    private fun initToolbar() {
//        binding.toolbar.logo = PackageManagerUtil.getAppIcon(packageInfo, this)
        binding.toolbar.title = PackageManagerUtil.getAppName(packageInfo, this)
//        binding.toolbar.subtitle = packageInfo.packageName
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

}