package tech.qingge.androiddevtoolbox.ui.fragment

import android.annotation.SuppressLint
import android.content.pm.PackageInfo
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.Bundle
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import tech.qingge.androiddevtoolbox.R
import tech.qingge.androiddevtoolbox.base.BaseFragment
import tech.qingge.androiddevtoolbox.base.SimpleRvAdapter
import tech.qingge.androiddevtoolbox.data.local.sys.SysApi
import tech.qingge.androiddevtoolbox.databinding.FragmentAppDetailServiceInfoBinding
import tech.qingge.androiddevtoolbox.databinding.ItemServiceListBinding
import tech.qingge.androiddevtoolbox.util.IntentUtil
import java.io.Serializable
import javax.inject.Inject

@AndroidEntryPoint
class AppDetailServiceInfoFragment @Inject constructor() :
    BaseFragment<FragmentAppDetailServiceInfoBinding>() {

    private lateinit var packageInfo: PackageInfo

    private lateinit var flagsMap: Map<Int, String>
    private lateinit var foregroundServiceTypeMap: Map<Int, String>

    @Inject
    lateinit var sysApi: SysApi

    override fun initViews() {
//        packageInfo =
//            BundleCompat.getParcelable(requireArguments(), "packageInfo", PackageInfo::class.java)!!

        val packageName = requireArguments().getString("packageName")
        packageInfo = sysApi.getPackageInfo(packageName!!)

        flagsMap = mapOf(
            *(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) arrayOf(
                ServiceInfo.FLAG_EXTERNAL_SERVICE to getString(R.string.external_service),
            ) else emptyArray()),

            *(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) arrayOf(
                ServiceInfo.FLAG_USE_APP_ZYGOTE to getString(R.string.use_app_zygote),
            ) else emptyArray()),

            *(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) arrayOf(
                ServiceInfo.FLAG_ALLOW_SHARED_ISOLATED_PROCESS to getString(R.string.allow_shared_isolated_process),
            ) else emptyArray()),

            ServiceInfo.FLAG_SINGLE_USER to getString(R.string.single_user),
            ServiceInfo.FLAG_ISOLATED_PROCESS to getString(R.string.isolated_process),
            ServiceInfo.FLAG_STOP_WITH_TASK to getString(R.string.stop_with_task)
        )

        foregroundServiceTypeMap = mapOf(
            *(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) arrayOf(
                ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION to getString(R.string.foreground_service_type_location),
                ServiceInfo.FOREGROUND_SERVICE_TYPE_CONNECTED_DEVICE to getString(R.string.foreground_service_type_connected_device),
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC to getString(R.string.foreground_service_type_data_sync),
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MANIFEST to getString(R.string.foreground_service_type_manifest),
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK to getString(R.string.foreground_service_type_media_playback),
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION to getString(R.string.foreground_service_type_media_projection),
                ServiceInfo.FOREGROUND_SERVICE_TYPE_PHONE_CALL to getString(R.string.foreground_service_type_phone_call),
                ServiceInfo.FOREGROUND_SERVICE_TYPE_NONE to getString(R.string.foreground_service_type_none),
            ) else emptyArray()),
            *(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) arrayOf(
                ServiceInfo.FOREGROUND_SERVICE_TYPE_CAMERA to getString(R.string.foreground_service_type_camera),
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE to getString(R.string.foreground_service_type_microphone),
            ) else emptyArray()),
            *(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) arrayOf(
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SHORT_SERVICE to getString(R.string.foreground_service_type_short_service),
                ServiceInfo.FOREGROUND_SERVICE_TYPE_HEALTH to getString(R.string.foreground_service_type_health),
                ServiceInfo.FOREGROUND_SERVICE_TYPE_REMOTE_MESSAGING to getString(R.string.foreground_service_type_remote_messaging),
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE to getString(R.string.foreground_service_type_special_use),
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SYSTEM_EXEMPTED to getString(R.string.foreground_service_type_system_exempted),
            ) else emptyArray())

        )

        initRv()
        packageInfo.requestedPermissionsFlags
    }

    @SuppressLint("SetTextI18n")
    private fun initRv() {
        if (packageInfo.services == null) {
            return
        }

        binding.rv.adapter = SimpleRvAdapter(
            packageInfo.services!!.asList()
                .sortedBy { it.name.substring(it.name.lastIndexOf(".") + 1) },
            ItemServiceListBinding::inflate
        ) { itemBinding, serviceInfo, _ ->
            Glide.with(requireContext()).load(serviceInfo.loadIcon(requireContext().packageManager))
                .into(itemBinding.ivIcon)
            itemBinding.tvServiceName.text =
                serviceInfo.name.substring(serviceInfo.name.lastIndexOf(".") + 1)
            if (serviceInfo.name.startsWith(serviceInfo.packageName)) {
                itemBinding.tvServiceFullName.text = ".${itemBinding.tvServiceName.text}"
            } else {
                itemBinding.tvServiceFullName.text = serviceInfo.name
            }
            itemBinding.root.setOnClickListener {
                val kvListDialogFragment = KvListDialogFragment()
                val kvList: MutableList<Pair<String, String?>> = mutableListOf(
                    Pair(getString(R.string.class_name), serviceInfo.name), Pair(
                        getString(R.string.metadata),
                        IntentUtil.getPrintableBundle(serviceInfo.metaData)
                    ), Pair(getString(R.string.exported), serviceInfo.exported.toString()), Pair(
                        getString(R.string.flags),
                        IntentUtil.parseFlag(serviceInfo.flags, flagsMap),
                    ), Pair(
                        getString(R.string.permission), serviceInfo.permission
                    )
                )
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    kvList.add(
                        Pair(
                            getString(R.string.forground_service_type),
                            foregroundServiceTypeMap[serviceInfo.foregroundServiceType]
                        )
                    )
                }
                val bundle = Bundle()
                bundle.putSerializable("kvList", kvList as Serializable)
                kvListDialogFragment.arguments = bundle
                kvListDialogFragment.show(childFragmentManager, "KvListDialogFragment")
            }
        }
    }

}
