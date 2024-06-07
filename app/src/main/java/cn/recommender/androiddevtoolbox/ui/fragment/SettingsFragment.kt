package cn.recommender.androiddevtoolbox.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import cn.recommender.androiddevtoolbox.Constants
import cn.recommender.androiddevtoolbox.base.BaseFragment
import cn.recommender.androiddevtoolbox.databinding.FragmentSettingsBinding
import cn.recommender.androiddevtoolbox.viewmodel.SettingsFragmentViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment @Inject constructor() : BaseFragment<FragmentSettingsBinding>() {


    private val viewModel: SettingsFragmentViewModel by viewModels()

    @Inject
    lateinit var chooseColorDialogFragment: ChooseColorDialogFragment

    override fun initViews() {
        initAppearanceSetting()

        viewModel.loadSettings()
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