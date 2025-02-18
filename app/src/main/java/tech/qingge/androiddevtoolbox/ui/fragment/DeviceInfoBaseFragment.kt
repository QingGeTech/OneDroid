package tech.qingge.androiddevtoolbox.ui.fragment

import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.lifecycleScope
import com.hjq.permissions.XXPermissions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tech.qingge.androiddevtoolbox.R
import tech.qingge.androiddevtoolbox.base.BaseFragment
import tech.qingge.androiddevtoolbox.base.SimpleRvAdapter
import tech.qingge.androiddevtoolbox.data.entity.CardData
import tech.qingge.androiddevtoolbox.databinding.FragmentDeviceInfoBaseBinding
import tech.qingge.androiddevtoolbox.databinding.ItemAppBasicInfoBinding
import tech.qingge.androiddevtoolbox.databinding.ItemAppBasicInfoCardBinding
import tech.qingge.androiddevtoolbox.util.ClipboardUtil
import tech.qingge.androiddevtoolbox.util.CommonPermissionCallback


abstract class DeviceInfoBaseFragment : BaseFragment<FragmentDeviceInfoBaseBinding>() {

    protected lateinit var cardDataList: List<CardData>

    override fun initViews() {

        if (getNeedPermissions().isEmpty() || XXPermissions.isGranted(
                requireContext(),
                getNeedPermissions()
            )
        ) {
            loadData()
            return
        }

        binding.rv.visibility = View.GONE
        binding.btnRequestPermission.visibility = View.VISIBLE
        binding.btnRequestPermission.setOnClickListener {
            XXPermissions.with(requireActivity()).permission(getNeedPermissions())
                .request(object : CommonPermissionCallback(requireActivity()) {
                    override fun onAllGranted() {
                        loadData()
                    }
                })
        }

    }

    protected fun loadData() {
        binding.btnRequestPermission.visibility = View.GONE
        binding.pb.visibility = View.VISIBLE

        lifecycleScope.launch(Dispatchers.IO) {
            cardDataList = initCardDataList()
            withContext(Dispatchers.Main) {
                initRv()
                binding.pb.visibility = View.GONE
                binding.rv.visibility = View.VISIBLE
            }
        }
    }

    private fun initRv() {
        binding.rv.adapter =
            SimpleRvAdapter(
                cardDataList,
                ItemAppBasicInfoCardBinding::inflate
            ) { itemBinding, cardData, _ ->
                itemBinding.tv.text = cardData.title
                itemBinding.rv.adapter = SimpleRvAdapter(
                    cardData.pairs, ItemAppBasicInfoBinding::inflate
                ) { itemBindingInner, pair, _ ->
                    itemBindingInner.tvKey.text = pair.first
                    itemBindingInner.tvValue.text = pair.second
                    itemBindingInner.root.setOnLongClickListener {
                        val popupMenu = PopupMenu(requireContext(), itemBindingInner.tvValue)
                        popupMenu.inflate(R.menu.popup_menu_app_detail)
                        popupMenu.setOnMenuItemClickListener {
                            ClipboardUtil.copyToClipboard(
                                requireContext(),
                                itemBindingInner.tvValue.text.toString()
                            )
                            true
                        }
                        popupMenu.show()
                        return@setOnLongClickListener true
                    }
                }
            }
    }

    abstract suspend fun initCardDataList(): List<CardData>

    open fun getNeedPermissions(): List<String> {
        return emptyList()
    }

}