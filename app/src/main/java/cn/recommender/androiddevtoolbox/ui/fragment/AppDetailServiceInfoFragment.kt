package cn.recommender.androiddevtoolbox.ui.fragment;

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageInfo
import android.os.Bundle
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
import cn.recommender.androiddevtoolbox.databinding.FragmentAppDetailActivityInfoBinding
import cn.recommender.androiddevtoolbox.databinding.FragmentAppDetailServiceInfoBinding
import cn.recommender.androiddevtoolbox.databinding.FragmentAppDetailSignInfoBinding
import cn.recommender.androiddevtoolbox.databinding.ItemActivityListBinding
import cn.recommender.androiddevtoolbox.databinding.ItemAppBasicInfoBinding
import cn.recommender.androiddevtoolbox.databinding.ItemAppBasicInfoCardBinding
import cn.recommender.androiddevtoolbox.databinding.ItemAppListBinding
import cn.recommender.androiddevtoolbox.databinding.ItemServiceListBinding
import cn.recommender.androiddevtoolbox.util.DateTimeUtil
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

// TODO: 长按复制
// TODO: 解析Activity含义
@AndroidEntryPoint
class AppDetailServiceInfoFragment @Inject constructor() :
    BaseFragment<FragmentAppDetailServiceInfoBinding>() {

    private lateinit var packageInfo: PackageInfo


    override fun initViews() {
        packageInfo =
            BundleCompat.getParcelable(requireArguments(), "packageInfo", PackageInfo::class.java)!!

        initRv()
        packageInfo.requestedPermissionsFlags
    }

    @SuppressLint("SetTextI18n")
    private fun initRv() {
        binding.rv.adapter = SimpleRvAdapter(
            packageInfo.services.asList()
                .sortedBy { it.name.substring(it.name.lastIndexOf(".") + 1) },
            ItemServiceListBinding::inflate
        ) { itemBinding, serviceInfo, _ ->
            Glide.with(requireContext())
                .load(serviceInfo.loadIcon(requireContext().packageManager))
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
                val kvList: List<Pair<String, String>> = listOf(
                    Pair(getString(R.string.class_name), serviceInfo.name),
//                    Pair(getString(R.string.metadata), activityInfo.metaData.toString()),
                    Pair(getString(R.string.exported), serviceInfo.exported.toString()),
                    Pair(getString(R.string.flags), serviceInfo.flags.toString()),
//                    Pair(getString(R.string.metadata), serviceInfo.metaData),
//                    Pair(getString(R.string.forground_service_type), serviceInfo.foregroundServiceType.toString()),
                )
                val bundle = Bundle()
                bundle.putSerializable("kvList", kvList as Serializable)
                kvListDialogFragment.arguments = bundle
                kvListDialogFragment.show(childFragmentManager, "KvListDialogFragment")
            }
        }
    }

}
