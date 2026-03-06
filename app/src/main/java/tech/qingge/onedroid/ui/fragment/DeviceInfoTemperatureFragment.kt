package tech.qingge.onedroid.ui.fragment

import dagger.hilt.android.AndroidEntryPoint
import tech.qingge.onedroid.R
import tech.qingge.onedroid.data.entity.CardData
import java.io.File
import javax.inject.Inject


@AndroidEntryPoint
class DeviceInfoTemperatureFragment @Inject constructor() : DeviceInfoBaseFragment() {


    private fun getTemperatureInfo(): MutableList<Pair<String, String>> {

        // 这种需要POWER权限，只有系统应用可以获取到
//        var cpuTemperature = getString(R.string.temperature_unknown)
//        var gpuTemperature = getString(R.string.temperature_unknown)
//        var skinTemperature = getString(R.string.temperature_unknown)
//        var batteryTemperature = getString(R.string.temperature_unknown)
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            val manager =
//                requireContext().getSystemService(Context.HARDWARE_PROPERTIES_SERVICE) as? HardwarePropertiesManager
//            cpuTemperature = manager?.getDeviceTemperatures(
//                HardwarePropertiesManager.DEVICE_TEMPERATURE_CPU,
//                HardwarePropertiesManager.TEMPERATURE_CURRENT
//            ).toString()
//
//            gpuTemperature = manager?.getDeviceTemperatures(
//                HardwarePropertiesManager.DEVICE_TEMPERATURE_GPU,
//                HardwarePropertiesManager.TEMPERATURE_CURRENT
//            ).toString()
//
//            skinTemperature = manager?.getDeviceTemperatures(
//                HardwarePropertiesManager.DEVICE_TEMPERATURE_SKIN,
//                HardwarePropertiesManager.TEMPERATURE_CURRENT
//            ).toString()
//
//            batteryTemperature = manager?.getDeviceTemperatures(
//                HardwarePropertiesManager.DEVICE_TEMPERATURE_BATTERY,
//                HardwarePropertiesManager.TEMPERATURE_CURRENT
//            ).toString()
//
//        }
//
//        return mutableListOf(
//            Pair(getString(R.string.temperature_cpu), cpuTemperature),
//            Pair(getString(R.string.temperature_gpu), gpuTemperature),
//            Pair(getString(R.string.temperature_skin), skinTemperature),
//            Pair(getString(R.string.temperature_battery), batteryTemperature)
//        )

        return File("/sys/class/thermal/").listFiles()
            ?.filter { dir -> dir.name.startsWith("thermal_zone") }
            ?.map { zoneDir -> getTemp(zoneDir) }
            ?.filter { p -> p.first.isNotEmpty() }
            ?.toMutableList()
            ?: mutableListOf()

    }

    private fun getTemp(zoneDir: File): Pair<String, String> {
        try {
            return Pair(
                File(zoneDir, "type").readText(),
                (File(zoneDir, "temp").readText().replace("\n", "")
                    .toInt() / 1000.0).toString() + "℃"
            )
        } catch (e: Exception) {
            return Pair("", "")
        }

    }

    override suspend fun initCardDataList(): List<CardData> {
        return mutableListOf(
            CardData(getString(R.string.temperature_info), getTemperatureInfo())
        )
    }

}