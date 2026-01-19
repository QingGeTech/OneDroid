package tech.qingge.onedroid.ui.fragment

import android.os.Build
import dagger.hilt.android.AndroidEntryPoint
import tech.qingge.onedroid.R
import tech.qingge.onedroid.data.entity.CardData
import javax.inject.Inject


@AndroidEntryPoint
class DeviceInfoDeviceFragment @Inject constructor() : DeviceInfoBaseFragment() {

    override suspend fun initCardDataList(): List<CardData> {
        val buildInfo = getBuildInfo()

        return mutableListOf(
            CardData(
                getString(R.string.build_info), buildInfo
            )
        )
    }

    private fun getBuildInfo(): MutableList<Pair<String, String>> {
        val infos = mutableListOf(
            Pair(getString(R.string.device_device), Build.DEVICE),
            Pair(getString(R.string.device_manufacturer), Build.MANUFACTURER),
            Pair(getString(R.string.device_brand), Build.BRAND),
            Pair(getString(R.string.device_model), Build.MODEL),
            Pair(getString(R.string.device_product), Build.PRODUCT),
            Pair(getString(R.string.device_type), Build.TYPE),
            Pair(getString(R.string.device_id), Build.ID),
            Pair(getString(R.string.device_board), Build.BOARD),
            Pair(getString(R.string.device_bootloader), Build.BOOTLOADER),
            Pair(getString(R.string.device_fingerprint), Build.FINGERPRINT),
            Pair(getString(R.string.device_hardware), Build.HARDWARE),
            Pair(getString(R.string.device_host), Build.HOST),
            Pair(getString(R.string.device_tags), Build.TAGS),
            Pair(getString(R.string.device_user), Build.USER),
            Pair(getString(R.string.device_supported_abis), Build.SUPPORTED_ABIS.contentToString()),
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            infos.add(Pair(getString(R.string.device_sku), Build.SKU))
            infos.add(Pair(getString(R.string.device_odm_sku), Build.ODM_SKU))
            infos.add(Pair(getString(R.string.device_soc_manufacturer), Build.SOC_MANUFACTURER))
            infos.add(Pair(getString(R.string.device_soc_model), Build.SOC_MODEL))
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            infos.add(Pair(getString(R.string.device_serial), Build.SERIAL))
        }

        return infos
    }
}