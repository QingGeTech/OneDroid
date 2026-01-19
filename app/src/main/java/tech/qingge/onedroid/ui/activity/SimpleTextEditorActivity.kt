package tech.qingge.onedroid.ui.activity

import android.os.Bundle
import dagger.hilt.android.AndroidEntryPoint
import tech.qingge.onedroid.R
import tech.qingge.onedroid.base.BaseActivity
import tech.qingge.onedroid.databinding.ActivitySimpleTextEditorBinding
import tech.qingge.onedroid.ui.dialog.Dialogs
import tech.qingge.onedroid.util.RootUtil


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
        RootUtil.getRemoteFs(this) { remoteFs ->
            val extendedFile = remoteFs.getFile(filePath)
            val bytes = extendedFile.newInputStream().readBytes()
            if (bytes.size > 20 * 1024) {
                Dialogs.showTwoButtonTips(
                    this@SimpleTextEditorActivity,
                    getString(R.string.file_too_large),
                    false
                ) { _, _ ->
                    binding.et.setText(bytes.decodeToString())
                }
            } else {
                binding.et.setText(bytes.decodeToString())
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
                .write(binding.et.text.toString().toByteArray())
            Dialogs.showMessageTips(this@SimpleTextEditorActivity, getString(R.string.save_success))
        }
    }


}