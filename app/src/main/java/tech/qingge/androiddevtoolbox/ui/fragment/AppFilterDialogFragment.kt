package tech.qingge.androiddevtoolbox.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import tech.qingge.androiddevtoolbox.Constants
import tech.qingge.androiddevtoolbox.R
import tech.qingge.androiddevtoolbox.data.local.sp.SpApi
import tech.qingge.androiddevtoolbox.databinding.FragmentFilterAppBinding
import tech.qingge.androiddevtoolbox.util.LogUtil
import tech.qingge.androiddevtoolbox.util.reverse
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AppFilterDialogFragment @Inject constructor() : BottomSheetDialogFragment() {

    @Inject
    lateinit var spApi: SpApi

    private lateinit var binding: FragmentFilterAppBinding

    var onFilter: (() -> Unit)? = null


    //TODO:use bimap
    private val idTypeMap = hashMapOf(
        R.id.btnAll to Constants.APP_FILTER_TYPE_ALL,
        R.id.btnSystem to Constants.APP_FILTER_TYPE_SYSTEM,
        R.id.btnUser to Constants.APP_FILTER_TYPE_USER
    )

    private val typeIdMap = idTypeMap.reverse()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFilterAppBinding.inflate(inflater, container, false)

        binding.btnGroupAppType.check(typeIdMap[spApi.getAppFilterType()]!!)

        binding.btnGroupAppType.addOnButtonCheckedListener { group, checkedId, isChecked ->
            LogUtil.d("listener:$group, $checkedId, $isChecked")
            if (isChecked) {
                spApi.setAppFilterType(idTypeMap[checkedId]!!)
                onFilter?.invoke()
            }
        }
        return binding.root
    }

}