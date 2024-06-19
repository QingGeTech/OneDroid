package cn.recommender.androiddevtoolbox.ui.fragment

import android.annotation.SuppressLint
import android.content.pm.PackageInfo
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.pm.PackageInfoCompat
import androidx.core.os.BundleCompat
import androidx.core.widget.PopupMenuCompat
import androidx.core.widget.PopupWindowCompat
import cn.recommender.androiddevtoolbox.R
import cn.recommender.androiddevtoolbox.base.BaseFragment
import cn.recommender.androiddevtoolbox.base.SimpleRvAdapter
import cn.recommender.androiddevtoolbox.data.entity.CardData
import cn.recommender.androiddevtoolbox.data.local.sp.SpApi
import cn.recommender.androiddevtoolbox.data.local.sys.SysApi
import cn.recommender.androiddevtoolbox.databinding.FragmentAppDetailBasicInfoBinding
import cn.recommender.androiddevtoolbox.databinding.FragmentSettingsBinding
import cn.recommender.androiddevtoolbox.databinding.FragmentSysInfoBinding
import cn.recommender.androiddevtoolbox.databinding.ItemAppBasicInfoBinding
import cn.recommender.androiddevtoolbox.databinding.ItemAppBasicInfoCardBinding
import cn.recommender.androiddevtoolbox.databinding.ItemAppListBinding
import cn.recommender.androiddevtoolbox.util.ClipboardUtil
import cn.recommender.androiddevtoolbox.util.DateTimeUtil
import cn.recommender.androiddevtoolbox.util.LogUtil
import cn.recommender.androiddevtoolbox.util.PackageManagerUtil
import dagger.hilt.android.AndroidEntryPoint
import java.util.Arrays
import javax.inject.Inject
import javax.inject.Singleton


@AndroidEntryPoint
class AppDetailBasicInfoFragment @Inject constructor() :
    BaseFragment<FragmentAppDetailBasicInfoBinding>() {

    private lateinit var cardDataList: List<CardData>

    private lateinit var packageInfo: PackageInfo

    @Inject
    lateinit var sysApi: SysApi

    override fun initViews() {
//        packageInfo =
//            BundleCompat.getParcelable(requireArguments(), "packageInfo", PackageInfo::class.java)!!

        val packageName = requireArguments().getString("packageName")
        packageInfo = sysApi.getPackageInfo(packageName!!)

        initData()
        initRv()
    }

    private fun initData() {
        val basicInfoList = listOf(
            Pair(
                getString(R.string.key_app_name),
                PackageManagerUtil.getAppName(packageInfo, requireContext())
            ),
            Pair(getString(R.string.package_name), packageInfo.packageName),
            Pair(getString(R.string.version_name), packageInfo.versionName),
            Pair(
                getString(R.string.version_code),
                PackageInfoCompat.getLongVersionCode(packageInfo).toString()
            ),
            Pair(
                getString(R.string.first_install_time),
                DateTimeUtil.getFormattedDateTime(packageInfo.firstInstallTime)
            ),
            Pair(
                getString(R.string.last_update_time),
                DateTimeUtil.getFormattedDateTime(packageInfo.lastUpdateTime)
            ),

            Pair(
                getString(R.string.compile_sdk_version),
                PackageManagerUtil.getCompileSdkVersion(packageInfo, requireContext())
            ),
            Pair(
                getString(R.string.min_sdk_version),
                PackageManagerUtil.getMinSdkVersion(packageInfo, requireContext())
            ),
            Pair(
                getString(R.string.target_sdk_version),
                packageInfo.applicationInfo.targetSdkVersion.toString()
            )

        )

        val dirList = listOf(
            Pair(getString(R.string.source_dir), packageInfo.applicationInfo.sourceDir),
            Pair(getString(R.string.data_dir), packageInfo.applicationInfo.dataDir),
            Pair(
                getString(R.string.native_library_dir), packageInfo.applicationInfo.nativeLibraryDir
            ),
            Pair(
                getString(R.string.shared_lib_files),
                packageInfo.applicationInfo.sharedLibraryFiles.contentToString()
            )
        )

        val metaDataList = metaDataToPairs(packageInfo.applicationInfo.metaData)

        val otherList = listOf(
            Pair(
                getString(R.string.revision_code),
                PackageManagerUtil.getRevisionCode(packageInfo, requireContext())
            ),
            Pair(
                getString(R.string.gids), packageInfo.gids.contentToString()
            ),
            Pair(getString(R.string.app_class_name), packageInfo.applicationInfo.className),
            Pair(getString(R.string.process_name), packageInfo.applicationInfo.processName),

            Pair(getString(R.string.uid), packageInfo.applicationInfo.uid.toString()),
            Pair(getString(R.string.shared_user_id), packageInfo.sharedUserId)
        )
        cardDataList = listOf(
            CardData(getString(R.string.basic_info), basicInfoList),
            CardData(getString(R.string.dir_info), dirList),
            CardData(getString(R.string.metadata), metaDataList),
            CardData(getString(R.string.other_info), otherList)
        )

    }

    private fun metaDataToPairs(metaData: Bundle?): List<Pair<String, String>> {
        if (metaData == null || metaData.isEmpty) {
            return emptyList()
        }
        return metaData.keySet().map { key -> Pair(key, metaData.get(key).toString()) }
    }


    @SuppressLint("ClickableViewAccessibility")
    private fun initRv() {
        binding.rv.adapter =
            SimpleRvAdapter(
                cardDataList,
                ItemAppBasicInfoCardBinding::inflate
            ) { itemBinding, cardData, _ ->
                itemBinding.tv.text = cardData.title
                itemBinding.rv.adapter = SimpleRvAdapter(
                    cardData.pairs, ItemAppBasicInfoBinding::inflate
                ) { itemBindingInner, pair, _ ->
                    itemBindingInner.tvKey.text = pair.first
                    itemBindingInner.tvValue.text = pair.second
                    itemBindingInner.root.setOnLongClickListener {
                        val popupMenu = PopupMenu(requireContext(), itemBindingInner.tvValue)
                        popupMenu.inflate(R.menu.popup_menu_app_detail)
                        popupMenu.setOnMenuItemClickListener {
                            ClipboardUtil.copyToClipboard(
                                requireContext(),
                                itemBindingInner.tvValue.text.toString()
                            )
                            true
                        }
                        popupMenu.show()
                        return@setOnLongClickListener true
                    }
                }
            }

    }

}