package tech.qingge.onedroid.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageInfo
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.content.pm.PackageInfoCompat
import androidx.core.net.toUri
import androidx.drawerlayout.widget.DrawerLayout.SimpleDrawerListener
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tech.qingge.onedroid.R
import tech.qingge.onedroid.base.BaseActivity
import tech.qingge.onedroid.data.local.sys.SysApi
import tech.qingge.onedroid.databinding.ActivityAppDetailBinding
import tech.qingge.onedroid.databinding.NavigationDrawerHeaderAppDetailBinding
import tech.qingge.onedroid.ui.adapter.SimpleFragmentVpAdapter
import tech.qingge.onedroid.ui.dialog.Dialogs
import tech.qingge.onedroid.ui.fragment.AppDetailActivityInfoFragment
import tech.qingge.onedroid.ui.fragment.AppDetailBasicInfoFragment
import tech.qingge.onedroid.ui.fragment.AppDetailPermissionInfoFragment
import tech.qingge.onedroid.ui.fragment.AppDetailProviderInfoFragment
import tech.qingge.onedroid.ui.fragment.AppDetailReceiverInfoFragment
import tech.qingge.onedroid.ui.fragment.AppDetailServiceInfoFragment
import tech.qingge.onedroid.ui.fragment.AppDetailSignInfoFragment
import tech.qingge.onedroid.ui.fragment.FileExplorerDialogFragment
import tech.qingge.onedroid.util.LogUtil
import tech.qingge.onedroid.util.PackageManagerUtil
import java.io.File
import javax.inject.Inject


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

    private val drawerOnBackPressedCallback = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            binding.drawerLayout.closeDrawers()
        }
    }

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
                if (it.resultCode == RESULT_OK && it.data != null && it.data!!.data != null) {
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
                        File(packageInfo.applicationInfo!!.sourceDir).inputStream()
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

    override fun getNeedPaddingView(): View {
        return binding.ll
    }

    private fun initFragment() {
        val bundle = Bundle()
//        bundle.putParcelable("packageInfo", packageInfo)
        bundle.putString("packageName", packageInfo.packageName)
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

        initNavigationDrawer()

        initToolbar()

        initViewPager()

    }

    private fun initNavigationDrawer() {
        val headerBinding = NavigationDrawerHeaderAppDetailBinding.inflate(layoutInflater)
        binding.navigationView.addHeaderView(headerBinding.root)
        Glide.with(this)
            .load(packageInfo.applicationInfo!!.loadIcon(packageManager))
            .into(headerBinding.ivIcon)
        headerBinding.tvAppName.text = PackageManagerUtil.getAppName(packageInfo, this)
//        headerBinding.tvPackageName.text = packageInfo.packageName

        binding.navigationView.setNavigationItemSelectedListener {
            return@setNavigationItemSelectedListener onClickMenuItem(it)
        }


        binding.drawerLayout.addDrawerListener(object : SimpleDrawerListener() {
            override fun onDrawerOpened(drawerView: View) {
                drawerOnBackPressedCallback.isEnabled = true
            }

            override fun onDrawerClosed(drawerView: View) {
                drawerOnBackPressedCallback.isEnabled = false
            }
        })


        onBackPressedDispatcher.addCallback(this, drawerOnBackPressedCallback)
    }

    private fun onClickMenuItem(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.launch -> launchApp()
            R.id.share -> shareApp()
            R.id.save -> saveApp()
            R.id.open_in_settings -> openAppSettings()
            R.id.uninstall -> uninstallApp()
            R.id.edit_sp -> showFileExplorerDialogFragment("/data/data/${packageInfo.packageName}/shared_prefs")
            R.id.edit_db -> showFileExplorerDialogFragment("/data/data/${packageInfo.packageName}/databases")
//            R.id.explore_internal_files -> showFileExplorerDialogFragment("/data/data/${packageInfo.packageName}")
//            R.id.explore_external_files -> showFileExplorerDialogFragment("/sdcard/Android/data/${packageInfo.packageName}")
            R.id.decompile -> openDecompileActivity()
        }
        binding.drawerLayout.closeDrawers()
        return true
    }

    private fun openDecompileActivity() {
        val intent = Intent(this, DecompileActivity::class.java)
        intent.putExtra("apkPath", packageInfo.applicationInfo!!.sourceDir)
        startActivity(intent)
    }

    private fun showFileExplorerDialogFragment(path: String) {
        val fragment = FileExplorerDialogFragment()
        val bundle = Bundle()
        bundle.putString("path", path)
        fragment.arguments = bundle
        fragment.show(supportFragmentManager, "FileExplorerDialogFragment")
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
        binding.toolbar.setNavigationOnClickListener {
//            binding.drawerLayout.openDrawer(binding.navigationView)
            finish()
        }
        binding.toolbar.setOnMenuItemClickListener {
            binding.drawerLayout.openDrawer(binding.navigationView)
            return@setOnMenuItemClickListener true
//            return@setOnMenuItemClickListener onClickMenuItem(
//                it
//            )
        }

    }

    private fun uninstallApp() {
        val intent = Intent(Intent.ACTION_DELETE)
        intent.data = "package:${packageInfo.packageName}".toUri()
        startActivity(intent)
        finish()
    }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.setData(("package:" + packageInfo.packageName).toUri())
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
            File(packageInfo.applicationInfo!!.sourceDir).inputStream()
                .copyTo(apkTempFile.outputStream())
        }

        val fileUri = FileProvider.getUriForFile(this, "${packageName}.fileProvider", apkTempFile)
        LogUtil.d("fileUri:$fileUri")
        val intent = Intent(Intent.ACTION_SEND).apply {
            setType("application/vnd.android.package-archive")
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