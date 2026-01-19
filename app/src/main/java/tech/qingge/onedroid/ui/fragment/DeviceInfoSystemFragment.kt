package tech.qingge.onedroid.ui.fragment

import android.os.Build
import dagger.hilt.android.AndroidEntryPoint
import tech.qingge.onedroid.R
import tech.qingge.onedroid.data.entity.CardData
import tech.qingge.onedroid.util.DateTimeUtil
import tech.qingge.onedroid.util.DeviceUtil
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject


@AndroidEntryPoint
class DeviceInfoSystemFragment @Inject constructor() : DeviceInfoBaseFragment() {

    override suspend fun initCardDataList(): List<CardData> {

        return mutableListOf(
            CardData(getString(R.string.version_info), getVersionInfo()),
            CardData(getString(R.string.other_info), getOtherInfo()),
        )
    }


    private fun getOtherInfo(): MutableList<Pair<String, String>> {
        val infos = mutableListOf(
            Pair(getString(R.string.language), Locale.getDefault().language),
            Pair(getString(R.string.timezone), TimeZone.getDefault().id),
            Pair(getString(R.string.is_root), if (DeviceUtil.isRoot()) "yes" else "no"),
            Pair(getString(R.string.uptime), DeviceUtil.getUptime())
        )

        return infos
    }

    private fun getVersionInfo(): MutableList<Pair<String, String>> {
        val infos = mutableListOf(
            Pair(getString(R.string.version_sdk_int), Build.VERSION.SDK_INT.toString()),
            Pair(getString(R.string.version_release), Build.VERSION.RELEASE),
            Pair(getString(R.string.version_codename), Build.VERSION.CODENAME),
            Pair(getString(R.string.version_incremental), Build.VERSION.INCREMENTAL),
            Pair(
                getString(R.string.device_build_time),
                DateTimeUtil.getFormattedDateTime(Build.TIME)
            ),
            Pair(getString(R.string.device_radio_version), Build.getRadioVersion()),
            Pair(getString(R.string.device_display), Build.DISPLAY),
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            infos.apply {
                add(
                    Pair(
                        getString(R.string.version_preview_sdk_int),
                        Build.VERSION.PREVIEW_SDK_INT.toString()
                    )
                )
                add(Pair(getString(R.string.version_base_os), Build.VERSION.BASE_OS))
                add(Pair(getString(R.string.version_security_patch), Build.VERSION.SECURITY_PATCH))
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            infos.apply {
                add(
                    Pair(
                        getString(R.string.version_media_performance_class),
                        Build.VERSION.MEDIA_PERFORMANCE_CLASS.toString()
                    )
                )
                add(
                    Pair(
                        getString(R.string.version_release_or_codename),
                        Build.VERSION.RELEASE_OR_CODENAME
                    )
                )
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            infos.add(
                Pair(
                    getString(R.string.version_release_or_preview_display),
                    Build.VERSION.RELEASE_OR_PREVIEW_DISPLAY
                )
            )
        }


        return infos
    }

}