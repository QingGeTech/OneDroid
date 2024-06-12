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
import cn.recommender.androiddevtoolbox.databinding.FragmentAppDetailSignInfoBinding
import cn.recommender.androiddevtoolbox.databinding.ItemActivityListBinding
import cn.recommender.androiddevtoolbox.databinding.ItemAppBasicInfoBinding
import cn.recommender.androiddevtoolbox.databinding.ItemAppBasicInfoCardBinding
import cn.recommender.androiddevtoolbox.databinding.ItemAppListBinding
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
class AppDetailActivityInfoFragment @Inject constructor() :
    BaseFragment<FragmentAppDetailActivityInfoBinding>() {

    private lateinit var packageInfo: PackageInfo


    override fun initViews() {
        packageInfo =
            BundleCompat.getParcelable(requireArguments(), "packageInfo", PackageInfo::class.java)!!

        initRv()

    }

    @SuppressLint("SetTextI18n")
    private fun initRv() {
        binding.rv.adapter = SimpleRvAdapter<ActivityInfo, ItemActivityListBinding>(
            packageInfo.activities.asList()
                .sortedBy { it.name.substring(it.name.lastIndexOf(".") + 1) },
            ItemActivityListBinding::inflate
        ) { itemBinding, activityInfo, index ->
            Glide.with(requireContext())
                .load(activityInfo.loadIcon(requireContext().packageManager))
                .into(itemBinding.ivIcon)
            itemBinding.tvActivityName.text =
                activityInfo.name.substring(activityInfo.name.lastIndexOf(".") + 1)
            if (activityInfo.name.startsWith(activityInfo.packageName)) {
                itemBinding.tvActivityFullName.text = ".${itemBinding.tvActivityName.text}"
            } else {
                itemBinding.tvActivityFullName.text = activityInfo.name
            }
            if (activityInfo.exported) {
                itemBinding.ivOpen.visibility = View.VISIBLE
                itemBinding.ivOpen.setOnClickListener {
                    val intent = Intent()
                    intent.setClassName(activityInfo.packageName, activityInfo.name)
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    requireContext().startActivity(intent)
                }
            } else {
                itemBinding.ivOpen.visibility = View.GONE
            }
            itemBinding.root.setOnClickListener {
                val kvListDialogFragment = KvListDialogFragment()
                val kvList: List<Pair<String, String>> = listOf(
                    Pair(getString(R.string.class_name), activityInfo.name),
//                    Pair(getString(R.string.metadata), activityInfo.metaData.toString()),
                    Pair(getString(R.string.target_activity), activityInfo.targetActivity),
                    Pair(
                        getString(R.string.config_changes), activityInfo.configChanges.toString()
                    ),
                    Pair(getString(R.string.flags), activityInfo.flags.toString()),
                    Pair(getString(R.string.launch_mode), activityInfo.launchMode.toString()),
                    Pair(getString(R.string.parent_activity_name), activityInfo.parentActivityName),
                    Pair(getString(R.string.max_recents), activityInfo.maxRecents.toString()),
                    Pair(getString(R.string.permission), activityInfo.permission),
                    Pair(
                        getString(R.string.persistable_mode),
                        activityInfo.persistableMode.toString()
                    ),
                    Pair(
                        getString(R.string.screen_orientation),
                        activityInfo.screenOrientation.toString()
                    ),
                    Pair(
                        getString(R.string.soft_input_mode),
                        activityInfo.softInputMode.toString()
                    ),
                    Pair(getString(R.string.task_affinity), activityInfo.taskAffinity),
                    Pair(getString(R.string.ui_options), activityInfo.uiOptions.toString()),
                    Pair(getString(R.string.exported), activityInfo.exported.toString()),
//                    Pair(getString(R.string.color_mode), activityInfo.colorMode.toString()),
//                    Pair(getString(R.string.persistable_mode), activityInfo.requiredDisplayCategory.toString()),

                )
                val bundle = Bundle()
                bundle.putSerializable("kvList", kvList as Serializable)
                kvListDialogFragment.arguments = bundle
                kvListDialogFragment.show(childFragmentManager, "KvListDialogFragment")
            }
        }
    }

}
