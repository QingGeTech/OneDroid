package cn.recommender.androiddevtoolbox.ui.dialog

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Build
import android.view.WindowManager
import cn.recommender.androiddevtoolbox.R
import com.google.android.material.color.DynamicColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.dialog.MaterialDialogs

object Dialogs {
    fun showMessageTips(
        context: Context,
        msg: String,
//        isSystem: Boolean? = false,
        listener: DialogInterface.OnClickListener? = null
    ) {
        val isActivityContext = context is Activity
        val dialog =
            MaterialAlertDialogBuilder(DynamicColors.wrapContextIfAvailable(context)).setMessage(msg)
                .setNegativeButton(android.R.string.ok, listener)
                .create()
        if (!isActivityContext) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                dialog.window?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
            } else {
                dialog.window?.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT)
            }
        }
        dialog.show()
    }


}