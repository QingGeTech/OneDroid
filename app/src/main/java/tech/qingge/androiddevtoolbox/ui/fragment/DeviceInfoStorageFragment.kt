package tech.qingge.androiddevtoolbox.ui.fragment

import android.app.ActivityManager
import android.content.Context
import android.os.Environment
import android.os.StatFs
import dagger.hilt.android.AndroidEntryPoint
import tech.qingge.androiddevtoolbox.R
import tech.qingge.androiddevtoolbox.data.entity.CardData
import javax.inject.Inject


@AndroidEntryPoint
class DeviceInfoStorageFragment @Inject constructor() : DeviceInfoBaseFragment() {


    override suspend fun initCardDataList(): List<CardData> {
        return mutableListOf(
            CardData(getString(R.string.mem_info), getMemInfo()),
            CardData(getString(R.string.disk_info), getDiskInfo()),
        )
    }

    private fun getDiskInfo(): MutableList<Pair<String, String>> {

        val internalStat = StatFs(Environment.getDataDirectory().absolutePath)
        val externalStat = StatFs(Environment.getExternalStorageDirectory().absolutePath)

        val internalTotal = internalStat.totalBytes / (1024 * 1024)  // 内部存储总大小 (MB)
        val internalAvailable = internalStat.availableBytes / (1024 * 1024)  // 内部存储可用 (MB)
        val internalUsed = internalTotal - internalAvailable  // 内部存储已用 (MB)
        val internalUsedPercent =
            internalUsed.toFloat() / internalTotal.toFloat() * 100  // 内部存储已用百分比

        val externalTotal = externalStat.totalBytes / (1024 * 1024)  // 外部存储总大小 (MB)
        val externalAvailable = externalStat.availableBytes / (1024 * 1024)  // 外部存储可用 (MB)
        val externalUsed = externalTotal - externalAvailable  // 外部存储已用 (MB)
        val externalUsedPercent =
            externalUsed.toFloat() / externalTotal.toFloat() * 100  // 外部存储已用百分比

        return mutableListOf(
            Pair(getString(R.string.disk_internal_total), "${internalTotal}MB"),
            Pair(getString(R.string.disk_internal_available), "${internalAvailable}MB"),
            Pair(getString(R.string.disk_internal_used), "${internalUsed}MB"),
            Pair(getString(R.string.disk_internal_used_percent), "${internalUsedPercent}%"),
            Pair(getString(R.string.disk_external_total), "${externalTotal}MB"),
            Pair(getString(R.string.disk_external_available), "${externalAvailable}MB"),
            Pair(getString(R.string.disk_external_used), "${externalUsed}MB"),
            Pair(getString(R.string.disk_external_used_percent), "${externalUsedPercent}%"),
        )

    }

    private fun getMemInfo(): MutableList<Pair<String, String>> {
        val activityManager =
            requireContext().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)


        val totalMem = memoryInfo.totalMem / (1024 * 1024)  // 总内存 (MB)
        val availMem = memoryInfo.availMem / (1024 * 1024)  // 可用内存 (MB)
        val usedMem = totalMem - availMem  // 已用内存 (MB)
        val usedMemPercentage = usedMem.toFloat() / totalMem.toFloat() * 100  // 已用内存百分比

        return mutableListOf(
            Pair(getString(R.string.mem_total), "${totalMem}MB"),
            Pair(getString(R.string.mem_avail), "${availMem}MB"),
            Pair(getString(R.string.mem_used), "${usedMem}MB"),
            Pair(getString(R.string.mem_used_percent), "${usedMemPercentage}%")
        )


    }

}