package tech.qingge.onedroid.ui.fragment

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import dagger.hilt.android.AndroidEntryPoint
import tech.qingge.onedroid.R
import tech.qingge.onedroid.base.BaseFragment
import tech.qingge.onedroid.databinding.FragmentToolsBinding
import tech.qingge.onedroid.service.FloatingWindowService
import tech.qingge.onedroid.tool.WifiPasswordViewer
import tech.qingge.onedroid.ui.activity.DecompileActivity
import tech.qingge.onedroid.ui.dialog.Dialogs
import tech.qingge.onedroid.util.CommonPermissionCallback
import tech.qingge.onedroid.util.FileUtil
import tech.qingge.onedroid.util.LogUtil
import tech.qingge.onedroid.util.ServiceUtil
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class ToolsFragment @Inject constructor() : BaseFragment<FragmentToolsBinding>(),
    View.OnClickListener {

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
                Dialogs.showMessageTips(requireActivity(), getString(R.string.not_select_file))
            }
        }

    override fun initViews() {

        listOf(
            binding.llScreenShot,
            binding.llUiInspect,
            binding.llScreenRecord,
            binding.llPickColor,
            binding.llTextOcr,
            binding.llDecompile,
            binding.llLogcat,
            binding.llTerminal,
            binding.llMockLocation,
            binding.llWifiPassword,
            binding.llNetCapture,
            binding.llFileServer
        ).forEach { it.setOnClickListener(this) }

        binding.switchFab.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                XXPermissions.with(requireContext()).permission(Permission.SYSTEM_ALERT_WINDOW)
                    .request(object : CommonPermissionCallback(requireActivity()) {
                        override fun onAllGranted() {
                            launchFWService()
                        }

                        override fun onDenied(
                            permissions: MutableList<String>,
                            doNotAskAgain: Boolean
                        ) {
                            binding.switchFab.isChecked = false
                        }
                    })
            } else {
                destroyFWService()
            }
        }

        if (ServiceUtil.isServiceRunning(
                requireContext(),
                FloatingWindowService::class.java.name
            )
        ) {
            launchFWService()
        }
    }


    override fun onClick(v: View) {
        when (v.id) {
            binding.llScreenShot.id -> onClickScreenshot()
            binding.llUiInspect.id -> onClickUiInspect()
            binding.llScreenRecord.id -> onClickScreenRecord()
            binding.llPickColor.id -> onClickPickColor()
            binding.llTextOcr.id -> onClickTextOcr()
            binding.llDecompile.id -> onClickDecompile()
            binding.llLogcat.id -> onClickLogcat()
            binding.llTerminal.id -> onClickTerminal()
            binding.llMockLocation.id -> onClickMockLocation()
            binding.llWifiPassword.id -> onClickWifiPassword()
            binding.llNetCapture.id -> onClickNetCapture()
            binding.llFileServer.id -> onClickFileServer()
        }
    }

    private fun onClickFileServer() {
        Dialogs.showMessageTips(requireActivity(), getString(R.string.developing))
    }

    private fun onClickNetCapture() {
        Dialogs.showMessageTips(requireActivity(), getString(R.string.developing))
    }

    private fun onClickWifiPassword() {
        WifiPasswordViewer.run(requireActivity(), lifecycleScope, childFragmentManager)
    }

    private fun onClickMockLocation() {
        Dialogs.showMessageTips(requireActivity(), getString(R.string.developing))
    }

    private fun onClickTerminal() {
        Dialogs.showMessageTips(requireActivity(), getString(R.string.developing))
    }

    private fun onClickLogcat() {
        Dialogs.showMessageTips(requireActivity(), getString(R.string.developing))
    }

    private fun onClickDecompile() {
        Toast.makeText(requireContext(), R.string.choose_apk_file, Toast.LENGTH_LONG).show()
        filePickerLauncher.launch(arrayOf("application/vnd.android.package-archive"))
    }

    private fun onClickTextOcr() {
        requestSystemWindowPermission()
    }

    private fun onClickPickColor() {
        requestSystemWindowPermission()
    }

    private fun onClickScreenRecord() {
        requestSystemWindowPermission()
    }

    private fun onClickUiInspect() {
        requestSystemWindowPermission()
    }

    private fun onClickScreenshot() {
        Dialogs.showMessageTips(requireActivity(), getString(R.string.developing))
    }

    private fun requestSystemWindowPermission() {
        XXPermissions.with(requireContext()).permission(Permission.SYSTEM_ALERT_WINDOW)
            .request(object : CommonPermissionCallback(requireActivity()) {
                override fun onAllGranted() {
                    launchFWService()
                }

            })
    }

    private fun launchFWService() {
        val intent = Intent(requireContext(), FloatingWindowService::class.java)
        serviceConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                floatingWindowService = (service as FloatingWindowService.Binder).getService()
                binding.switchFab.isChecked = true
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                LogUtil.d("onServiceDisconnected")
                floatingWindowService = null
                serviceConnection = null
                binding.switchFab.isChecked = false
            }
        }
        requireContext().startService(intent)
        requireContext().bindService(intent, serviceConnection!!, 0)
    }

    private fun destroyFWService() {
        serviceConnection?.let { requireContext().unbindService(it) }
        requireContext().stopService(Intent(requireContext(), FloatingWindowService::class.java))
        serviceConnection = null
        floatingWindowService = null
    }

}