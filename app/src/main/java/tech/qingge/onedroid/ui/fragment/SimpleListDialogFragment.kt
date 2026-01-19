package tech.qingge.onedroid.ui.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.toDrawable
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import tech.qingge.onedroid.base.SimpleRvAdapter
import tech.qingge.onedroid.databinding.FragmentSimpleListBinding
import tech.qingge.onedroid.databinding.ItemStringBinding
import javax.inject.Inject

@AndroidEntryPoint
class SimpleListDialogFragment @Inject constructor() : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentSimpleListBinding

    private lateinit var listData: List<String>

    lateinit var onClickItem: (Int) -> Unit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        listData = requireArguments().getStringArrayList("listData")!!
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSimpleListBinding.inflate(layoutInflater)
        (dialog as BottomSheetDialog).behavior.state = BottomSheetBehavior.STATE_COLLAPSED
        initView()
        return binding.root
    }

    private fun initView() {
        val divider =
            DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL).apply {
                setDrawable(Color.GRAY.toDrawable())
            }
        binding.rv.addItemDecoration(divider)
        binding.rv.adapter = SimpleRvAdapter<String, ItemStringBinding>(
            listData,
            ItemStringBinding::inflate
        ) { itemBinding, item, index ->
            itemBinding.tv.text = item
            itemBinding.root.setOnClickListener { onClickItem(index)}
        }
    }
}