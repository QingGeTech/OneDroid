package tech.qingge.onedroid.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import tech.qingge.onedroid.Constants
import tech.qingge.onedroid.R
import tech.qingge.onedroid.data.local.sp.SpApi
import tech.qingge.onedroid.databinding.FragmentFilterAppBinding
import tech.qingge.onedroid.util.LogUtil
import tech.qingge.onedroid.util.ViewUtil
import tech.qingge.onedroid.util.reverse
import javax.inject.Inject

@AndroidEntryPoint
class AppFilterDialogFragment @Inject constructor() : BottomSheetDialogFragment() {

    @Inject
    lateinit var spApi: SpApi

    private lateinit var binding: FragmentFilterAppBinding

    var onFilter: (() -> Unit)? = null


    private val idTypeMap = hashMapOf(
        R.id.btnAll to Constants.APP_FILTER_TYPE_ALL,
        R.id.btnSystem to Constants.APP_FILTER_TYPE_SYSTEM,
        R.id.btnUser to Constants.APP_FILTER_TYPE_USER
    )

    private val typeIdMap = idTypeMap.reverse()

    private val sortIdTypeMap = hashMapOf(
        R.id.btnSortTypeAppName to Constants.APP_SORT_TYPE_APP_NAME,
        R.id.btnSortTypeInstallTime to Constants.APP_SORT_TYPE_INSTALL_TIME
    )

    private val sortTypeIdMap = sortIdTypeMap.reverse()

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



        binding.btnGroupSortType.check(sortTypeIdMap[spApi.getAppSortType()]!!)
        updateArrowDrawable(sortTypeIdMap[spApi.getAppSortType()]!!, spApi.isAppSortDesc())

//        binding.btnGroupSortType.addOnButtonCheckedListener { group, checkedId, isChecked ->
//            LogUtil.d("listener:$group, $checkedId, $isChecked")
//
//        }

        binding.btnSortTypeAppName.setOnClickListener {
            if (spApi.getAppSortType() == Constants.APP_SORT_TYPE_APP_NAME) {
                updateArrowDrawable(R.id.btnSortTypeAppName, spApi.isAppSortDesc().not())
                spApi.setAppSortDesc(spApi.isAppSortDesc().not())
            } else {
                updateArrowDrawable(R.id.btnSortTypeAppName, spApi.isAppSortDesc())
                binding.btnSortTypeInstallTime.setCompoundDrawables(null, null, null, null)
                spApi.setAppSortType(Constants.APP_SORT_TYPE_APP_NAME)
            }
            onFilter?.invoke()
        }

        binding.btnSortTypeInstallTime.setOnClickListener {
            if (spApi.getAppSortType() == Constants.APP_SORT_TYPE_INSTALL_TIME) {
                updateArrowDrawable(R.id.btnSortTypeInstallTime, spApi.isAppSortDesc().not())
                spApi.setAppSortDesc(spApi.isAppSortDesc().not())
            } else {
                updateArrowDrawable(R.id.btnSortTypeInstallTime, spApi.isAppSortDesc())
                binding.btnSortTypeAppName.setCompoundDrawables(null, null, null, null)
                spApi.setAppSortType(Constants.APP_SORT_TYPE_INSTALL_TIME)
            }
            onFilter?.invoke()
        }

        return binding.root
    }

    private fun updateArrowDrawable(id: Int, isDesc: Boolean) {
        val btn = binding.btnGroupSortType.findViewById<Button>(id)!!
        val arrow =
            if (isDesc) ResourcesCompat.getDrawable(resources, R.drawable.ic_arrow_down, null) else
                ResourcesCompat.getDrawable(resources, R.drawable.ic_arrow_up, null)
        DrawableCompat.setTint(
            arrow!!,
            ViewUtil.getColorByStyledAttr(requireContext(), R.attr.colorOnSurface)
        )
        btn.setCompoundDrawablesWithIntrinsicBounds(null, null, arrow, null)
    }

}