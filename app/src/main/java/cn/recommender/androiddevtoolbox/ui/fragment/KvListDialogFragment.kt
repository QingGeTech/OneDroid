package cn.recommender.androiddevtoolbox.ui.fragment

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.core.os.BundleCompat
import cn.recommender.androiddevtoolbox.R
import cn.recommender.androiddevtoolbox.base.SimpleRvAdapter
import cn.recommender.androiddevtoolbox.data.local.sp.SpApi
import cn.recommender.androiddevtoolbox.databinding.FragmentKvListBinding
import cn.recommender.androiddevtoolbox.databinding.ItemKvVerticleBinding
import cn.recommender.androiddevtoolbox.util.ClipboardUtil
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import java.io.Serializable
import javax.inject.Inject

@AndroidEntryPoint
class KvListDialogFragment @Inject constructor() : BottomSheetDialogFragment() {

    @Inject
    lateinit var spApi: SpApi

    private lateinit var binding: FragmentKvListBinding

    @Suppress("UNCHECKED_CAST")
    private fun initRv() {
        val kvList = BundleCompat.getSerializable(
            requireArguments(),
            "kvList",
            Serializable::class.java
        ) as List<Pair<String, String?>>

        binding.rv.adapter = SimpleRvAdapter(
            kvList,
            ItemKvVerticleBinding::inflate
        ) { itemBinding, pair, _ ->
            itemBinding.tvKey.text = pair.first
            itemBinding.tvValue.text =
                if (TextUtils.isEmpty(pair.second)) getString(R.string.null_str) else pair.second
            itemBinding.root.setOnLongClickListener {
                val popupMenu = PopupMenu(requireContext(), itemBinding.tvValue)
                popupMenu.inflate(R.menu.popup_menu_app_detail)
                popupMenu.setOnMenuItemClickListener {
                    ClipboardUtil.copyToClipboard(
                        requireContext(),
                        itemBinding.tvValue.text.toString()
                    )
                    true
                }
                popupMenu.show()
                return@setOnLongClickListener true
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentKvListBinding.inflate(layoutInflater)
        (dialog as BottomSheetDialog).behavior.state = BottomSheetBehavior.STATE_COLLAPSED
        initRv()

        return binding.root
    }

}