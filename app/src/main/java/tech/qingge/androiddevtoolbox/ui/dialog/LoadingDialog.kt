package tech.qingge.androiddevtoolbox.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.WindowManager
import tech.qingge.androiddevtoolbox.R

class LoadingDialog(context: Context) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 使用布局文件
        setContentView(R.layout.dialog_loading)

        // 设置窗口属性
        window?.let {
            it.setBackgroundDrawableResource(android.R.color.transparent)
            it.attributes?.let { params ->
                params.width = WindowManager.LayoutParams.WRAP_CONTENT
                params.height = WindowManager.LayoutParams.WRAP_CONTENT
            }
        }
    }

    companion object {
        private var dialog: LoadingDialog? = null

        fun show(
            context: Context,
            cancelable: Boolean? = false,
            onCancel: (() -> Unit)? = null
        ): LoadingDialog {
            dialog?.dismiss()
            dialog = LoadingDialog(context).apply {
                setCancelable(cancelable == true)
                setCanceledOnTouchOutside(cancelable == true)
                if (cancelable == true) {
                    setOnCancelListener {
                        onCancel?.invoke()
                    }
                }
                show()
            }

            return dialog!!
        }

        fun dismiss() {
            dialog?.dismiss()
            dialog = null
        }

    }
}