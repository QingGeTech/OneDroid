package cn.recommender.androiddevtoolbox.ui.fragment;

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageInfo
import android.content.pm.PathPermission
import android.os.Build
import android.os.Bundle
import android.os.PatternMatcher
import android.text.SpannableString
import android.util.Base64
import android.view.View
import android.widget.TextView
import androidx.core.content.pm.PackageInfoCompat
import androidx.core.os.BundleCompat
import androidx.core.widget.PopupWindowCompat
import cn.recommender.androiddevtoolbox.R
import cn.recommender.androiddevtoolbox.base.BaseFragment
import cn.recommender.androiddevtoolbox.base.SimpleRvAdapter
import cn.recommender.androiddevtoolbox.data.entity.CardData
import cn.recommender.androiddevtoolbox.data.local.sys.SysApi
import cn.recommender.androiddevtoolbox.databinding.FragmentAppDetailActivityInfoBinding
import cn.recommender.androiddevtoolbox.databinding.FragmentAppDetailPermissionInfoBinding
import cn.recommender.androiddevtoolbox.databinding.FragmentAppDetailProviderInfoBinding
import cn.recommender.androiddevtoolbox.databinding.FragmentAppDetailReceiverInfoBinding
import cn.recommender.androiddevtoolbox.databinding.FragmentAppDetailServiceInfoBinding
import cn.recommender.androiddevtoolbox.databinding.FragmentAppDetailSignInfoBinding
import cn.recommender.androiddevtoolbox.databinding.ItemActivityListBinding
import cn.recommender.androiddevtoolbox.databinding.ItemAppBasicInfoBinding
import cn.recommender.androiddevtoolbox.databinding.ItemAppBasicInfoCardBinding
import cn.recommender.androiddevtoolbox.databinding.ItemAppListBinding
import cn.recommender.androiddevtoolbox.databinding.ItemProviderListBinding
import cn.recommender.androiddevtoolbox.databinding.ItemServiceListBinding
import cn.recommender.androiddevtoolbox.util.DateTimeUtil
import cn.recommender.androiddevtoolbox.util.IntentUtil
import cn.recommender.androiddevtoolbox.util.LogUtil
import cn.recommender.androiddevtoolbox.util.base64
import cn.recommender.androiddevtoolbox.util.hex
import cn.recommender.androiddevtoolbox.util.md5
import cn.recommender.androiddevtoolbox.util.sha1
import cn.recommender.androiddevtoolbox.util.sha256
import com.bumptech.glide.Glide
import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import java.io.ByteArrayInputStream
import java.io.Serializable
import java.security.MessageDigest
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import kotlin.math.sign

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
                packageInfo.providers.toList(),
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
