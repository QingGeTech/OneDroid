package tech.qingge.onedroid.ui.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import com.google.android.material.color.DynamicColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import tech.qingge.onedroid.R
import tech.qingge.onedroid.base.BaseActivity
import tech.qingge.onedroid.data.local.sp.SpApi
import tech.qingge.onedroid.databinding.ActivityLaunchBinding
import tech.qingge.onedroid.util.CommonUtil
import tech.qingge.onedroid.util.CommonUtil.openUrl
import javax.inject.Inject


@AndroidEntryPoint
class LaunchActivity : BaseActivity<ActivityLaunchBinding>() {

    @Inject
    lateinit var spApi: SpApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!spApi.isAgreeProtocol()) {
            showPrivacyTipDialog()
        } else {
            startMainActivity()
        }

    }

    private fun showPrivacyTipDialog() {
        val isChinese = CommonUtil.isChinese(resources.configuration)

        val content = if (isChinese) {
            "我们非常重视您的个人信息和隐私保护。为了向您提供优质的服务，我们可能会收集和使用您的相关信息。在使用我们的产品或服务前，请您务必仔细阅读并充分理解《用户协议》和《隐私政策》的各项条款，了解我们如何收集、使用、存储您的个人信息以及您享有的用户权利。如果您同意，请点击“同意”开始使用我们的服务。"
        } else {
            "We highly value your personal information and privacy protection. In order to provide you with high-quality services, we may collect and use your relevant information. Before using our products or services, please ensure you carefully read and fully understand User Agreement and Privacy Policy The terms and conditions, to understand how we collect, use, and store your personal information and the user rights you enjoy. If you agree, please click 'Agree' to start using our services."
        }

        val spannableString = SpannableString(content)

        val uaKey = if (isChinese) "《用户协议》" else "《User Agreement》"
        val ppKey = if (isChinese) "《隐私政策》" else "《Privacy Policy》"

        fun setSpan(target: String, action: () -> Unit) {
            val start = content.indexOf(target)
            if (start != -1) {
                spannableString.setSpan(object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        action()
                    }

                    override fun updateDrawState(ds: TextPaint) {
                        ds.color = Color.BLUE
                        ds.isUnderlineText = false
                    }
                }, start, start + target.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }

        setSpan(uaKey) { openUrl(this, "https://qingge.tech/onedroid/user-protocol.html") }
        setSpan(ppKey) { openUrl(this, "https://qingge.tech/onedroid/privacy-policy.html") }

        val dialog =
            MaterialAlertDialogBuilder(DynamicColors.wrapContextIfAvailable(this)).setMessage(
                spannableString
            )
                .setNegativeButton(
                    R.string.disagree
                ) { dialog, which -> finish() }
                .setPositiveButton(R.string.agree) { _, _ ->
                    spApi.setAgreeProtocol()
                    startMainActivity()
                }
                .setCancelable(false)
                .create()
        dialog.show()

        val messageView = dialog.findViewById<TextView>(android.R.id.message)
        messageView?.apply {
            movementMethod = LinkMovementMethod.getInstance()
            highlightColor = Color.TRANSPARENT
        }
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }


}