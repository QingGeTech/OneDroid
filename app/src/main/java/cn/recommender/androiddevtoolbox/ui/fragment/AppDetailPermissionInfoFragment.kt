package cn.recommender.androiddevtoolbox.ui.fragment;

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageInfo
import android.content.pm.PermissionInfo
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.util.Base64
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.pm.PackageInfoCompat
import androidx.core.os.BundleCompat
import androidx.core.widget.PopupWindowCompat
import cn.recommender.androiddevtoolbox.R
import cn.recommender.androiddevtoolbox.base.BaseFragment
import cn.recommender.androiddevtoolbox.base.SimpleRvAdapter
import cn.recommender.androiddevtoolbox.data.entity.CardData
import cn.recommender.androiddevtoolbox.databinding.FragmentAppDetailActivityInfoBinding
import cn.recommender.androiddevtoolbox.databinding.FragmentAppDetailPermissionInfoBinding
import cn.recommender.androiddevtoolbox.databinding.FragmentAppDetailServiceInfoBinding
import cn.recommender.androiddevtoolbox.databinding.FragmentAppDetailSignInfoBinding
import cn.recommender.androiddevtoolbox.databinding.ItemActivityListBinding
import cn.recommender.androiddevtoolbox.databinding.ItemAppBasicInfoBinding
import cn.recommender.androiddevtoolbox.databinding.ItemAppBasicInfoCardBinding
import cn.recommender.androiddevtoolbox.databinding.ItemAppListBinding
import cn.recommender.androiddevtoolbox.databinding.ItemPermissionListBinding
import cn.recommender.androiddevtoolbox.databinding.ItemServiceListBinding
import cn.recommender.androiddevtoolbox.databinding.ItemUsesPermissionListBinding
import cn.recommender.androiddevtoolbox.util.ClipboardUtil
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
class AppDetailPermissionInfoFragment @Inject constructor() :
    BaseFragment<FragmentAppDetailPermissionInfoBinding>() {

    private lateinit var packageInfo: PackageInfo

    private lateinit var cardDataList: List<CardData>

    private lateinit var requestedPermissionsMap: Map<Int, String>

    private lateinit var permissionFlagsMap: Map<Int, String>

    override fun initViews() {
        packageInfo =
            BundleCompat.getParcelable(requireArguments(), "packageInfo", PackageInfo::class.java)!!

        requestedPermissionsMap = mapOf(
            PackageInfo.REQUESTED_PERMISSION_GRANTED to getString(R.string.granted),
        )

        permissionFlagsMap = mapOf(
            *(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) arrayOf(
                PermissionInfo.FLAG_INSTALLED to getString(R.string.installed),
            ) else emptyArray()),
            PermissionInfo.FLAG_COSTS_MONEY to getString(R.string.costs_money),
            *(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) arrayOf(
                PermissionInfo.FLAG_HARD_RESTRICTED to getString(R.string.hard_restricted),
                PermissionInfo.FLAG_SOFT_RESTRICTED to getString(R.string.soft_restricted),
                PermissionInfo.FLAG_IMMUTABLY_RESTRICTED to getString(R.string.immutably_restricted)
            ) else emptyArray()),
        )

        initCardData()

        initRv()
    }

    private fun initCardData() {
        cardDataList = listOf(
            CardData(
                getString(R.string.custom_permission),
                if (packageInfo.permissions == null) emptyList() else packageInfo.permissions.map {
                    Pair(
                        it.name,
                        ""
                    )
                }.sortedBy { it.first }),
            CardData(
                getString(R.string.uses_permission),
                if (packageInfo.requestedPermissions == null) emptyList() else
                    packageInfo.requestedPermissions.mapIndexed { index, s ->
                        Pair(
                            s,
                            IntentUtil.parseFlag(
                                packageInfo.requestedPermissionsFlags[index],
                                requestedPermissionsMap
                            )
                        )
                    }.sortedBy { it.first })
        )
    }

    @SuppressLint("SetTextI18n")
    private fun initRv() {
        binding.rv.adapter = SimpleRvAdapter(
            cardDataList,
            ItemAppBasicInfoCardBinding::inflate
        ) { itemBinding, cardData, index ->
            itemBinding.tv.text = cardData.title
            if (index == 0) {
                itemBinding.rv.adapter = SimpleRvAdapter(
                    cardData.pairs,
                    ItemPermissionListBinding::inflate
                ) { innerItemBinding, pair, i ->
                    innerItemBinding.tvPermission.text = pair.first
                    innerItemBinding.root.setOnClickListener {
                        val kvListDialogFragment = KvListDialogFragment()

                        val kvList: MutableList<Pair<String, String?>> = mutableListOf(
                            Pair(
                                getString(R.string.permission_name),
                                packageInfo.permissions[i].name
                            ),
                            Pair(
                                getString(R.string.permission_group),
                                packageInfo.permissions[i].group
                            ),
                            Pair(
                                getString(R.string.flags),
                                IntentUtil.parseFlag(
                                    packageInfo.permissions[i].flags,
                                    permissionFlagsMap
                                )
                            )
                        )

                        val bundle = Bundle()
                        bundle.putSerializable("kvList", kvList as Serializable)
                        kvListDialogFragment.arguments = bundle
                        kvListDialogFragment.show(childFragmentManager, "KvListDialogFragment")
                    }
                }

            } else {
                itemBinding.rv.adapter = SimpleRvAdapter(
                    cardData.pairs,
                    ItemUsesPermissionListBinding::inflate
                ) { innerItemBinding, pair, _ ->
                    innerItemBinding.tvUsesPermission.text =
                        if (pair.first.startsWith("android.permission.")) pair.first.replace(
                            "android.permission.",
                            ""
                        ) else pair.first
                    innerItemBinding.tvStatus.text = pair.second
                    innerItemBinding.root.setOnClickListener {
                        val kvListDialogFragment = KvListDialogFragment()

                        val kvList: MutableList<Pair<String, String?>> = mutableListOf(
                            Pair(
                                getString(R.string.permission_name),
                                pair.first
                            ),
                            Pair(
                                getString(R.string.flags),
                                pair.second
                            )
                        )

                        val bundle = Bundle()
                        bundle.putSerializable("kvList", kvList as Serializable)
                        kvListDialogFragment.arguments = bundle
                        kvListDialogFragment.show(childFragmentManager, "KvListDialogFragment")
                    }
                }
            }
        }
    }

}
