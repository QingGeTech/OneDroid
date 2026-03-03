package tech.qingge.onedroid.ui.fragment

import android.content.Intent
import androidx.fragment.app.viewModels
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import dagger.hilt.android.AndroidEntryPoint
import tech.qingge.onedroid.BuildConfig
import tech.qingge.onedroid.Constants
import tech.qingge.onedroid.base.BaseFragment
import tech.qingge.onedroid.databinding.FragmentSettingsBinding
import tech.qingge.onedroid.util.CommonUtil.openUrl
import tech.qingge.onedroid.viewmodel.SettingsFragmentViewModel
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment @Inject constructor() : BaseFragment<FragmentSettingsBinding>() {


    private val viewModel: SettingsFragmentViewModel by viewModels()

    @Inject
    lateinit var chooseColorDialogFragment: ChooseColorDialogFragment

    override fun initViews() {
        initAppearanceSetting()

        viewModel.loadSettings()

        binding.rlUserAgreement.setOnClickListener { openUrl(requireActivity(),"https://qingge.tech/onedroid/user-protocol.html") }
        binding.rlPrivatePolicy.setOnClickListener { openUrl(requireActivity(),"https://qingge.tech/onedroid/privacy-policy.html") }

        binding.rlGetSourceCode.setOnClickListener { openUrl(requireActivity(),"https://github.com/QingGeTech/OneDroid") }
        binding.rlCheckUpdate.setOnClickListener { openUrl(requireActivity(),"https://github.com/QingGeTech/OneDroid/releases") }

        binding.tvVersion.text = "${binding.tvVersion.text}  ${BuildConfig.VERSION_NAME}(${BuildConfig.VERSION_CODE})"

    }

    private fun sendThemeChangeBroadcast() {
        val intent = Intent(Constants.LOCAL_BROADCAST_ACTION_THEME_CHANGE)
        LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)
    }

    private fun initAppearanceSetting() {
        viewModel.themeColor.observe(viewLifecycleOwner) {
            binding.ccv.setColor(it)
        }

        viewModel.isDarkTheme.observe(viewLifecycleOwner) {
            if (binding.msDarkTheme.isChecked != it) {
                binding.msDarkTheme.isChecked = it
            }
        }

        binding.rlChooseColor.setOnClickListener {
            chooseColorDialogFragment.show(childFragmentManager, "ChooseColor")
            chooseColorDialogFragment.callback = object : ChooseColorDialogFragment.Callback{
                override fun onChooseColor(color: Int) {
                    viewModel.onThemeColorChange(color)
                    sendThemeChangeBroadcast()
                    requireActivity().recreate()
                }
            }
        }
        binding.rlDarkTheme.setOnClickListener {
            viewModel.onDarkThemeChange(!binding.msDarkTheme.isChecked)
            sendThemeChangeBroadcast()
        }
        binding.msDarkTheme.setOnCheckedChangeListener { _, isChecked ->
            viewModel.onDarkThemeChange(isChecked)
            sendThemeChangeBroadcast()
        }

    }


}