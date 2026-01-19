package tech.qingge.onedroid.tool

import android.content.Context
import android.os.Bundle
import androidx.lifecycle.LifecycleCoroutineScope
import org.w3c.dom.Element
import org.xml.sax.InputSource
import tech.qingge.onedroid.R
import tech.qingge.onedroid.ui.dialog.Dialogs
import tech.qingge.onedroid.ui.dialog.LoadingDialog
import tech.qingge.onedroid.ui.fragment.KvListDialogFragment
import java.io.Serializable
import java.io.StringReader
import javax.xml.parsers.DocumentBuilderFactory

object WifiPasswordViewer {
    fun run(
        context: Context,
        lifecycleScope: LifecycleCoroutineScope,
        supportFragmentManager: androidx.fragment.app.FragmentManager
    ) {
//        RootUtil.getRemoteFs(context) { remoteFs ->
//            val content =
//                remoteFs.getFile("/data/misc/apexdata/com.android.wifi/WifiConfigStore.xml")
//                    .readText()
//            Dialogs.showMessageTips(context, content)
//        }

        LoadingDialog.showWithTask(context, lifecycleScope, {
            val p = Runtime.getRuntime().exec(
                arrayOf(
                    "su",
                    "-c",
                    "cat /data/misc/apexdata/com.android.wifi/WifiConfigStore.xml"
                )
            )
            val content = p.inputStream.bufferedReader().readText()
            parseWifiConfigWithDom(content)
        }, false, onSuccess = {
            val kvListDialogFragment = KvListDialogFragment()
            val bundle = Bundle()
            bundle.putSerializable("kvList", it as Serializable)
            kvListDialogFragment.arguments = bundle
            kvListDialogFragment.show(supportFragmentManager, "KvListDialogFragment")
        }, onFail = {
            Dialogs.showMessageTips(context, context.getString(R.string.fail))
        })


    }

    private fun parseWifiConfigWithDom(xmlString: String): List<Pair<String, String>> {
        val factory = DocumentBuilderFactory.newInstance()
        val builder = factory.newDocumentBuilder()
        val document = builder.parse(InputSource(StringReader(xmlString)))
        val wifiList = mutableListOf<Pair<String, String>>()

        // 获取所有 Network 节点
        val networkList = document.getElementsByTagName("Network")
        for (i in 0 until networkList.length) {
            val network = networkList.item(i) as Element
            val wifiConfig = network.getElementsByTagName("WifiConfiguration").item(0) as Element
            val strings = wifiConfig.getElementsByTagName("string")
            val nulls = wifiConfig.getElementsByTagName("null")

            var ssid: String? = null
            var preSharedKey: String? = null

            // 解析 <string> 标签
            for (j in 0 until strings.length) {
                val stringElement = strings.item(j) as Element
                val name = stringElement.getAttribute("name")
                val value = stringElement.textContent
                when (name) {
                    "SSID" -> ssid = value
                    "PreSharedKey" -> preSharedKey = value
                }
            }

            // 解析 <null> 标签
            for (j in 0 until nulls.length) {
                val nullElement = nulls.item(j) as Element
                if (nullElement.getAttribute("name") == "PreSharedKey") {
                    preSharedKey = null
                }
            }

            if (ssid != null) {
                wifiList.add(
                    Pair(
                        ssid.removeSurrounding("\""),
                        preSharedKey?.removeSurrounding("\"") ?: ""
                    )
                )
            }
        }

        return wifiList
    }

}