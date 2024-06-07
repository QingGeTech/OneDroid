package cn.recommender.androiddevtoolbox.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.recommender.androiddevtoolbox.R
import cn.recommender.androiddevtoolbox.base.BaseFragment
import cn.recommender.androiddevtoolbox.databinding.FragmentDeviceInfoBinding
import cn.recommender.androiddevtoolbox.ui.adapter.SimpleFragmentVpAdapter
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DeviceInfoFragment @Inject constructor() : BaseFragment<FragmentDeviceInfoBinding>() {

    @Inject
    lateinit var sysInfoFragment: SysInfoFragment

    @Inject
    lateinit var hardwareInfoFragment: HardwareInfoFragment


    private val titles = listOf(R.string.sys_info, R.string.hardware_info)


    override fun initViews() {
        initViewPager()
    }

    private fun initViewPager() {
        binding.vp.adapter = SimpleFragmentVpAdapter(
            listOf(sysInfoFragment, hardwareInfoFragment), childFragmentManager, lifecycle
        )
        TabLayoutMediator(binding.tabLayout, binding.vp) { tab, position ->
            tab.text = getString(titles[position])
        }.attach()
    }


}