package tech.qingge.onedroid.ui.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import dagger.hilt.android.AndroidEntryPoint
import tech.qingge.onedroid.R
import tech.qingge.onedroid.data.entity.CardData
import javax.inject.Inject


@AndroidEntryPoint
class DeviceInfoBatteryFragment @Inject constructor() : DeviceInfoBaseFragment() {


    @SuppressLint("DefaultLocale")
    override suspend fun initCardDataList(): List<CardData> {
        val batteryManager =
            requireContext().getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        val batteryInfo = mutableListOf<Pair<String, String>>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            batteryInfo.add(
                Pair(
                    getString(R.string.is_charging),
                    batteryManager.isCharging.toString()
                )
            )
        }
        batteryInfo.add(
            Pair(
                getString(R.string.battery_property_capacity),
                batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
                    .toString() + "%"
            )
        )

        batteryInfo.add(
            Pair(
                getString(R.string.battery_current_now),
                (batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW) / 1000)
                    .toString() + "mA"
            )
        )
        batteryInfo.add(
            Pair(
                getString(R.string.battery_capacity),
                getBatteryCapacity().toString() + "mAh"
            )
        )

        val batteryStatus = getBatteryStatus()
        batteryStatus?.let {
            batteryInfo.add(Pair(getString(R.string.battery_voltage), getBatteryVoltage(it)))
            batteryInfo.add(Pair(getString(R.string.battery_health), getBatteryHealth(it)))
            batteryInfo.add(
                Pair(
                    getString(R.string.battery_plugged_type),
                    getBatteryPluggedType(it)
                )
            )
            batteryInfo.add(Pair(getString(R.string.battery_technology), getBatteryTechnology(it)))
            batteryInfo.add(
                Pair(
                    getString(R.string.battery_power),
                    String.format("%.2fW", getChargingPower(it, batteryManager))
                )
            )
        }


        return listOf(CardData(requireContext().getString(R.string.battery_info), batteryInfo))
    }


    // 供电来源（AC 充电器 / USB / 无线充电）
    fun getBatteryPluggedType(batteryStatus: Intent): String {
        return when (batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)) {
            BatteryManager.BATTERY_PLUGGED_AC -> getString(R.string.battery_plugged_ac)
            BatteryManager.BATTERY_PLUGGED_USB -> getString(R.string.battery_plugged_usb)
            BatteryManager.BATTERY_PLUGGED_WIRELESS -> getString(R.string.battery_plugged_wireless)
            else -> getString(R.string.unknown)
        }
    }

    // 电池健康状态
    fun getBatteryHealth(batteryStatus: Intent): String {
        return when (batteryStatus.getIntExtra(BatteryManager.EXTRA_HEALTH, -1)) {
            BatteryManager.BATTERY_HEALTH_GOOD -> getString(R.string.battery_health_good)
            BatteryManager.BATTERY_HEALTH_OVERHEAT -> getString(R.string.battery_health_overheat)
            BatteryManager.BATTERY_HEALTH_DEAD -> getString(R.string.battery_health_dead)
            BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> getString(R.string.battery_health_over_voltage)
            BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> getString(R.string.battery_health_unspecified_failure)
            BatteryManager.BATTERY_HEALTH_COLD -> getString(R.string.battery_health_cold)
            else -> getString(R.string.unknown)
        }
    }

    // 电池技术类型（如 Li-ion）
    fun getBatteryTechnology(batteryStatus: Intent): String {
        return batteryStatus.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY)
            ?: getString(R.string.unknown)
    }


    fun getBatteryStatus(): Intent? {
        val intentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryStatus: Intent? = requireContext().registerReceiver(null, intentFilter)
        return batteryStatus
    }

    fun getBatteryVoltage(batteryStatus: Intent): String {
        val v = batteryStatus.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0)
        return if (v == 0) getString(R.string.unknown) else "${v / 1000f}V"
    }

    fun getBatteryCapacity(): Double {
        return try {
            val powerProfileClass = Class.forName("com.android.internal.os.PowerProfile")
            val constructor = powerProfileClass.getConstructor(Context::class.java)
            val powerProfile = constructor.newInstance(context)
            val method = powerProfileClass.getMethod("getBatteryCapacity")
            method.invoke(powerProfile) as Double
        } catch (e: Exception) {
            e.printStackTrace()
            -1.0  // 返回 -1.0 表示无法获取容量
        }
    }

    fun getChargingPower(batteryStatus: Intent, batteryManager: BatteryManager): Double {

        val voltage = batteryStatus.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0) / 1000f
        val current =
            batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW) / 1000f / 1000f

        // 电压单位 mV，电流单位 mA，转换为瓦特 (W)
        return voltage * current.toDouble()
    }

}

