package cn.recommender.androiddevtoolbox.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.recommender.androiddevtoolbox.R
import cn.recommender.androiddevtoolbox.base.BaseFragment
import cn.recommender.androiddevtoolbox.databinding.FragmentSettingsBinding
import cn.recommender.androiddevtoolbox.databinding.FragmentSysInfoBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Singleton


@AndroidEntryPoint
class SysInfoFragment @Inject constructor() : BaseFragment<FragmentSysInfoBinding>() {

    override fun initViews() {
    }

}