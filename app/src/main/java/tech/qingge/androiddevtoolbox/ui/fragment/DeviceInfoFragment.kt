package tech.qingge.androiddevtoolbox.ui.fragment

import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import tech.qingge.androiddevtoolbox.R
import tech.qingge.androiddevtoolbox.base.BaseFragment
import tech.qingge.androiddevtoolbox.databinding.FragmentDeviceInfoBinding
import tech.qingge.androiddevtoolbox.ui.adapter.SimpleFragmentVpAdapter
import javax.inject.Inject

@AndroidEntryPoint
class DeviceInfoFragment @Inject constructor() : BaseFragment<FragmentDeviceInfoBinding>() {

    @Inject
    lateinit var deviceFragment: DeviceInfoDeviceFragment

    @Inject
    lateinit var systemFragment: DeviceInfoSystemFragment

    @Inject
    lateinit var networkFragment: DeviceInfoNetworkFragment

    @Inject
    lateinit var storageFragment: DeviceInfoStorageFragment

    @Inject
    lateinit var screenFragment: DeviceInfoScreenFragment

    @Inject
    lateinit var sensorFragment: DeviceInfoSensorFragment

    @Inject
    lateinit var temperatureFragment: DeviceInfoTemperatureFragment

    @Inject
    lateinit var cameraFragment: DeviceInfoCameraFragment

    @Inject
    lateinit var batteryFragment: DeviceInfoBatteryFragment

    @Inject
    lateinit var chipFragment: DeviceInfoChipFragment

    @Inject
    lateinit var locationFragment: DeviceInfoLocationFragment

    @Inject
    lateinit var propFragment: DeviceInfoPropFragment


    private val titles = listOf(
        R.string.device,
        R.string.system,
        R.string.network,
        R.string.storage,
        R.string.screen,
        R.string.sensor2,
        R.string.temperature,
        R.string.camera,
        R.string.battery,
        R.string.chip,
        R.string.location,
        R.string.prop
    )


    override fun initViews() {
        initViewPager()
    }

    private fun initViewPager() {
        binding.vp.adapter = SimpleFragmentVpAdapter(
            listOf(
                deviceFragment,
                systemFragment,
                networkFragment,
                storageFragment,
                screenFragment,
                sensorFragment,
                temperatureFragment,
                cameraFragment,
                batteryFragment,
                chipFragment,
                locationFragment,
                propFragment
            ), childFragmentManager, lifecycle
        )
        TabLayoutMediator(binding.tabLayout, binding.vp) { tab, position ->
            tab.text = getString(titles[position])
        }.attach()
    }


}