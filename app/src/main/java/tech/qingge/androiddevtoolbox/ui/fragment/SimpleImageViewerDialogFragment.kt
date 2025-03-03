package tech.qingge.androiddevtoolbox.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import tech.qingge.androiddevtoolbox.databinding.FragmentSimpleImageViewerBinding
import javax.inject.Inject

@AndroidEntryPoint
class SimpleImageViewerDialogFragment @Inject constructor() : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentSimpleImageViewerBinding

    private lateinit var imgBytes: ByteArray
    private lateinit var fileName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imgBytes = requireArguments().getByteArray("imgBytes")!!
        fileName = requireArguments().getString("fileName")!!
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSimpleImageViewerBinding.inflate(layoutInflater)
        (dialog as BottomSheetDialog).behavior.state = BottomSheetBehavior.STATE_COLLAPSED
        initView()
        return binding.root
    }

    private fun initView() {
        Glide.with(requireContext()).load(imgBytes).into(binding.img)

//        binding.img.setOnLongClickListener {
//            val popupMenu = PopupMenu(requireContext(), binding.img)
//            popupMenu.inflate(R.menu.popup_menu_image_viewer)
//            popupMenu.setOnMenuItemClickListener {
//                lifecycleScope.launch {
//                    val uri = FileUtil.getTmpFileUri(requireActivity(), fileName)
//                    LoadingDialog.show(requireContext())
//                    runCatching {
//                        requireContext().contentResolver.openOutputStream(uri).use {
//                            it!!.write(imgBytes)
//                        }
//                    }.onSuccess {
//                        LoadingDialog.dismiss()
//                        Dialogs.showMessageTips(
//                            requireContext(),
//                            getString(R.string.save_success)
//                        )
//                    }.onFailure {
//                        LoadingDialog.dismiss()
//                        Dialogs.showMessageTips(
//                            requireContext(),
//                            getString(R.string.save_fail)
//                        )
//                    }
//                }
//                true
//            }
//            popupMenu.show()
//            return@setOnLongClickListener true
//        }
    }
}