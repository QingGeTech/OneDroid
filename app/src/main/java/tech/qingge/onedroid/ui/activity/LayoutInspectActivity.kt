package tech.qingge.onedroid.ui.activity

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tech.qingge.onedroid.base.BaseActivity
import tech.qingge.onedroid.databinding.ActivityLayoutInspectBinding
import tech.qingge.onedroid.tool.ViewNode
import java.io.File

class LayoutInspectActivity : BaseActivity<ActivityLayoutInspectBinding>() {

    private val gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViews(intent)
    }

    private fun initViews(intent: Intent) {
        binding.toolbar.setNavigationOnClickListener { finish() }

        val screenshotPath = intent.getStringExtra("screenshotPath") ?: return
        val uiJsonPath = intent.getStringExtra("uiJsonPath") ?: return

        // 使用协程异步加载大数据
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // 1. 加载截图
                val bitmap = BitmapFactory.decodeFile(screenshotPath)

                // 2. 读取并解析 JSON
                val json = File(uiJsonPath).readText()
                val rootNode = gson.fromJson(json, ViewNode::class.java)

                withContext(Dispatchers.Main) {
                    // 3. 将数据交给 binding 中的 inspectView (需在 XML 中定义)
                    // 如果你的 XML 中 ID 是 inspectView
                    binding.inspectView.setData(bitmap, rootNode)

                    // 设置点击回调，将节点信息显示在 UI 上（例如 TextView）
                    binding.inspectView.onNodeSelectedListener = { node ->
                        showNodeDetails(node)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@LayoutInspectActivity, "解析 UI 失败", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showNodeDetails(node: ViewNode) {
        // 假设你在布局底部有一个用于显示信息的 TextView
        val detail = """
            Class: ${node.className.substringAfterLast(".")}
            ID: ${node.resourceId ?: "none"}
            Text: ${node.text ?: "none"}
            Bounds: ${node.bounds.toShortString()}
        """.trimIndent()

        binding.tvNodeInfo.text = detail
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent) // 建议加上这一行，更新 Activity 的 intent 引用
        initViews(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        // 自动清理当前的 JSON 文件（可选）
        intent.getStringExtra("uiJsonPath")?.let { File(it).delete() }
    }
}