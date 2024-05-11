package cn.recommender.androiddevtoolbox.ui.dialog

import android.app.AlertDialog
import android.content.Context
import cn.recommender.androiddevtoolbox.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.dialog.MaterialDialogs

object Dialogs {
    fun showMessageTips(context: Context, msg: String) {
        MaterialAlertDialogBuilder(context).setMessage(msg)
            .setNegativeButton(android.R.string.ok, null)
            .show()
    }
}