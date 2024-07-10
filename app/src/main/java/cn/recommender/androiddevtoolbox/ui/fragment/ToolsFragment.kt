package cn.recommender.androiddevtoolbox.ui.fragment

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.annotation.DrawableRes
import cn.recommender.androiddevtoolbox.R
import cn.recommender.androiddevtoolbox.base.BaseFragment
import cn.recommender.androiddevtoolbox.base.SimpleRvAdapter
import cn.recommender.androiddevtoolbox.databinding.FragmentToolsBinding
import cn.recommender.androiddevtoolbox.databinding.ItemSmallToolsBinding
import cn.recommender.androiddevtoolbox.service.FloatingWindowService
import cn.recommender.androiddevtoolbox.util.CommonPermissionCallback
import cn.recommender.androiddevtoolbox.util.LogUtil
import cn.recommender.androiddevtoolbox.util.ServiceUtil
import cn.recommender.androiddevtoolbox.util.ViewUtil
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import dagger.hilt.android.AndroidEntryPoint
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

    override fun initViews() {
        initToolItems()
        if (ServiceUtil.isServiceRunning(
                requireContext(),
                FloatingWindowService::class.java.name
            )
        ) {
            initFloatingWindowService()
        }
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

    private fun initToolItems() {
        toolItems = listOf(
            ToolItem(
                R.drawable.ic_scroll_screenshot,
                getString(R.string.scroll_screenshot),
                false
            ),
            ToolItem(
                R.drawable.ic_screen_record,
                getString(R.string.screen_record),
                false
            ),
            ToolItem(
                R.drawable.ic_color_pick_tool,
                getString(R.string.color_pick_tool),
                false
            ),
            ToolItem(
                R.drawable.ic_text_ocr,
                getString(R.string.text_ocr),
                false
            )
            //TODO: 二维码扫描
        )
        adapter =
            SimpleRvAdapter(toolItems, ItemSmallToolsBinding::inflate) { bind, item, position ->
                bind.img.setImageResource(item.imgResId)
                bind.tv.text = item.title
                if (item.enabled) {
                    bind.root.setBackgroundResource(R.drawable.tools_bg_selected)
                } else {
                    bind.root.background = ViewUtil.getDrawableByStyledAttr(
                        requireContext(), R.attr.selectableItemBackground
                    )
                }
                bind.root.setOnClickListener { _ ->
                    if (item.enabled) {
                        onDisableItem(item, position)
                    } else {
                        if (floatingWindowService != null) {
                            val enabledPosition = floatingWindowService!!.position
                            onDisableItem(toolItems[enabledPosition], enabledPosition)
                        }
                        onEnableItem(item, position)
                    }
                }
            }

        binding.rv.adapter = adapter
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