package cn.recommender.androiddevtoolbox.ui.activity

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import cn.recommender.androiddevtoolbox.R
import cn.recommender.androiddevtoolbox.base.BaseActivity
import cn.recommender.androiddevtoolbox.databinding.ActivitySimpleTextEditorBinding
import cn.recommender.androiddevtoolbox.ui.dialog.Dialogs
import cn.recommender.androiddevtoolbox.util.RootUtil
import dagger.hilt.android.AndroidEntryPoint
import de.markusressel.kodeeditor.library.extensions.dpToPx
import de.markusressel.kodehighlighter.language.markdown.MarkdownRuleBook


@AndroidEntryPoint
class SimpleTextEditorActivity : BaseActivity<ActivitySimpleTextEditorBinding>() {

    private lateinit var filePath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initData()

        initViews()

    }


    private fun initData() {
        filePath = intent.getStringExtra("filePath")!!
    }

    private fun initViews() {

        initToolbar()

        initEditor()

    }

    private fun initEditor() {
        binding.codeEditorLayout.apply {
            languageRuleBook = MarkdownRuleBook()
            lineNumberGenerator = { lines ->
                (1..lines).map { " $it " }
            }
            editable = true
            showDivider = true
            showMinimap = true
            minimapBorderWidth = 1.dpToPx(context)
            minimapBorderColor = Color.BLACK
            minimapIndicatorColor = Color.GREEN
            minimapMaxDimension = 150.dpToPx(context)
            minimapGravity = Gravity.BOTTOM or Gravity.END
        }
        RootUtil.getRemoteFs(this) { remoteFs ->
            val extendedFile = remoteFs.getFile(filePath)
            val bytes = extendedFile.newInputStream().readBytes()
            if (bytes.size > 20 * 1024) {
                Dialogs.showTwoButtonTips(
                    this@SimpleTextEditorActivity,
                    getString(R.string.file_too_large),
                    false
                ) { _, _ ->
                    binding.codeEditorLayout.text = bytes.decodeToString()
                }
            } else {
                binding.codeEditorLayout.text = bytes.decodeToString()
            }

        }
    }

    private fun initToolbar() {
        binding.toolbar.title = filePath.substring(filePath.lastIndexOf("/") + 1)
        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.save -> {
                    saveFile()
                }
            }
            true
        }
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun saveFile() {
        RootUtil.getRemoteFs(this) { remoteFs ->
            remoteFs.getFile(filePath).newOutputStream()
                .write(binding.codeEditorLayout.text.toByteArray())
            Dialogs.showMessageTips(this@SimpleTextEditorActivity, getString(R.string.save_success))
        }
    }


}