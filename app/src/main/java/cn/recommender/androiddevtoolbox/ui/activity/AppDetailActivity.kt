package cn.recommender.androiddevtoolbox.ui.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageInfo
import android.content.pm.PackageInstaller
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.content.pm.PackageInfoCompat
import androidx.lifecycle.lifecycleScope
import cn.recommender.androiddevtoolbox.R
import cn.recommender.androiddevtoolbox.base.BaseActivity
import cn.recommender.androiddevtoolbox.data.local.sys.SysApi
import cn.recommender.androiddevtoolbox.databinding.ActivityAppDetailBinding
import cn.recommender.androiddevtoolbox.ui.adapter.SimpleFragmentVpAdapter
import cn.recommender.androiddevtoolbox.ui.dialog.Dialogs
import cn.recommender.androiddevtoolbox.ui.fragment.AppDetailActivityInfoFragment
import cn.recommender.androiddevtoolbox.ui.fragment.AppDetailBasicInfoFragment
import cn.recommender.androiddevtoolbox.ui.fragment.AppDetailPermissionInfoFragment
import cn.recommender.androiddevtoolbox.ui.fragment.AppDetailProviderInfoFragment
import cn.recommender.androiddevtoolbox.ui.fragment.AppDetailReceiverInfoFragment
import cn.recommender.androiddevtoolbox.ui.fragment.AppDetailServiceInfoFragment
import cn.recommender.androiddevtoolbox.ui.fragment.AppDetailSignInfoFragment
import cn.recommender.androiddevtoolbox.util.LogUtil
import cn.recommender.androiddevtoolbox.util.PackageManagerUtil
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext


@AndroidEntryPoint
class AppDetailActivity : BaseActivity<ActivityAppDetailBinding>() {


    @Inject
    lateinit var sysApi: SysApi

    private lateinit var packageInfo: PackageInfo

    private lateinit var vpAdapter: SimpleFragmentVpAdapter

    @Inject
    lateinit var basicInfoFragment: AppDetailBasicInfoFragment

    @Inject
    lateinit var signInfoFragment: AppDetailSignInfoFragment

    @Inject
    lateinit var activityInfoFragment: AppDetailActivityInfoFragment

    @Inject
    lateinit var serviceInfoFragment: AppDetailServiceInfoFragment

    @Inject
    lateinit var permissionInfoFragment: AppDetailPermissionInfoFragment

    @Inject
    lateinit var receiverInfoFragment: AppDetailReceiverInfoFragment

    @Inject
    lateinit var providerInfoFragment: AppDetailProviderInfoFragment

    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    private val titles = listOf(
        R.string.basic_info,
        R.string.sign_info,
        R.string.activity_info,
        R.string.service_info,
        R.string.receiver,
        R.string.provider,
        R.string.permission_info,
    )

    @SuppressLint("Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initData()

        initFragment()

        initViews()

        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK && it.data != null && it.data!!.data != null) {
                    val uri = it.data!!.data
                    LogUtil.d("uri:${uri}")
//                    var fileName = ""
//                    contentResolver.query(uri!!, null, null, null, null).use { cursor ->
//                        fileName =
//                            cursor!!.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
//                        LogUtil.d("fileName:$fileName")
//                    }
                    //TODO:能否获取到真实路径?
                    lifecycleScope.launch(Dispatchers.IO) {
                        File(packageInfo.applicationInfo.sourceDir).inputStream()
                            .copyTo(contentResolver.openOutputStream(uri!!)!!)
                        lifecycleScope.launch(Dispatchers.Main) {
                            Dialogs.showMessageTips(
                                this@AppDetailActivity,
                                getString(R.string.save_success)
                            )
                        }
                    }

                }
            }
    }

    private fun initFragment() {
        val bundle = Bundle()
        bundle.putParcelable("packageInfo", packageInfo)
        basicInfoFragment.arguments = bundle
        signInfoFragment.arguments = bundle
        activityInfoFragment.arguments = bundle
        serviceInfoFragment.arguments = bundle
        permissionInfoFragment.arguments = bundle
        receiverInfoFragment.arguments = bundle
        providerInfoFragment.arguments = bundle
    }


    private fun initData() {
        val pkgName = intent.getStringExtra("packageName")
        packageInfo = sysApi.getPackageInfo(pkgName!!)
    }

    private fun initViews() {

        initToolbar()

        initViewPager()

    }

    private fun initViewPager() {
        vpAdapter = SimpleFragmentVpAdapter(
            listOf(
                basicInfoFragment,
                signInfoFragment,
                activityInfoFragment,
                serviceInfoFragment,
                receiverInfoFragment,
                providerInfoFragment,
                permissionInfoFragment
            ),
            supportFragmentManager,
            lifecycle
        )
        binding.vp.adapter = vpAdapter
        TabLayoutMediator(binding.tabLayout, binding.vp) { tab, position ->
            tab.text = getString(titles[position])
        }.attach()
    }

    private fun initToolbar() {
//        binding.toolbar.logo = PackageManagerUtil.getAppIcon(packageInfo, this)
        binding.toolbar.title = PackageManagerUtil.getAppName(packageInfo, this)
//        binding.toolbar.subtitle = packageInfo.packageName
        binding.toolbar.setNavigationOnClickListener { finish() }

        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.launch -> launchApp()
                R.id.share -> shareApp()
                R.id.save -> saveApp()
                R.id.open_in_settings -> openAppSettings()
                R.id.uninstall -> uninstallApp()
            }
            return@setOnMenuItemClickListener true
        }

    }

    private fun uninstallApp() {
        val intent = Intent(Intent.ACTION_DELETE)
        intent.data = Uri.parse("package:${packageInfo.packageName}")
        startActivity(intent)
        lifecycleScope.launch(Dispatchers.Main){
            delay(1000)
            finish()
        }
    }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.setData(Uri.parse("package:" + packageInfo.packageName))
        startActivity(intent)
    }

    private fun saveApp() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.setType("application/vnd.android.package-archive")
        intent.putExtra(Intent.EXTRA_TITLE, packageInfo.packageName + ".apk")

        activityResultLauncher.launch(intent)
    }


    private fun shareApp() {
        val tempFileDir = File(externalCacheDir, "apk")
        if (!tempFileDir.exists()) {
            tempFileDir.mkdir()
        }
        val apkTempFile = File(
            tempFileDir,
            "${packageInfo.packageName}_${PackageInfoCompat.getLongVersionCode(packageInfo)}.apk"
        )
        if (!apkTempFile.exists()) {
            apkTempFile.createNewFile()
            File(packageInfo.applicationInfo.sourceDir).inputStream()
                .copyTo(apkTempFile.outputStream())
        }

        val fileUri = FileProvider.getUriForFile(this, "${packageName}.fileProvider", apkTempFile)
        LogUtil.d("fileUri:$fileUri")
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/vnd.android.package-archive"
            putExtra(Intent.EXTRA_STREAM, fileUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(intent)
    }

    private fun launchApp() {
        val launchIntent = packageManager.getLaunchIntentForPackage(packageInfo.packageName)
        if (launchIntent != null) {
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(launchIntent)
        } else {
            Dialogs.showMessageTips(this, getString(R.string.launch_fail))
        }
    }

}