package cn.recommender.androiddevtoolbox.ui.fragment

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import cn.recommender.androiddevtoolbox.R
import cn.recommender.androiddevtoolbox.data.local.sp.SpApi
import cn.recommender.androiddevtoolbox.databinding.FragmentChooseColorBinding
import cn.recommender.androiddevtoolbox.ui.adapter.ChooseColorRvAdapter
import cn.recommender.androiddevtoolbox.util.LogUtil
import cn.recommender.androiddevtoolbox.ui.view.colorpicker.ColorPickerViewGroup
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.internal.EdgeToEdgeUtils
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ChooseColorDialogFragment @Inject constructor() : BottomSheetDialogFragment() {

    @Inject
    lateinit var spApi: SpApi

    private lateinit var binding: FragmentChooseColorBinding

    private lateinit var bottomSheetDialog: BottomSheetDialog

    interface Callback {
        fun onChooseColor(color: Int)
    }

    var callback: Callback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LogUtil.d("onCreate")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        LogUtil.d("onCreateView")
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        LogUtil.d("onCreateDialog")
        bottomSheetDialog = BottomSheetDialog(requireContext(), theme)
        binding = FragmentChooseColorBinding.inflate(layoutInflater)
        initRv()
        bottomSheetDialog.setContentView(binding.root)
        binding.llDialogCollapse.post {
            bottomSheetDialog.behavior.setPeekHeight(
                binding.llDialogCollapse.height,
                false
            )
        }
        bottomSheetDialog.behavior.addBottomSheetCallback(object : BottomSheetCallback() {
            override fun onStateChanged(p0: View, p1: Int) {
                LogUtil.d("onStateChanged:${view}, $p1")
                if (p1 == BottomSheetBehavior.STATE_EXPANDED) {
                    binding.colorPicker.startColorPick(spApi.getThemeColor())
                } else if (p1 == BottomSheetBehavior.STATE_COLLAPSED) {
                    binding.colorPicker.stopColorPick()
                }
            }

            override fun onSlide(p0: View, p1: Float) {
                LogUtil.d("onSlide:${view},$p1")
            }
        })

        binding.colorPicker.onColorChangeListener =
            object : ColorPickerViewGroup.OnColorChangeListener {
                override fun onColorChange(color: Int) {
                    (binding.rv.adapter as ChooseColorRvAdapter).paletteAsColor = color
                    (binding.rv.adapter as ChooseColorRvAdapter).notifyItemChanged(0)
                }
            }

        return bottomSheetDialog
    }

    private fun initRv() {
        val adapter = ChooseColorRvAdapter()
        binding.rv.adapter = adapter
        adapter.callback = object : ChooseColorRvAdapter.Callback {
            override fun onChooseColor(color: Int) {
                callback?.onChooseColor(color)
                this@ChooseColorDialogFragment.dismiss()
            }

            override fun onOpenPalette() {
                (dialog as BottomSheetDialog).behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
    }

}