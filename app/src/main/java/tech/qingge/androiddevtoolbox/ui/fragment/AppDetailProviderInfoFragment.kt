package tech.qingge.androiddevtoolbox.ui.fragment

import android.annotation.SuppressLint
import android.content.pm.PackageInfo
import android.content.pm.PathPermission
import android.os.Build
import android.os.Bundle
import android.os.PatternMatcher
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import tech.qingge.androiddevtoolbox.R
import tech.qingge.androiddevtoolbox.base.BaseFragment
import tech.qingge.androiddevtoolbox.base.SimpleRvAdapter
import tech.qingge.androiddevtoolbox.data.local.sys.SysApi
import tech.qingge.androiddevtoolbox.databinding.FragmentAppDetailProviderInfoBinding
import tech.qingge.androiddevtoolbox.databinding.ItemProviderListBinding
import tech.qingge.androiddevtoolbox.util.IntentUtil
import java.io.Serializable
import javax.inject.Inject

@AndroidEntryPoint
class AppDetailProviderInfoFragment @Inject constructor() :
    BaseFragment<FragmentAppDetailProviderInfoBinding>() {

    private lateinit var packageInfo: PackageInfo


    @Inject
    lateinit var sysApi: SysApi

    override fun initViews() {
//        packageInfo =
//            BundleCompat.getParcelable(requireArguments(), "packageInfo", PackageInfo::class.java)!!

        val packageName = requireArguments().getString("packageName")
        packageInfo = sysApi.getPackageInfo(packageName!!)

        initRv()
    }

    private fun getPathPermissions(pathPermissions: Array<PathPermission>?): String {
        if (pathPermissions == null) {
            return ""
        }
        return pathPermissions.joinToString { it.path + "\n" + it.writePermission + "\n" + it.readPermission + "\n" }
    }

    private fun getUriPermissionPatterns(uriPermissionPatterns: Array<PatternMatcher>?): String {
        if (uriPermissionPatterns == null){
            return ""
        }
        return uriPermissionPatterns.joinToString { it.path + "\n" + it.type + "\n" }
    }

    @SuppressLint("SetTextI18n")
    private fun initRv() {
        if (packageInfo.providers == null) {
            return
        }
        binding.rv.adapter =
            SimpleRvAdapter(
                packageInfo.providers!!.toList(),
                ItemProviderListBinding::inflate
            ) { itemBinding, providerInfo, _ ->
                Glide.with(requireContext())
                    .load(providerInfo.loadIcon(requireContext().packageManager))
                    .into(itemBinding.ivIcon)

                itemBinding.tvProviderName.text =
                    providerInfo.name.substring(providerInfo.name.lastIndexOf(".") + 1)
                if (providerInfo.name.startsWith(providerInfo.packageName)) {
                    itemBinding.tvProviderFullName.text = ".${itemBinding.tvProviderName.text}"
                } else {
                    itemBinding.tvProviderFullName.text = providerInfo.name
                }

                itemBinding.root.setOnClickListener {
                    val kvListDialogFragment = KvListDialogFragment()
                    val kvList: MutableList<Pair<String, String?>> = mutableListOf(
                        Pair(getString(R.string.class_name), providerInfo.name),
                        Pair(
                            getString(R.string.metadata),
                            IntentUtil.getPrintableBundle(providerInfo.metaData)
                        ),
                        Pair(getString(R.string.exported), providerInfo.exported.toString()),
                        Pair(getString(R.string.authority), providerInfo.authority),
                        Pair(
                            getString(R.string.grant_uri_permissions),
                            providerInfo.grantUriPermissions.toString()
                        ),
                        Pair(
                            getString(R.string.init_order),
                            providerInfo.initOrder.toString()
                        ),
                        Pair(
                            getString(R.string.read_permission),
                            providerInfo.readPermission
                        ),
                        Pair(
                            getString(R.string.write_permission),
                            providerInfo.writePermission
                        ),
                        Pair(
                            getString(R.string.path_permissions),
                            getPathPermissions(providerInfo.pathPermissions)
                        ),
                        Pair(
                            getString(R.string.uri_permission_patterns),
                            getUriPermissionPatterns(providerInfo.uriPermissionPatterns)
                        )
                    )

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        kvList.add(
                            Pair(
                                getString(R.string.force_uri_permissions),
                                providerInfo.forceUriPermissions.toString()
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
