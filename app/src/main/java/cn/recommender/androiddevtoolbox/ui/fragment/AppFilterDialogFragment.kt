package cn.recommender.androiddevtoolbox.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.recommender.androiddevtoolbox.databinding.FragmentFilterAppBinding
import cn.recommender.androiddevtoolbox.util.LogUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Singleton

@AndroidEntryPoint
@Singleton
class AppFilterDialogFragment @Inject constructor() : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentFilterAppBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFilterAppBinding.inflate(inflater, container, false)
        binding.btnGroupAppType.addOnButtonCheckedListener { group, checkedId, isChecked ->
            LogUtil.d("listener:$group, $checkedId, $isChecked")
        }
        return binding.root
    }

}