package tech.qingge.androiddevtoolbox.ui.dialog

import android.R
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.os.Build
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.EditText
import com.google.android.material.color.DynamicColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder

object Dialogs {
    fun showMessageTips(
        context: Context,
        msg: String,
//        isSystem: Boolean? = false,
        cancelable: Boolean = true,
        listener: DialogInterface.OnClickListener? = null
    ) {
        val isActivityContext = context is Activity
        val dialog =
            MaterialAlertDialogBuilder(DynamicColors.wrapContextIfAvailable(context)).setMessage(msg)
                .setNegativeButton(R.string.ok, listener)
                .setCancelable(cancelable)
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

    fun showTwoButtonTips(
        context: Context,
        msg: String,
        cancelable: Boolean = true,
        cancelListener: DialogInterface.OnClickListener? = DialogInterface.OnClickListener { dialog, _ -> dialog.dismiss() },
        okListener: DialogInterface.OnClickListener,
    ) {
        val isActivityContext = context is Activity
        val dialog =
            MaterialAlertDialogBuilder(DynamicColors.wrapContextIfAvailable(context)).setMessage(msg)
                .setNegativeButton(R.string.ok, okListener)
                .setPositiveButton(R.string.cancel, cancelListener)
                .setCancelable(cancelable)
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

    fun showInputDialog(
        context: Context,
        title: String,
        hint: String,
        okText: String,
        onInputFinish: (String) -> Unit
    ) {

        val v = LayoutInflater.from(context)
            .inflate(tech.qingge.androiddevtoolbox.R.layout.view_dialog_et, null, false)
        val et = v.findViewById<EditText>(tech.qingge.androiddevtoolbox.R.id.et)
        et.hint = hint

        val dialog = MaterialAlertDialogBuilder(DynamicColors.wrapContextIfAvailable(context))
            .setTitle(title)
            .setNegativeButton(
                okText,
                DialogInterface.OnClickListener { dialog, _ -> onInputFinish(et.text.toString()) })
            .setPositiveButton(
                R.string.cancel,
                DialogInterface.OnClickListener { dialog, _ -> dialog.dismiss() })
            .setView(v)
            .setCancelable(false)
            .create()

        dialog.show()
    }

}