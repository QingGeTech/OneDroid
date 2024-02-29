package cn.recommender.androiddevtoolbox.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.recommender.androiddevtoolbox.base.BaseFragment
import cn.recommender.androiddevtoolbox.databinding.FragmentAppManagerBinding

object AppManagerFragment : BaseFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentAppManagerBinding.inflate(layoutInflater, container, false)
        return binding.root
    }
}