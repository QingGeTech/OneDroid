package tech.qingge.onedroid.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.webkit.MimeTypeMap
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jf.baksmali.Adaptors.ClassDefinition
import org.jf.baksmali.BaksmaliOptions
import org.jf.baksmali.formatter.BaksmaliWriter
import org.jf.dexlib2.Opcodes
import org.jf.dexlib2.dexbacked.DexBackedDexFile
import tech.qingge.onedroid.R
import tech.qingge.onedroid.base.BaseActivity
import tech.qingge.onedroid.base.SimpleRvAdapter
import tech.qingge.onedroid.databinding.ActivityDecompileBinding
import tech.qingge.onedroid.databinding.ItemFileBinding
import tech.qingge.onedroid.databinding.ItemPathBinding
import tech.qingge.onedroid.ui.dialog.Dialogs
import tech.qingge.onedroid.ui.dialog.LoadingDialog
import tech.qingge.onedroid.ui.fragment.KvListDialogFragment
import tech.qingge.onedroid.ui.fragment.SimpleImageViewerDialogFragment
import tech.qingge.onedroid.ui.fragment.SimpleListDialogFragment
import tech.qingge.onedroid.ui.fragment.SimpleTextViewerDialogFragment
import tech.qingge.onedroid.util.FileUtil
import tech.qingge.onedroid.util.LogUtil
import tech.qingge.onedroid.util.ViewUtil
import tech.qingge.onedroid.util.XmlUtil
import java.io.Serializable
import java.io.StringWriter
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import javax.inject.Inject

data class ZipNode(
    val name: String,
    val absPath: String,
    val children: MutableList<ZipNode>?,
//    val isFile: Boolean,
    val zipEntry: ZipEntry?
)

@AndroidEntryPoint
class DecompileActivity : BaseActivity<ActivityDecompileBinding>() {

    private lateinit var apkPath: String
    private lateinit var apkFile: ZipFile
    private val rootNode = ZipNode("/", "/", mutableListOf(), null)
    private var curNode = rootNode

    @Inject
    lateinit var textViewFragment: SimpleTextViewerDialogFragment

    @Inject
    lateinit var imageViewFragment: SimpleImageViewerDialogFragment

    @Inject
    lateinit var simpleListDialogFragment: SimpleListDialogFragment

    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private var savingNode: ZipNode? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        apkPath = intent.getStringExtra("apkPath")!!
        apkFile = ZipFile(apkPath)
        buildZipTree()

