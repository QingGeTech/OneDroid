package tech.qingge.androiddevtoolbox.ui.fragment

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import dagger.hilt.android.AndroidEntryPoint
import tech.qingge.androiddevtoolbox.R
import tech.qingge.androiddevtoolbox.data.entity.CardData
import javax.inject.Inject


@AndroidEntryPoint
class DeviceInfoSensorFragment @Inject constructor() : DeviceInfoBaseFragment() {


    override suspend fun initCardDataList(): List<CardData> {
        val sensorManager =
            requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL)
        return sensorList.map { sensor ->
            CardData(
                sensor.stringType,
                getSensorDetails(sensor)
            )
        }
    }

    private fun getSensorDetails(sensor: Sensor): MutableList<Pair<String, String>> {
        return mutableListOf(
            Pair(getString(R.string.sensor_name), sensor.name),
            Pair(getString(R.string.sensor_vendor), sensor.vendor),
            Pair(getString(R.string.sensor_version), sensor.version.toString()),
            Pair(getString(R.string.sensor_power), sensor.power.toString()),
        )
    }


}