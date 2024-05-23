package cn.recommender.androiddevtoolbox.ui.fragment

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.res.ColorStateList
import android.graphics.PixelFormat
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.WindowManager.LayoutParams
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.widget.ImageView
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ServiceCompat
import androidx.core.content.ContextCompat
import androidx.core.os.ProcessCompat
import androidx.fragment.app.viewModels
import cn.recommender.androiddevtoolbox.App
import cn.recommender.androiddevtoolbox.R
import cn.recommender.androiddevtoolbox.base.BaseFragment
import cn.recommender.androiddevtoolbox.base.SimpleRvAdapter
import cn.recommender.androiddevtoolbox.data.local.sp.SpApi
import cn.recommender.androiddevtoolbox.databinding.FragmentToolsBinding
import cn.recommender.androiddevtoolbox.databinding.ItemSmallToolsBinding
import cn.recommender.androiddevtoolbox.databinding.LayoutToolsSettingBinding
import cn.recommender.androiddevtoolbox.service.MediaProjectionService
import cn.recommender.androiddevtoolbox.service.ToolsAccessibilityService
import cn.recommender.androiddevtoolbox.tool.ScreenPickColor
import cn.recommender.androiddevtoolbox.tool.ScreenPickText
import cn.recommender.androiddevtoolbox.tool.ScreenRecord
import cn.recommender.androiddevtoolbox.tool.ScrollScreenshot
import cn.recommender.androiddevtoolbox.ui.activity.PickColorActivity
import cn.recommender.androiddevtoolbox.ui.dialog.Dialogs
import cn.recommender.androiddevtoolbox.ui.view.ToolFab
import cn.recommender.androiddevtoolbox.util.BitmapUtil
import cn.recommender.androiddevtoolbox.util.DeviceUtil
import cn.recommender.androiddevtoolbox.util.IntentUtil
import cn.recommender.androiddevtoolbox.util.LogUtil
import cn.recommender.androiddevtoolbox.util.RandomUtil
import cn.recommender.androiddevtoolbox.util.ViewUtil
import cn.recommender.androiddevtoolbox.viewmodel.AppManagerViewModel
import cn.recommender.androiddevtoolbox.viewmodel.SettingsFragmentViewModel
import cn.recommender.androiddevtoolbox.viewmodel.ToolsFragmentViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.internal.WindowUtils
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.nio.file.Paths
import javax.inject.Inject
import kotlin.math.abs

@AndroidEntryPoint
class ToolsFragment @Inject constructor() : BaseFragment() {

    companion object {
        // process level
        var fab: ToolFab? = null
    }

    @Inject
    lateinit var windowManger: WindowManager

    @Inject
    lateinit var mediaProjectionManager: MediaProjectionManager

    @Inject
    lateinit var spApi: SpApi

    @Inject
    lateinit var appContext: App

    @Inject
    lateinit var screenRecord: ScreenRecord

    @Inject
    lateinit var scrollScreenshot: ScrollScreenshot

    @Inject
    lateinit var screenPickColor: ScreenPickColor

    @Inject
    lateinit var screenPickText: ScreenPickText

    private lateinit var binding: FragmentToolsBinding
    private val viewModel: ToolsFragmentViewModel by viewModels()

    private var mediaProjectionService: MediaProjectionService? = null

    private lateinit var mediaProjectionPermissionOk: (ActivityResult) -> Unit

    private val activityResultCallback = ActivityResultCallback<ActivityResult> {
        if (it.resultCode == RESULT_OK) {
            mediaProjectionPermissionOk(it)
        } else {
            Dialogs.showMessageTips(
                requireContext(), getString(R.string.permission_not_granted)
            )
        }
    }

    private val activityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(), activityResultCallback
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (fab != null) {
            fab!!.backgroundTintList = ColorStateList.valueOf(spApi.getThemeColor())
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentToolsBinding.inflate(layoutInflater, container, false)


        ToolsFragmentViewModel.curSelectedPos.observe(viewLifecycleOwner) { pos ->
            if (pos == -1) {
                removeFab()
            } else if (fab == null) {
                addFab(pos)
            } else {
                updateFab(pos)
            }
        }

        ToolsFragmentViewModel.toolItems.observe(viewLifecycleOwner) { items ->
            if (binding.rv.adapter == null) {
                setAdapter(items)
            } else {
                binding.rv.adapter!!.notifyDataSetChanged()
            }
        }

        viewModel.initData()

        return binding.root
    }

    private fun updateFab(pos: Int) {
        fab!!.setImageResource(ToolsFragmentViewModel.toolItems.value!![pos].imgResId)
    }

