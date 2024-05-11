package cn.recommender.androiddevtoolbox.ui.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import cn.recommender.androiddevtoolbox.R
import cn.recommender.androiddevtoolbox.base.BaseFragment
import cn.recommender.androiddevtoolbox.base.SimpleRvAdapter
import cn.recommender.androiddevtoolbox.databinding.FragmentToolsBinding
import cn.recommender.androiddevtoolbox.databinding.ItemSmallToolsBinding
import cn.recommender.androiddevtoolbox.databinding.LayoutToolsSettingBinding
import cn.recommender.androiddevtoolbox.tool.ScreenRecorder
import cn.recommender.androiddevtoolbox.ui.dialog.Dialogs
import cn.recommender.androiddevtoolbox.util.DeviceUtil
import cn.recommender.androiddevtoolbox.util.LogUtil
import cn.recommender.androiddevtoolbox.util.ViewUtil
import cn.recommender.androiddevtoolbox.viewmodel.SettingsFragmentViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.internal.WindowUtils
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.math.abs

@AndroidEntryPoint
class ToolsFragment @Inject constructor() : BaseFragment() {

    companion object {
        // process level
        var fab: FloatingActionButton? = null
    }

    @Inject
    lateinit var screenRecorder: ScreenRecorder

    data class Item(
        @DrawableRes var imgResId: Int,
        var title: String,
        var enabled: Boolean
    )

    private lateinit var items: List<Item>

    private lateinit var windowManger: WindowManager


    private lateinit var binding: FragmentToolsBinding
//    lateinit var settingDialog: AlertDialog

