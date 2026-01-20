package tech.qingge.onedroid.ui.fragment

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.lifecycle.lifecycleScope
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import dagger.hilt.android.AndroidEntryPoint
import tech.qingge.onedroid.R
import tech.qingge.onedroid.base.BaseFragment
import tech.qingge.onedroid.base.SimpleRvAdapter
import tech.qingge.onedroid.databinding.FragmentToolsBinding
import tech.qingge.onedroid.databinding.ItemSmallToolsBinding
import tech.qingge.onedroid.service.FloatingWindowService
import tech.qingge.onedroid.tool.WifiPasswordViewer
import tech.qingge.onedroid.ui.activity.DecompileActivity
import tech.qingge.onedroid.ui.dialog.Dialogs
import tech.qingge.onedroid.util.CommonPermissionCallback
import tech.qingge.onedroid.util.FileUtil
import tech.qingge.onedroid.util.LogUtil
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class ToolsFragment @Inject constructor() : BaseFragment<FragmentToolsBinding>() {

    data class ToolItem(
        @DrawableRes var imgResId: Int,
        var title: String,
        var enabled: Boolean
    )

    private lateinit var toolItems: List<ToolItem>
    private lateinit var adapter: SimpleRvAdapter<ToolItem, ItemSmallToolsBinding>

    private var floatingWindowService: FloatingWindowService? = null
    private var serviceConnection: ServiceConnection? = null

    private val filePickerLauncher =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            uri?.let {
                val fileName = FileUtil.getFileNameFromUri(requireContext(), it) ?: "tmp.apk"
                val tmpPath = File(requireContext().cacheDir, fileName).absolutePath
                FileUtil.copyUri(
                    requireContext(),
                    it,
                    tmpPath
                )
                val intent = Intent(requireContext(), DecompileActivity::class.java)
                intent.putExtra("apkPath", tmpPath)
                startActivity(intent)
            } ?: run {
                Dialogs.showMessageTips(requireContext(), getString(R.string.not_select_file))
            }
        }

    override fun initViews() {
//        initToolItems()
//        if (ServiceUtil.isServiceRunning(
//                requireContext(),
//                FloatingWindowService::class.java.name
//            )
//        ) {
//            initFloatingWindowService()
//        }
    }

    private fun initFloatingWindowService() {
        val intent = Intent(requireContext(), FloatingWindowService::class.java)
        serviceConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                floatingWindowService = (service as FloatingWindowService.Binder).getService()
                val position = floatingWindowService!!.position
                if (position != -1) {
                    toolItems[position].enabled = true
                    adapter.notifyItemChanged(position)
                }
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                handleServiceDisconnected()
            }
        }
        requireContext().bindService(intent, serviceConnection!!, 0)
    }

    private fun handleServiceDisconnected() {
        LogUtil.d("onServiceDisconnected")
        floatingWindowService = null
        serviceConnection = null
        toolItems.forEachIndexed { index, toolItem ->
            if (toolItem.enabled) {
                toolItem.enabled = false
                adapter.notifyItemChanged(index)
            }
        }
    }

//    private fun initToolItems() {
//        toolItems = listOf(
//            ToolItem(
//                R.drawable.ic_scroll_screenshot,
//                getString(R.string.scroll_screenshot),
//                false
//            ),
//            ToolItem(
//                R.drawable.ic_layout_inspect,
//                getString(R.string.layout_inspect),
//                false
//            ),
//            ToolItem(
//                R.drawable.ic_screen_record,
//                getString(R.string.screen_record),
//                false
//            ),
//            ToolItem(
//                R.drawable.ic_color_pick_tool,
//                getString(R.string.color_pick_tool),
//                false
//            ),
//            ToolItem(
//                R.drawable.ic_text_ocr,
//                getString(R.string.text_ocr),
//                false
//            ),
//            ToolItem(
//                R.drawable.ic_wifi_password,
//                getString(R.string.wifi_password),
//                false
//            ),
//            ToolItem(
//                R.drawable.ic_decompile,
//                getString(R.string.decompile),
//                false
//            ),
//
//            ToolItem(
//                R.drawable.ic_logcat,
//                getString(R.string.logcat),
//                false
//            ),
//            ToolItem(
//                R.drawable.ic_terminal,
//                getString(R.string.terminal),
//                false
//            ),
//            ToolItem(
//                R.drawable.ic_net_capture,
//                getString(R.string.net_capture),
//                false
//            ),
//            ToolItem(
//                R.drawable.ic_mock_location,
//                getString(R.string.location_mock),
//                false
//            ),
//            ToolItem(
//                R.drawable.ic_file_server,
//                getString(R.string.file_server),
//                false
//            ),
//        )
//        adapter =
//            SimpleRvAdapter(toolItems, ItemSmallToolsBinding::inflate) { bind, item, position ->
//                bind.img.setImageResource(item.imgResId)
//                bind.tv.text = item.title
//                if (item.enabled) {
//                    bind.root.setBackgroundResource(R.drawable.tools_bg_selected)
//                } else {
//                    bind.root.background = ViewUtil.getDrawableByStyledAttr(
//                        requireContext(), R.attr.selectableItemBackground
//                    )
//                }
//                bind.root.setOnClickListener { _ ->
//                    if (position <= 3) {
//                        if (item.enabled) {
//                            onDisableItem(item, position)
//                        } else {
//                            if (floatingWindowService != null) {
//                                val enabledPosition = floatingWindowService!!.position
//                                onDisableItem(toolItems[enabledPosition], enabledPosition)
//                            }
//                            onEnableItem(item, position)
//                        }
//                    } else {
//                        onClickTool(position)
//                    }
//                }
//            }
//
//        binding.rv.adapter = adapter
//    }

    private fun onClickTool(position: Int) {
        when (position) {
            4 -> WifiPasswordViewer.run(requireActivity(), lifecycleScope, childFragmentManager)
            5 -> chooseFile()
            6 -> {}
            7 -> {}
            8 -> {}
        }
    }

    private fun chooseFile() {
        Toast.makeText(requireContext(), R.string.choose_apk_file, Toast.LENGTH_LONG).show()
        filePickerLauncher.launch(arrayOf("application/vnd.android.package-archive"))
    }

    private fun onEnableItem(item: ToolItem, position: Int) {
        XXPermissions.with(requireContext()).permission(Permission.SYSTEM_ALERT_WINDOW)
            .request(object : CommonPermissionCallback(requireContext()) {
                override fun onAllGranted() {
                    onEnableItemAfterGranted(item, position)
                }
            })
    }

    private fun onEnableItemAfterGranted(item: ToolItem, position: Int) {
        val intent = Intent(requireContext(), FloatingWindowService::class.java)
        requireContext().startService(intent)
        serviceConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                LogUtil.d("onServiceConnected")
                floatingWindowService = (service as FloatingWindowService.Binder).getService()
                onEnableItemAfterGrantedServiceCreated(item, position)
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                handleServiceDisconnected()
            }
        }
        requireContext().bindService(intent, serviceConnection!!, 0)
    }

    private fun onEnableItemAfterGrantedServiceCreated(item: ToolItem, position: Int) {
        floatingWindowService!!.showFloatingWindow(position)
        item.enabled = true
        adapter.notifyItemChanged(position)
    }

    private fun onDisableItem(item: ToolItem, position: Int) {
        item.enabled = false
        adapter.notifyItemChanged(position)
        requireContext().unbindService(serviceConnection!!)
        floatingWindowService?.closeFloatingWindow(position)
        floatingWindowService = null
        serviceConnection = null
    }

}