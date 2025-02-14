package tech.qingge.androiddevtoolbox.ui.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DividerItemDecoration
import tech.qingge.androiddevtoolbox.R
import tech.qingge.androiddevtoolbox.base.SimpleRvAdapter
import tech.qingge.androiddevtoolbox.data.local.sp.SpApi
import tech.qingge.androiddevtoolbox.databinding.FragmentFileExplorerBinding
import tech.qingge.androiddevtoolbox.databinding.ItemFileBinding
import tech.qingge.androiddevtoolbox.databinding.ItemPathBinding
import tech.qingge.androiddevtoolbox.ui.activity.SimpleTextEditorActivity
import tech.qingge.androiddevtoolbox.ui.activity.SqliteInspectorActivity
import tech.qingge.androiddevtoolbox.util.RootUtil
import tech.qingge.androiddevtoolbox.util.ViewUtil
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.topjohnwu.superuser.nio.ExtendedFile
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

//TODO: 下滑后，pathRv固定在顶部
@AndroidEntryPoint
class FileExplorerDialogFragment @Inject constructor() : BottomSheetDialogFragment() {

//    data class PathItem(val fullPath: String, val pathName: String)
//
//    data class FileItem(val fileName: String, val path: String)

    @Inject
    lateinit var spApi: SpApi

    private lateinit var binding: FragmentFileExplorerBinding


    private lateinit var pathList: MutableList<String>
    private var files: MutableList<ExtendedFile> = mutableListOf()

    private lateinit var pathRvAdapter: SimpleRvAdapter<String, ItemPathBinding>
    private lateinit var filesRvAdapter: SimpleRvAdapter<ExtendedFile, ItemFileBinding>

    private fun initRv() {

        val path = requireArguments().getString("path")!!
        pathList = mutableListOf("/")
        pathList.addAll(path.substring(1).split("/"))

        updatePathRv()
        updateFileRv()

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateFileRv() {
        RootUtil.getRemoteFs(requireContext()) { remoteFS ->
            val dir = remoteFS.getFile(getCurrentFullPath())
            files.clear()
            files.addAll(dir.listFiles()!!.sorted())

            if (!this::filesRvAdapter.isInitialized) {
                filesRvAdapter = SimpleRvAdapter(
                    files, ItemFileBinding::inflate
                ) { itemBinding, extendedFile, _ ->
                    itemBinding.tvFileName.text = extendedFile.name
                    if (extendedFile.isDirectory) {
                        itemBinding.ivFileType.setImageResource(R.drawable.ic_folder)
                        itemBinding.tvFileName.setTextColor(
                            ViewUtil.getColorByStyledAttr(
                                requireContext(), R.attr.colorPrimary
                            )
                        )
                        itemBinding.root.setOnClickListener {
                            pathList.add(extendedFile.name)
                            updatePathRv()
                            updateFileRv()
                        }
                    } else {
                        itemBinding.ivFileType.setImageResource(R.drawable.ic_regular_file)
                        itemBinding.root.setOnClickListener {
                            if (isTextFile(extendedFile)) {
                                val intent =
                                    Intent(requireContext(), SimpleTextEditorActivity::class.java)
                                intent.putExtra("filePath", extendedFile.absolutePath)
                                requireContext().startActivity(intent)
                            } else if ("db".equals(extendedFile.name.takeLast(2), true)) {
                                val intent =
                                    Intent(context, SqliteInspectorActivity::class.java).apply {
                                        putExtra(
                                            "filePath",
                                            extendedFile.absolutePath
                                        )
                                    }
                                requireContext().startActivity(intent)
                            } else {
                                val intent = Intent().apply {
                                    setAction(requireContext().packageName + ".ACTION_VIEW")
                                    putExtra("filePath", extendedFile.absolutePath)
                                }
                                requireContext().startActivity(intent)
                            }
                        }
                        itemBinding.tvFileName.setTextColor(
                            ViewUtil.getColorByStyledAttr(
                                requireContext(), R.attr.colorOnSurface
                            )
                        )
                    }
                }
                binding.rvFile.adapter = filesRvAdapter
            } else {
                filesRvAdapter.notifyDataSetChanged()
            }


        }
    }

    private fun isTextFile(extendedFile: ExtendedFile): Boolean {
        val ext = extendedFile.name.substring(extendedFile.name.lastIndexOf(".") + 1)
        val extList = setOf(
            "txt",
            "java",
            "kt",
            "kts",
            "xml",
            "js",
            "json",
            "md",
            "gradle",
            "properties",
            "yml",
            "yaml",
            "sh",
            "c",
            "cpp",
            "h",
            "hpp",
            "go",
            "ts",
            "css",
            "html",
            "kt",
            "kts",
            "py",
            "sh",
            "c",
        )
        return extList.contains(ext)
    }

    private fun getCurrentFullPath(): String {
        if (pathList.size == 1) {
            return "/"
        }
        return "/" + pathList.subList(1, pathList.size).joinToString("/")
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updatePathRv() {
        if (!this::pathRvAdapter.isInitialized) {
            pathRvAdapter = SimpleRvAdapter(
                pathList, ItemPathBinding::inflate
            ) { itemBinding, filePath, index ->
                itemBinding.root.text = filePath
                itemBinding.root.setOnClickListener {
                    val removeCount = pathList.size - index - 1
                    for (i in 1..removeCount) {
                        pathList.removeLast()
                    }
                    updatePathRv()
                    updateFileRv()
                }
            }
            val divider = DividerItemDecoration(requireContext(), DividerItemDecoration.HORIZONTAL)
            divider.setDrawable(
                ResourcesCompat.getDrawable(
                    requireContext().resources,
                    R.drawable.ic_arrow_right,
                    requireContext().theme
                )!!
            )
            binding.rvPath.addItemDecoration(divider)
            binding.rvPath.adapter = pathRvAdapter
        } else {
            pathRvAdapter.notifyDataSetChanged()
        }
        binding.rvPath.smoothScrollToPosition(pathList.size - 1)

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentFileExplorerBinding.inflate(layoutInflater)
        (dialog as BottomSheetDialog).behavior.state = BottomSheetBehavior.STATE_COLLAPSED
        initRv()

        return binding.root
    }

}