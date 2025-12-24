package tech.qingge.androiddevtoolbox.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import com.bumptech.glide.Glide
import org.xml.sax.InputSource
import tech.qingge.androiddevtoolbox.base.BaseActivity
import tech.qingge.androiddevtoolbox.databinding.ActivityLayoutInspectBinding
import tech.qingge.androiddevtoolbox.util.LogUtil
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

class LayoutInspectActivity : BaseActivity<ActivityLayoutInspectBinding>() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initViews(intent)

    }

    @SuppressLint("SetTextI18n")
    private fun initViews(intent: Intent) {
        val filePath = intent.getStringExtra("filePath")
        val bitmap = BitmapFactory.decodeFile(filePath!!)

        val uiFilePath = intent.getStringExtra("uiFilePath")!!
        val factory = DocumentBuilderFactory.newInstance()
        val builder = factory.newDocumentBuilder()
        val document = builder.parse(InputSource(File(uiFilePath).inputStream()))
        LogUtil.d(document.toString())

        binding.toolbar.setNavigationOnClickListener { finish() }
        Glide.with(this).load(bitmap).into(binding.img)



    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        initViews(intent)
    }

}