        initToolbar()
        initPathRv()
        initFileRv()

        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                pressBack()
            }

        })

        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == RESULT_OK && it.data != null && it.data!!.data != null && savingNode != null) {
                    val uri = it.data!!.data
                    LogUtil.d("uri:${uri}")
                    LoadingDialog.show(this)
                    lifecycleScope.launch(Dispatchers.IO) {
                        runCatching {
                            apkFile.getInputStream(savingNode!!.zipEntry)
                                .copyTo(contentResolver.openOutputStream(uri!!)!!)
                        }.onSuccess {
                            withContext(Dispatchers.Main) {
                                LoadingDialog.dismiss()
                                Dialogs.showMessageTips(
                                    this@DecompileActivity,
                                    getString(R.string.save_success)
                                )
                            }
                        }.onFailure {
                            withContext(Dispatchers.Main) {
                                LoadingDialog.dismiss()
                                Dialogs.showMessageTips(
                                    this@DecompileActivity,
                                    getString(R.string.save_fail)
                                )
                            }
                        }
                    }

                }
            }

    }

    private fun pressBack() {
        if (curNode == rootNode) {
            aboutToFinish()
            return
        }
//        val items = (binding.rvPath.adapter as SimpleRvAdapter<String, *>).items
//        val dirName = items[items.size - 2]

        resetCurNode(
            curNode.absPath.substring(
                0,
                curNode.absPath.removeSuffix("/").lastIndexOf("/") + 1
            ), rootNode
        )
        updatePathRv()
        updateFileRv()
    }

    private fun aboutToFinish() {
        //TODO: dialog
        finish()
    }

    private fun initFileRv() {
//        apkFile.entries().toList().map { ze -> ze.name }.forEach { println(it) }

        val filesRvAdapter = SimpleRvAdapter(
            curNode.children!!, ItemFileBinding::inflate
        ) { itemBinding, node, _ ->
            itemBinding.tvFileName.text = node.name
            if (node.zipEntry == null) {
                itemBinding.ivFileType.setImageResource(R.drawable.ic_folder)
                itemBinding.tvFileName.setTextColor(
                    ViewUtil.getColorByStyledAttr(
                        this, R.attr.colorPrimary
                    )
                )
                itemBinding.root.setOnClickListener {
                    resetCurNode(node.absPath, rootNode)
                    updatePathRv()
                    updateFileRv()
//                    pathList.add(extendedFile.name)
//                    updatePathRv()
//                    updateFileRv()
                }
            } else {
                itemBinding.ivFileType.setImageResource(R.drawable.ic_regular_file)
                itemBinding.root.setOnClickListener {
                    if (FileUtil.getFileExt(node.absPath) == "xml") {
                        showXml(node)
                    } else if (FileUtil.isImageFile(node.absPath)) {
                        showImage(node)
                    } else if (FileUtil.getFileExt(node.absPath) == "dex") {
                        showDexClasses(node)
                    } else {
                        showText(node)
                    }

//                    if (isTextFile(extendedFile)) {
//                        val intent =
//                            Intent(requireContext(), SimpleTextEditorActivity::class.java)
//                        intent.putExtra("filePath", extendedFile.absolutePath)
//                        requireContext().startActivity(intent)
//                    } else if ("db".equals(extendedFile.name.takeLast(2), true)) {
//                        val intent =
//                            Intent(context, SqliteInspectorActivity::class.java).apply {
//                                putExtra(
//                                    "filePath",
//                                    extendedFile.absolutePath
//                                )
//                            }
//                        requireContext().startActivity(intent)
//                    } else {
//                        val intent = Intent().apply {
//                            setAction(requireContext().packageName + ".ACTION_VIEW")
//                            putExtra("filePath", extendedFile.absolutePath)
//                        }
//                        requireContext().startActivity(intent)
//                    }
                }
                itemBinding.root.setOnLongClickListener {
                    val popupMenu = PopupMenu(this, itemBinding.root, Gravity.END)
                    popupMenu.inflate(R.menu.popup_menu_image_viewer)
                    popupMenu.setOnMenuItemClickListener {
                        saveFile(node)
                        true
                    }
                    popupMenu.show()
                    true
                }
                itemBinding.tvFileName.setTextColor(
                    ViewUtil.getColorByStyledAttr(
                        this, R.attr.colorOnSurface
                    )
                )
            }
        }
        binding.rvFile.adapter = filesRvAdapter

    }

    private fun showText(node: ZipNode) {

        loadNodeContent(node, { it.decodeToString() }) {
            val bundle = Bundle()
            bundle.putString(
                "text",
                it
            )
            //TODO:传递大对象，显示缓慢
            textViewFragment.arguments = bundle
            textViewFragment.show(supportFragmentManager, "textViewFragment")
        }

//        loadNodeString(node) { str ->
//            val bundle = Bundle()
//            bundle.putString(
//                "text",
//                str
//            )
//            //TODO:传递大对象，显示缓慢
//            textViewFragment.arguments = bundle
//            textViewFragment.show(supportFragmentManager, "textViewFragment")
//        }

    }


    private fun showDexClasses(node: ZipNode) {
        val dexBytes = apkFile.getInputStream(node.zipEntry).readBytes()
        val dexFile = DexBackedDexFile(Opcodes.getDefault(), dexBytes)
        val classes = dexFile.classes.toList()
        val classNames =
            ArrayList(classes.map { it.type.removePrefix("L").removeSuffix(";").replace("/", ".") }
                .toList())

        val bundle = Bundle()
        bundle.putStringArrayList("listData", classNames)

        simpleListDialogFragment.arguments = bundle
        simpleListDialogFragment.onClickItem = { index ->
            val c = classes[index]
            val cd = ClassDefinition(BaksmaliOptions(), c)
            val content = StringWriter()
            val writer = BaksmaliWriter(content)
            cd.writeTo(writer)
            val text = content.toString()

            val bundle = Bundle()
            bundle.putString("text", text)
            textViewFragment.arguments = bundle
            textViewFragment.show(supportFragmentManager, "textViewFragment")
        }

        simpleListDialogFragment.show(supportFragmentManager, "simpleListDialogFragment")

    }

    private fun saveFile(node: ZipNode) {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.putExtra(Intent.EXTRA_TITLE, node.name)
        intent.setType(
            MimeTypeMap.getSingleton().getMimeTypeFromExtension(FileUtil.getFileExt(node.name))
        )
        savingNode = node
        activityResultLauncher.launch(intent)
        //TODO: 没有Activity能够处理intent的情况，会抛异常, 可能没有后缀名，mimetype异常
        // 是否保存解码后的文件
    }

    private fun showImage(node: ZipNode) {

        loadNodeContent(node, { it }) {
            val bundle = Bundle()
            bundle.putByteArray("imgBytes", it)
            bundle.putString("fileName", node.name)
            imageViewFragment.arguments = bundle
            imageViewFragment.show(supportFragmentManager, "imageViewFragment")
        }

//        lifecycleScope.launch(Dispatchers.Main) {
//            LoadingDialog.show(this@DecompileActivity)
//            runCatching {
//                loadNodeBytes(node)
//            }.onSuccess {
//                LoadingDialog.dismiss()
//                val bundle = Bundle()
//                bundle.putByteArray("imgBytes", it)
//                bundle.putString("fileName", node.name)
//                imageViewFragment.arguments = bundle
//                imageViewFragment.show(supportFragmentManager, "imageViewFragment")
//            }.onFailure {
//                LogUtil.e("fail", it)
//                LoadingDialog.dismiss()
//                Dialogs.showMessageTips(this@DecompileActivity, getString(R.string.failed_to_show))
//            }
//        }
    }

    private fun showXml(node: ZipNode) {

        loadNodeContent(node, { XmlUtil.getXmlString(it) }) {
            val bundle = Bundle()
            bundle.putString("text", it)
            textViewFragment.arguments = bundle
            textViewFragment.show(supportFragmentManager, "textViewFragment")
        }

//        loadXmlString (node){string ->
//            val bundle = Bundle()
//            bundle.putString("text", string)
//            textViewFragment.arguments = bundle
//            textViewFragment.show(supportFragmentManager, "textViewFragment")
//        }

    }

