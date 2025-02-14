package tech.qingge.androiddevtoolbox.util

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.provider.Settings
import androidx.core.net.toUri
import com.hjq.permissions.OnPermissionCallback
import tech.qingge.androiddevtoolbox.R
import tech.qingge.androiddevtoolbox.ui.dialog.Dialogs

abstract class CommonPermissionCallback(val context: Context) : OnPermissionCallback {

    abstract fun onAllGranted()

    override fun onGranted(permissions: MutableList<String>, allGranted: Boolean) {
        if (allGranted) {
            onAllGranted()
        } else {
            Dialogs.showMessageTips(
                context,
                context.getString(R.string.permission_not_granted),
                false,
                object : DialogInterface.OnClickListener{
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        openAppSettings()
                    }
                }
            )
        }
    }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.setData(("package:" + context.applicationInfo.packageName).toUri())
        context.startActivity(intent)
    }

    override fun onDenied(permissions: MutableList<String>, doNotAskAgain: Boolean) {
        Dialogs.showMessageTips(
            context,
            context.getString(R.string.permission_not_granted),
            false,
            object : DialogInterface.OnClickListener{
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    openAppSettings()
                }
            }
        )
    }
}