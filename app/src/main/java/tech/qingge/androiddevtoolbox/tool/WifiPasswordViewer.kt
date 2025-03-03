package tech.qingge.androiddevtoolbox.tool

import android.content.Context
import tech.qingge.androiddevtoolbox.ui.dialog.Dialogs

object WifiPasswordViewer {
    fun run(context: Context) {
//        RootUtil.getRemoteFs(context) { remoteFs ->
//            val content =
//                remoteFs.getFile("/data/misc/apexdata/com.android.wifi/WifiConfigStore.xml")
//                    .readText()
//            Dialogs.showMessageTips(context, content)
//        }

        val p = Runtime.getRuntime().exec(arrayOf("su","-c","cat /data/misc/apexdata/com.android.wifi/WifiConfigStore.xml"))
        val content = p.inputStream.bufferedReader().readText()
        Dialogs.showMessageTips(context, content)

    }
}