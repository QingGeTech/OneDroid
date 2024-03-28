package cn.recommender.androiddevtoolbox.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import cn.recommender.androiddevtoolbox.App
import cn.recommender.androiddevtoolbox.R
import cn.recommender.androiddevtoolbox.base.BaseFragment
import cn.recommender.androiddevtoolbox.databinding.FragmentDeviceInfoBinding
import cn.recommender.androiddevtoolbox.databinding.FragmentSettingsBinding
import cn.recommender.androiddevtoolbox.viewmodel.AppManagerViewModel
import cn.recommender.androiddevtoolbox.viewmodel.SettingsFragmentViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Singleton

@AndroidEntryPoint
class SettingsFragment @Inject constructor() : BaseFragment() {

    private lateinit var binding: FragmentSettingsBinding

    private val viewModel: SettingsFragmentViewModel by viewModels()

    @Inject
    lateinit var chooseColorDialogFragment: ChooseColorDialogFragment

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(layoutInflater, container, false)

        initAppearanceSetting()

        viewModel.loadSettings()
        return binding.root
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
                    requireActivity().recreate()
                }
            }
        }
        binding.rlDarkTheme.setOnClickListener {
            viewModel.onDarkThemeChange(!binding.msDarkTheme.isChecked)
        }
        binding.msDarkTheme.setOnCheckedChangeListener { _, isChecked ->
            viewModel.onDarkThemeChange(isChecked)
        }

    }


}