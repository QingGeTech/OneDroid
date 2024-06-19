package cn.recommender.androiddevtoolbox.ui.fragment;

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageInfo
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.util.Base64
import android.view.View
import android.view.WindowManager
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
import cn.recommender.androiddevtoolbox.databinding.FragmentAppDetailSignInfoBinding
import cn.recommender.androiddevtoolbox.databinding.ItemActivityListBinding
import cn.recommender.androiddevtoolbox.databinding.ItemAppBasicInfoBinding
import cn.recommender.androiddevtoolbox.databinding.ItemAppBasicInfoCardBinding
import cn.recommender.androiddevtoolbox.databinding.ItemAppListBinding
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
open class AppDetailActivityInfoFragment @Inject constructor() :
    BaseFragment<FragmentAppDetailActivityInfoBinding>() {

    private lateinit var packageInfo: PackageInfo

    private lateinit var configChangesMap: Map<Int, String>
    private lateinit var flagsMap: Map<Int, String>
    private lateinit var launchModeMap: Map<Int, String>
    private lateinit var persistableModeMap: Map<Int, String>
    private lateinit var screenOrientationMap: Map<Int, String>
    private lateinit var softInputModeMap: Map<Int, String>
    private lateinit var uiOptionsMap: Map<Int, String>
    private lateinit var colorModeMap: Map<Int, String>

    @Inject
    lateinit var sysApi: SysApi

    override fun initViews() {
//        packageInfo =
//            BundleCompat.getParcelable(requireArguments(), "packageInfo", PackageInfo::class.java)!!

        val packageName = requireArguments().getString("packageName")
        packageInfo = sysApi.getPackageInfo(packageName!!)

        configChangesMap = mapOf(
            ActivityInfo.CONFIG_MCC to getString(R.string.mcc),
            ActivityInfo.CONFIG_MNC to getString(R.string.mnc),
            ActivityInfo.CONFIG_DENSITY to getString(R.string.density),
            ActivityInfo.CONFIG_LOCALE to getString(R.string.locale),
            ActivityInfo.CONFIG_KEYBOARD to getString(R.string.keyboard_type),
            ActivityInfo.CONFIG_KEYBOARD_HIDDEN to getString(R.string.keyboard_hidden),
            ActivityInfo.CONFIG_FONT_SCALE to getString(R.string.font_scale),
            ActivityInfo.CONFIG_LAYOUT_DIRECTION to getString(R.string.layout_direction),
            ActivityInfo.CONFIG_NAVIGATION to getString(R.string.navigation_type),
            ActivityInfo.CONFIG_ORIENTATION to getString(R.string.screen_orientation),
            ActivityInfo.CONFIG_SCREEN_LAYOUT to getString(R.string.screen_layout),
            ActivityInfo.CONFIG_SCREEN_SIZE to getString(R.string.screen_size),
            ActivityInfo.CONFIG_SMALLEST_SCREEN_SIZE to getString(R.string.smallest_screen_size),
            ActivityInfo.CONFIG_TOUCHSCREEN to getString(R.string.touchscreen),
            ActivityInfo.CONFIG_UI_MODE to getString(R.string.ui_mode),
            *(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) arrayOf(
                ActivityInfo.CONFIG_COLOR_MODE to getString(
                    R.string.color_mode
                )
            ) else emptyArray()),
            *(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) arrayOf(
                ActivityInfo.CONFIG_FONT_WEIGHT_ADJUSTMENT to getString(R.string.font_weight_adjustment)
            ) else emptyArray()),
            *(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) arrayOf(
                ActivityInfo.CONFIG_GRAMMATICAL_GENDER to getString(R.string.grammatical_gender)
            ) else emptyArray())
        )

        flagsMap = mapOf(
            ActivityInfo.FLAG_IMMERSIVE to getString(R.string.immersive),
            ActivityInfo.FLAG_MULTIPROCESS to getString(R.string.multiprocess),
            ActivityInfo.FLAG_NO_HISTORY to getString(R.string.no_history),
            ActivityInfo.FLAG_SINGLE_USER to getString(R.string.single_user),
            ActivityInfo.FLAG_ALLOW_TASK_REPARENTING to getString(R.string.allow_task_reparenting),
            ActivityInfo.FLAG_ALWAYS_RETAIN_TASK_STATE to getString(R.string.always_retain_task_state),
            ActivityInfo.FLAG_AUTO_REMOVE_FROM_RECENTS to getString(R.string.auto_remove_from_recents),
            ActivityInfo.FLAG_CLEAR_TASK_ON_LAUNCH to getString(R.string.clear_task_on_launch),
            ActivityInfo.FLAG_EXCLUDE_FROM_RECENTS to getString(R.string.exclude_from_recents),
            ActivityInfo.FLAG_FINISH_ON_CLOSE_SYSTEM_DIALOGS to getString(R.string.finish_on_close_system_dialogs),
            ActivityInfo.FLAG_FINISH_ON_TASK_LAUNCH to getString(R.string.finish_on_task_launch),
            ActivityInfo.FLAG_HARDWARE_ACCELERATED to getString(R.string.hardware_accelerated),
            ActivityInfo.FLAG_RELINQUISH_TASK_IDENTITY to getString(R.string.relinquish_task_identity),
            ActivityInfo.FLAG_RESUME_WHILE_PAUSING to getString(R.string.resume_while_pausing),
            ActivityInfo.FLAG_STATE_NOT_NEEDED to getString(R.string.state_not_needed),

            *(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) arrayOf(
                ActivityInfo.FLAG_ALLOW_UNTRUSTED_ACTIVITY_EMBEDDING to getString(R.string.allow_untrusted_activity_embedding)
            ) else emptyArray()),

            *(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) arrayOf(
                ActivityInfo.FLAG_ENABLE_VR_MODE to getString(R.string.enable_vr_mode)
            ) else emptyArray()),

            *(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) arrayOf(
                ActivityInfo.FLAG_PREFER_MINIMAL_POST_PROCESSING to getString(R.string.prefer_minimal_post_processing)
            ) else emptyArray())

        )

        launchModeMap = mapOf(
            ActivityInfo.LAUNCH_MULTIPLE to getString(R.string.standard),
            ActivityInfo.LAUNCH_SINGLE_TOP to getString(R.string.single_top),
            ActivityInfo.LAUNCH_SINGLE_TASK to getString(R.string.single_task),
            ActivityInfo.LAUNCH_SINGLE_INSTANCE to getString(R.string.single_instance),
            *(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) arrayOf(
                ActivityInfo.LAUNCH_SINGLE_INSTANCE_PER_TASK to getString(R.string.single_instance_per_task)
            ) else emptyArray()),
        )

        persistableModeMap = mapOf(
            ActivityInfo.PERSIST_ACROSS_REBOOTS to getString(R.string.persist_across_reboots),
            ActivityInfo.PERSIST_ROOT_ONLY to getString(R.string.persist_root_only),
            ActivityInfo.PERSIST_NEVER to getString(R.string.persist_never)
        )

        screenOrientationMap = mapOf(
            ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED to getString(R.string.unspecified),
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE to getString(R.string.landscape),
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT to getString(R.string.portrait),
            ActivityInfo.SCREEN_ORIENTATION_USER to getString(R.string.orientation_user),
            ActivityInfo.SCREEN_ORIENTATION_BEHIND to getString(R.string.behind),
            ActivityInfo.SCREEN_ORIENTATION_SENSOR to getString(R.string.sensor),
            ActivityInfo.SCREEN_ORIENTATION_NOSENSOR to getString(R.string.no_sensor),
            ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE to getString(R.string.sensor_landscape),
            ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT to getString(R.string.sensor_portrait),
            ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE to getString(R.string.reverse_landscape),
            ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT to getString(R.string.reverse_portrait),
            ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR to getString(R.string.full_sensor),
            ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE to getString(R.string.user_landscape),
            ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT to getString(R.string.user_portrait),
            ActivityInfo.SCREEN_ORIENTATION_FULL_USER to getString(R.string.full_user),
            ActivityInfo.SCREEN_ORIENTATION_LOCKED to getString(R.string.locked)
        )

        softInputModeMap = mapOf(
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN to getString(R.string.adjust_pan),
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING to getString(R.string.adjust_nothing),
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED to getString(R.string.adjust_unspecified),
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE to getString(R.string.adjust_resize),
            WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN to getString(R.string.state_hidden),
            WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE to getString(R.string.state_visible),
            WindowManager.LayoutParams.SOFT_INPUT_STATE_UNCHANGED to getString(R.string.state_unchanged),
            WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED to getString(R.string.state_unspecified),
            WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN to getString(R.string.state_always_hidden),
            WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE to getString(R.string.state_always_visible),
        )

        uiOptionsMap = mapOf(
            ActivityInfo.UIOPTION_SPLIT_ACTION_BAR_WHEN_NARROW to getString(R.string.split_action_bar_when_narrow)
        )

        colorModeMap = mapOf(
            *(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) arrayOf(
                ActivityInfo.COLOR_MODE_DEFAULT to getString(R.string.color_mode_default),
                ActivityInfo.COLOR_MODE_WIDE_COLOR_GAMUT to getString(R.string.color_mode_wide_color_gamut),
                ActivityInfo.COLOR_MODE_HDR to getString(R.string.color_mode_hdr)
            ) else emptyArray()),
        )

        initRv()

    }

    @SuppressLint("SetTextI18n")
    private fun initRv() {
        if (packageInfo.activities == null){
            return
        }

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
                val kvList: MutableList<Pair<String, String?>> = mutableListOf(
                    Pair(getString(R.string.class_name), activityInfo.name),
                    Pair(
                        getString(R.string.metadata),
                        IntentUtil.getPrintableBundle(activityInfo.metaData)
                    ),
                    Pair(getString(R.string.target_activity), activityInfo.targetActivity),
                    Pair(
                        getString(R.string.config_changes), IntentUtil.parseFlag(
                            activityInfo.configChanges, configChangesMap
                        )
                    ),
                    Pair(
                        getString(R.string.flags),
                        IntentUtil.parseFlag(activityInfo.flags, flagsMap)
                    ),
                    Pair(getString(R.string.launch_mode), launchModeMap[activityInfo.launchMode]),
                    Pair(getString(R.string.parent_activity_name), activityInfo.parentActivityName),
                    Pair(getString(R.string.max_recents), activityInfo.maxRecents.toString()),
                    Pair(getString(R.string.permission), activityInfo.permission),
                    Pair(
                        getString(R.string.persistable_mode),
                        persistableModeMap[activityInfo.persistableMode]
                    ),
                    Pair(
                        getString(R.string.screen_orientation),
                        screenOrientationMap[activityInfo.screenOrientation]
                    ),
                    Pair(
                        getString(R.string.soft_input_mode),
                        IntentUtil.parseFlag(activityInfo.softInputMode, softInputModeMap)
                    ),
                    Pair(getString(R.string.task_affinity), activityInfo.taskAffinity),
                    Pair(
                        getString(R.string.ui_options),
                        IntentUtil.parseFlag(activityInfo.uiOptions, uiOptionsMap),
                    ),
                    Pair(getString(R.string.exported), activityInfo.exported.toString()),
                )

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    kvList.add(
                        Pair(
                            getString(R.string.color_mode),
                            colorModeMap[activityInfo.colorMode]
                        )
                    )
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    kvList.add(
                        Pair(
                            getString(R.string.required_display_category),
                            if (activityInfo.requiredDisplayCategory == null) "null" else activityInfo.requiredDisplayCategory.toString()
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
