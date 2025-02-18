package tech.qingge.androiddevtoolbox.ui.fragment

import dagger.hilt.android.AndroidEntryPoint
import tech.qingge.androiddevtoolbox.R
import tech.qingge.androiddevtoolbox.data.entity.CardData
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import javax.inject.Inject


@AndroidEntryPoint
class DeviceInfoPropFragment @Inject constructor() : DeviceInfoBaseFragment() {


    override suspend fun initCardDataList(): List<CardData> {
        return mutableListOf(
            CardData(getString(R.string.prop_info), getPropInfo()),
        )
    }


    private fun getPropInfo(): MutableList<Pair<String, String>> {
        return getAllProperties().entries.map { entry -> Pair(entry.key, entry.value) }.toMutableList()
    }

    fun getAllProperties(): Map<String, String> {
        val props = mutableMapOf<String, String>()
        try {
            val process = Runtime.getRuntime().exec("getprop")
            process.inputStream.use { inputStream ->
                val reader = BufferedReader(InputStreamReader(inputStream))
                reader.useLines { lines ->
                    lines.forEach { line ->
                        val parts = line.split(":")
                        if (parts.size == 2) {
                            val key = parts[0].trim().removeSurrounding("[", "]")
                            val value = parts[1].trim().removeSurrounding("[", "]")
                            props[key] = value
                        }
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return props
    }

}