    private fun removeFab() {
        if (fab != null) {
            windowManger.removeView(fab)
            fab = null
        }
    }

    private fun addFab(pos: Int) {
        val v = binding.rv.getChildAt(pos)

        val layoutParams = LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                LayoutParams.TYPE_SYSTEM_ALERT
            },
            LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = v.x.toInt() + v.width / 4
            y = v.y.toInt() + v.height / 4 + binding.rv.y.toInt()
        }

        fab = ToolFab(requireContext(), null, 0, spApi.getThemeColor()).apply {
            setImageResource(ToolsFragmentViewModel.toolItems.value!![pos].imgResId)
        }
        windowManger.addView(fab, layoutParams)
        startShowFabAnim(layoutParams)
        fab!!.setListener(onDrag = { x, y ->
            layoutParams.x = x
            layoutParams.y = y
            windowManger.updateViewLayout(ToolsFragment.fab, layoutParams)
        }, onClick = {
            onClickFab()
        })

    }

    private fun startShowFabAnim(layoutParams: LayoutParams) {
        val startX = layoutParams.x
        val startY = layoutParams.y
        val endX = DeviceUtil.getScreenWidth(requireContext()) * 0.8
        val endY = DeviceUtil.getScreenHeight(requireContext()) * 0.5
        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.addUpdateListener { animation ->
            if (fab != null) {
                layoutParams.x =
                    startX + ((endX - startX) * (animation.animatedValue as Float)).toInt()
                layoutParams.y =
                    startY + ((endY - startY) * (animation.animatedValue as Float)).toInt()
                windowManger.updateViewLayout(fab, layoutParams)
            }
        }
        animator.duration = 1000
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.start()
    }

    private fun setAdapter(items: List<ToolsFragmentViewModel.ToolItem>) {
        binding.rv.adapter =
            SimpleRvAdapter(items, ItemSmallToolsBinding::inflate) { bind, item, position ->
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
                    onClickItem(item, position)
                }
            }
    }

    private fun onClickItem(item: ToolsFragmentViewModel.ToolItem, position: Int) {
        XXPermissions.with(requireContext()).permission(Permission.SYSTEM_ALERT_WINDOW)
            .request(object : OnPermissionCallback {
                override fun onGranted(p0: MutableList<String>, p1: Boolean) {

                    if (mediaProjectionService != null) {
                        viewModel.handleItemClick(item, position)
                        return
                    }
                    mediaProjectionPermissionOk = {
                        val intent = Intent(
                            requireContext(), MediaProjectionService::class.java
                        )
                        intent.putExtra("resultCode", it.resultCode)
                        intent.putExtra("data", it.data)
                        ContextCompat.startForegroundService(
                            requireContext(), intent
                        )
                        requireContext().bindService(
                            intent, object : ServiceConnection {
                                override fun onServiceConnected(
                                    name: ComponentName?, service: IBinder?
                                ) {
                                    mediaProjectionService =
                                        (service as MediaProjectionService.Binder).getService()
                                }

                                override fun onServiceDisconnected(name: ComponentName?) {
                                    mediaProjectionService = null
                                }
                            }, Context.BIND_AUTO_CREATE
                        )
                        viewModel.handleItemClick(item, position)
                    }
                    activityResultLauncher.launch(mediaProjectionManager.createScreenCaptureIntent())

                }

                override fun onDenied(
                    permissions: MutableList<String>, doNotAskAgain: Boolean
                ) {
                    Dialogs.showMessageTips(
                        requireContext(), getString(R.string.permission_not_granted)
                    )
                }
            })
    }


    override fun onResume() {
        super.onResume()
        // 同步fab与Item状态
        if (!XXPermissions.isGranted(
                requireContext(), Permission.SYSTEM_ALERT_WINDOW
            ) && fab != null
        ) {
            removeFab()
            viewModel.clearState()
        }

    }

    private fun onClickFab() {
        //activity context may destroyed . use appContext
        LogUtil.d("clickFab:${ToolsFragmentViewModel.curSelectedPos.value}")
        when (ToolsFragmentViewModel.curSelectedPos.value) {
            0 -> scrollScreenshot.start()
            1 -> screenRecord.start()
            2 -> {
                fab!!.visibility = View.GONE
                mediaProjectionService?.screenshot { _, filePath ->
                    val intent = Intent(appContext, PickColorActivity::class.java)
                    intent.putExtra("filePath", filePath)
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    appContext.startActivity(intent)
                    fab!!.visibility = View.VISIBLE
                }
            }

            3 -> screenPickText.start()
        }
    }


}