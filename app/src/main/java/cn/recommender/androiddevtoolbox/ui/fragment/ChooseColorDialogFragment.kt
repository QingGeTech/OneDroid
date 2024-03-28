package cn.recommender.androiddevtoolbox.ui.fragment

import android.app.Dialog
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import cn.recommender.androiddevtoolbox.R
import cn.recommender.androiddevtoolbox.databinding.FragmentChooseColorBinding
import cn.recommender.androiddevtoolbox.ui.adapter.ChooseColorRvAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.internal.EdgeToEdgeUtils
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ChooseColorDialogFragment @Inject constructor() : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentChooseColorBinding

    interface Callback {
        fun onChooseColor(color: Int)
    }

    var callback: Callback? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = BottomSheetDialog(requireContext(), theme)
        binding = FragmentChooseColorBinding.inflate(layoutInflater)
        initRv()
        bottomSheetDialog.setContentView(binding.root)
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

            }
        }
    }

}