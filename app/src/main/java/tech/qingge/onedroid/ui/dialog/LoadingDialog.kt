package tech.qingge.onedroid.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.WindowManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tech.qingge.onedroid.R

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

        fun <T> showWithTask(
            context: Context,
            scope: CoroutineScope,
            task: () -> T,
            cancelable: Boolean? = false,
            onCancel: (() -> Unit)? = null,
            onSuccess: ((T) -> Unit)? = null,
            onFail: (() -> Unit)? = null
        ) {
            scope.launch {
                show(context, cancelable){
                    cancel()
                    onCancel?.invoke()
                }
                runCatching {
                    withContext(context = Dispatchers.IO) {
                        task()
                    }
                }.onSuccess {
                    dismiss()
                    onSuccess?.invoke(it)
                }.onFailure {
                    dismiss()
                    onFail?.invoke()
                }

            }
        }

        fun dismiss() {
            dialog?.dismiss()
            dialog = null
        }

    }
}