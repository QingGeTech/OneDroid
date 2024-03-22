package cn.recommender.androiddevtoolbox.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import cn.recommender.androiddevtoolbox.R
import cn.recommender.androiddevtoolbox.base.BaseFragment
import cn.recommender.androiddevtoolbox.databinding.FragmentDeviceInfoBinding
import cn.recommender.androiddevtoolbox.ui.adapter.DeviceInfoVpAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Singleton

@AndroidEntryPoint
@Singleton
class DeviceInfoFragment @Inject constructor(
    private val sysInfoFragment: SysInfoFragment,
    private val hardwareInfoFragment: HardwareInfoFragment
) : BaseFragment() {

    private lateinit var binding: FragmentDeviceInfoBinding

    private val titles = listOf(R.string.sys_info, R.string.hardware_info)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentDeviceInfoBinding.inflate(layoutInflater, container, false)

        initViewPager()

        return binding.root
    }

    private fun initViewPager() {
        binding.vp.adapter = DeviceInfoVpAdapter(
            listOf(sysInfoFragment, hardwareInfoFragment), childFragmentManager, lifecycle
        )
        TabLayoutMediator(binding.tabLayout, binding.vp) { tab, position ->
            tab.text = getString(titles[position])
        }.attach()
    }


}