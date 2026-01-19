package tech.qingge.onedroid.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import tech.qingge.onedroid.databinding.FragmentSimpleTextViewerBinding
import javax.inject.Inject

@AndroidEntryPoint
class SimpleTextViewerDialogFragment @Inject constructor() : BottomSheetDialogFragment(){

    private lateinit var binding: FragmentSimpleTextViewerBinding

    private lateinit var text:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        text = requireArguments().getString("text")!!
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSimpleTextViewerBinding.inflate(layoutInflater)
        (dialog as BottomSheetDialog).behavior.state = BottomSheetBehavior.STATE_COLLAPSED
        initView()
        return binding.root
    }

    private fun initView() {
        binding.tv.text = text
    }
}