//    private fun loadXmlString(node: ZipNode, onSuccess: (string: String) -> Unit) {
//
//        XmlUtil.getXmlString(loadNodeBytes(node))
//    }

    private suspend fun loadNodeBytes(node: ZipNode): ByteArray = withContext(Dispatchers.IO) {
        apkFile.getInputStream(node.zipEntry).use { inputStream ->
            inputStream.readBytes()
        }
    }

    private fun <T> loadNodeContent(
        node: ZipNode,
        block: (bytes: ByteArray) -> T,
        onSuccess: (content: T) -> Unit
    ) {
        lifecycleScope.launch(Dispatchers.Main) {
            LoadingDialog.show(this@DecompileActivity, true) {
                this.cancel()
            }
            runCatching {
                block(loadNodeBytes(node))
            }.onSuccess {
                LoadingDialog.dismiss()
                onSuccess(it)
            }.onFailure {
                LogUtil.e("fail", it)
                LoadingDialog.dismiss()
                Dialogs.showMessageTips(this@DecompileActivity, getString(R.string.failed_to_show))
            }
        }
    }

//    private fun loadNodeString(node: ZipNode, onSuccess: (string: String) -> Unit) {
//        lifecycleScope.launch(Dispatchers.Main) {
//            LoadingDialog.show(this@DecompileActivity, true) {
//                this.cancel()
//            }
//            runCatching {
//                loadNodeBytes(node).decodeToString()
//            }.onSuccess {
//                LoadingDialog.dismiss()
//                onSuccess(it)
//            }.onFailure {
//                LogUtil.e("fail", it)
//                LoadingDialog.dismiss()
//                Dialogs.showMessageTips(this@DecompileActivity, getString(R.string.failed_to_show))
//            }
//        }
//    }

    private fun buildZipTree() {
        for (entry in apkFile.entries()) {
            addToTree(rootNode, entry, entry.name)
        }
        sortTree(rootNode)
    }

    private fun sortTree(node: ZipNode) {

        node.children?.sortWith(compareBy<ZipNode> { it.zipEntry != null }.thenBy { it.name })

        node.children?.forEach { sortTree(it) }
    }

    private fun addToTree(node: ZipNode, zipEntry: ZipEntry, absPath: String) {
        if (absPath.contains("/").not()) {
            node.children!!.add(ZipNode(absPath, absPath, null, zipEntry))
            return
        }
        val ix = absPath.indexOf("/")
        val dirName = absPath.substring(0, ix)
        val subPath = absPath.substring(ix + 1)

//        val newAbsPath = if (node.absPath == "/") {
//            dirName
//        } else {
//            node.absPath + "/" + dirName
//        }
        val dirAbsPath = node.absPath + dirName + "/"
        for (child in node.children!!) {
            if (child.absPath == dirAbsPath) {
                addToTree(child, zipEntry, subPath)
                return
            }
        }
        val curNode = ZipNode(dirName, dirAbsPath, mutableListOf(), null)
        node.children.add(curNode)
        addToTree(curNode, zipEntry, subPath)
    }

    private fun initPathRv() {
        val pathRvAdapter = SimpleRvAdapter(
            getDirList(), ItemPathBinding::inflate
        ) { itemBinding, dirName, index ->
            itemBinding.root.text = dirName
            itemBinding.root.setOnClickListener {
                val newAbsPath = if (index == 0) {
                    "/"
                } else {
                    "/" + getDirList().subList(1, index + 1).joinToString("/") + "/"
                }
                resetCurNode(newAbsPath, rootNode)
                updatePathRv()
                updateFileRv()
            }
        }
        val divider = DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL)
        divider.setDrawable(
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.ic_arrow_right,
                theme
            )!!
        )
        binding.rvPath.addItemDecoration(divider)
        binding.rvPath.adapter = pathRvAdapter
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateFileRv() {
        (binding.rvFile.adapter as SimpleRvAdapter<ZipNode, *>).items = curNode.children!!
        binding.rvFile.adapter!!.notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updatePathRv() {
        (binding.rvPath.adapter as SimpleRvAdapter<String, *>).items = getDirList()
        binding.rvPath.adapter!!.notifyDataSetChanged()
    }

    private fun resetCurNode(absPath: String, node: ZipNode) {
        if (node.absPath == absPath) {
            curNode = node
            return
        }
        node.children?.forEach {
            resetCurNode(absPath, it)
        }
    }

    private fun getDirList(): List<String> {
        if (curNode == rootNode) {
            return mutableListOf("/")
        }
//        val parts = curNode.zipEntry!!.name.split("/")
//        val ix = parts.indexOf(curNode.name)
//        return parts.subList(0, ix + 1)
        val l = curNode.absPath.removeSurrounding("/").split("/").toMutableList()
        l.add(0, "/")
        return l
    }

    private fun initToolbar() {
        binding.toolbar.setNavigationOnClickListener { pressBack() }

        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.close -> aboutToFinish()
                R.id.tip -> showBottomTip()
            }
            true
        }

//        binding.toolbar.subtitle = FileUtil.getFileName(apkPath)
//        binding.toolbar.subtitle = apkPath
    }

    private fun showBottomTip() {
        val kvListDialogFragment = KvListDialogFragment()
        val kvList: MutableList<Pair<String, String?>> = mutableListOf(
            Pair(getString(R.string.apk_path), apkPath)
        )
        val bundle = Bundle()
        bundle.putSerializable("kvList", kvList as Serializable)
        kvListDialogFragment.arguments = bundle
        kvListDialogFragment.show(supportFragmentManager, "KvListDialogFragment")
    }
}