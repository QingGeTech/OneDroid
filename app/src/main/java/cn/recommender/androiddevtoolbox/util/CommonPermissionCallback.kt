package cn.recommender.androiddevtoolbox.util

import android.content.Context
import cn.recommender.androiddevtoolbox.R
import cn.recommender.androiddevtoolbox.ui.dialog.Dialogs
import com.hjq.permissions.OnPermissionCallback

abstract class CommonPermissionCallback(val context: Context) : OnPermissionCallback {
    override fun onDenied(permissions: MutableList<String>, doNotAskAgain: Boolean) {
        Dialogs.showMessageTips(
            context,
            context.getString(R.string.permission_not_granted)
        )
    }
}