    override fun onAttach(context: Context) {
        super.onAttach(context)
        windowManger = requireContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        items = listOf(
            Item(R.drawable.ic_scroll_screenshot, getString(R.string.scroll_screenshot), false),
            Item(R.drawable.ic_screen_record, getString(R.string.screen_record), false),
            Item(R.drawable.ic_color_pick_tool, getString(R.string.color_pick_tool), false),
            Item(R.drawable.ic_text_ocr, getString(R.string.text_ocr), false)
        )
        updateItemEnableStatus()
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun updateItemEnableStatus() {
        if (fab == null) {
            items.forEachIndexed { _, item ->
                item.enabled = false
            }
        } else {
            val position = fab!!.tag
            items.forEachIndexed { index, item ->
                item.enabled = position == index
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val colorPrimary =
            ViewUtil.getColorByStyledAttr(requireContext(), android.R.attr.colorPrimary)
        val textColorPrimary =
            ViewUtil.getColorByStyledAttr(requireContext(), android.R.attr.textColorPrimary)
        binding = FragmentToolsBinding.inflate(layoutInflater, container, false)
        binding.rv.adapter =
            SimpleRvAdapter(items, ItemSmallToolsBinding::inflate) { bind, item, position ->
                bind.img.setImageResource(item.imgResId)
                bind.tv.text = item.title
                if (item.enabled) {
                    bind.tv.setTextColor(colorPrimary)
                    bind.img.setColorFilter(colorPrimary)
                } else {
                    bind.tv.setTextColor(textColorPrimary)
                    bind.img.setColorFilter(textColorPrimary)
                }
                bind.root.setOnClickListener { _ ->
                    toggleFloatingButton(item, position)
                }
            }
//        binding.toolbar.setOnMenuItemClickListener { menuItem ->
//            when (menuItem.itemId) {
//                R.id.settings -> {
//                    showSettingDialog()
//                }
//            }
//            true
//        }
        if (fab != null) {
            val position = fab!!.tag as Int
            windowManger.removeView(fab)
            fab = null
            addFabToWindow(items[position], position)
        }
        return binding.root
    }


//    private fun showSettingDialog() {
//        val settingBinding = LayoutToolsSettingBinding.inflate(layoutInflater)
//        settingBinding.switchFloatingButton.isChecked = fab != null
//        val settingDialog =
//            MaterialAlertDialogBuilder(requireContext()).setTitle(R.string.settings)
//                .setView(settingBinding.root).create()
//        settingBinding.switchFloatingButton.setOnCheckedChangeListener { _, isChecked ->
//            settingDialog.dismiss()
//            if (isChecked) {
//                showFloatingButton(Item(R.drawable.ic_tools, "") {
//                    //TODO: show SmallToolsFragment
//
//                })
//            } else {
//                windowManger.removeView(fab)
//                fab = null
//            }
//        }
//        settingDialog.show()
//    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        // 同步fab与Item状态
        if (!XXPermissions.isGranted(
                requireContext(),
                Permission.SYSTEM_ALERT_WINDOW
            ) && fab != null
        ) {
            windowManger.removeView(fab)
            fab = null
            updateItemEnableStatus()
            binding.rv.adapter?.notifyDataSetChanged()
        }

        if (fab == null) {
            updateItemEnableStatus()
            binding.rv.adapter?.notifyDataSetChanged()
        }
    }

    private fun toggleFloatingButton(item: Item, position: Int) {
        XXPermissions.with(this).permission(Permission.SYSTEM_ALERT_WINDOW)
            .request(object : OnPermissionCallback {
                @SuppressLint("ClickableViewAccessibility", "NotifyDataSetChanged")
                override fun onGranted(p0: MutableList<String>, p1: Boolean) {
                    if (item.enabled) {
                        windowManger.removeView(fab)
                        fab = null
                        updateItemEnableStatus()
                        binding.rv.adapter?.notifyDataSetChanged()
                    } else {
                        addFabToWindow(item, position)
                    }
                }

                override fun onDenied(permissions: MutableList<String>, doNotAskAgain: Boolean) {
                    Dialogs.showMessageTips(
                        requireContext(), getString(R.string.permission_not_granted)
                    )
                }
            })
    }

    @SuppressLint("ClickableViewAccessibility", "NotifyDataSetChanged")
    private fun addFabToWindow(item: Item, position: Int) {
        if (fab != null) {
            fab!!.apply {
                setImageResource(item.imgResId)
                tag = position
                updateItemEnableStatus()
                binding.rv.adapter?.notifyDataSetChanged()
            }
            return
        }

        val layoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
            },
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
        layoutParams.gravity = Gravity.TOP or Gravity.START
        layoutParams.x = (DeviceUtil.getScreenWidth(requireContext()) * 0.8).toInt()
        layoutParams.y = (DeviceUtil.getScreenHeight(requireContext()) * 0.8).toInt()

        var offsetX = 0
        var offsetY = 0
        var downX = 0
        var downY = 0

        fab = FloatingActionButton(requireContext()).apply {
            compatElevation = 0f
            elevation = 0f
            setImageResource(item.imgResId)
            scaleType = ImageView.ScaleType.FIT_CENTER

            setOnTouchListener { _, event ->
                LogUtil.d("event:${event}")
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        downX = event.rawX.toInt()
                        downY = event.rawY.toInt()
                        offsetX = event.rawX.toInt() - layoutParams.x
                        offsetY = event.rawY.toInt() - layoutParams.y
                    }

                    MotionEvent.ACTION_MOVE -> {
                        layoutParams.x = event.rawX.toInt() - offsetX
                        layoutParams.y = event.rawY.toInt() - offsetY
                        windowManger.updateViewLayout(fab, layoutParams)
                    }

                    MotionEvent.ACTION_UP -> {
                        LogUtil.d("${layoutParams.x} , ${layoutParams.y}")
                        if (abs(event.rawX.toInt() - downX) < 20 && abs(event.rawY.toInt() - downY) < 20) {
                            onClickFab(tag as Int)
                        }
                    }
                }
                true
            }
        }

        fab!!.tag = position
        windowManger.addView(fab, layoutParams)
        updateItemEnableStatus()
        binding.rv.adapter?.notifyDataSetChanged()

    }

    private fun onClickFab(position: Int) {
        LogUtil.d("clickFab:${position}")
    }


}