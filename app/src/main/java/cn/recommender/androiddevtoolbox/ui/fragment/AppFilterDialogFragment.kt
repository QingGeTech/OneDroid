package cn.recommender.androiddevtoolbox.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.recommender.androiddevtoolbox.databinding.FragmentFilterAppBinding
import cn.recommender.androiddevtoolbox.util.LogUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AppFilterDialogFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentFilterAppBinding

    private val TAG = javaClass.name

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFilterAppBinding.inflate(inflater, container, false)
        binding.btnGroupAppType.addOnButtonCheckedListener { group, checkedId, isChecked ->
            LogUtil.d(TAG,"listener:$group, $checkedId, $isChecked")
        }
        return binding.root
    }

}