package tech.qingge.onedroid.ui.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageInfo
import android.view.View
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.fragment.app.viewModels
import androidx.transition.Transition
import androidx.transition.TransitionManager
import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint
import tech.qingge.onedroid.R
import tech.qingge.onedroid.base.BaseFragment
import tech.qingge.onedroid.base.SimpleRvAdapter
import tech.qingge.onedroid.data.local.sp.SpApi
import tech.qingge.onedroid.databinding.FragmentAppManagerBinding
import tech.qingge.onedroid.databinding.ItemAppListBinding
import tech.qingge.onedroid.ui.activity.AppDetailActivity
import tech.qingge.onedroid.util.LogUtil
import tech.qingge.onedroid.util.PackageManagerUtil
import tech.qingge.onedroid.util.SoftKeyboardUtil
import tech.qingge.onedroid.viewmodel.AppManagerViewModel
import javax.inject.Inject

@AndroidEntryPoint
class AppManagerFragment @Inject constructor() : BaseFragment<FragmentAppManagerBinding>() {

    @Inject
    lateinit var spApi: SpApi

    @Inject
    lateinit var appFilterDialogFragment: AppFilterDialogFragment

    private lateinit var openSearchViewTransition: Transition
    private lateinit var closeSearchViewTransition: Transition

    private val viewModel: AppManagerViewModel by viewModels()

    private lateinit var adapter: SimpleRvAdapter<PackageInfo, ItemAppListBinding>

    override fun initViews() {
        initSearchView()

        initToolbar()

        initRv()

        initObserver()

        viewModel.initAppList()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initObserver() {
        viewModel.appList.observe(viewLifecycleOwner) {
            adapter.items = it
            adapter.notifyDataSetChanged()
        }
    }

    private fun initRv() {
        adapter = SimpleRvAdapter(
            emptyList(), ItemAppListBinding::inflate
        ) { itemBinding, item, _ ->
            itemBinding.ivLogo.setImageDrawable(
                PackageManagerUtil.getAppIcon(
                    item, requireContext()
                )
            )
            itemBinding.tvAppName.text = PackageManagerUtil.getAppName(item, requireContext())
            itemBinding.tvPkgName.text = item.packageName
            itemBinding.root.setOnClickListener {
                val intent = Intent(requireContext(), AppDetailActivity::class.java)
                intent.putExtra("packageName", item.packageName)
                requireContext().startActivity(intent)
            }
        }

        binding.rv.adapter = adapter

    }

    private fun initToolbar() {
        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.search -> {
                    openSearchView()
                    viewModel.startSearch()
                }

                R.id.filter -> openFilterSheet()
            }
            true
        }
    }

    private fun initTransition() {
        openSearchViewTransition = createSearchViewTransition(true)
        closeSearchViewTransition = createSearchViewTransition(false)
    }

    private fun initSearchView() {
        initTransition()
        binding.sv.setOnClickListener {
            closeSearchView()
            viewModel.stopSearch()
        }
        binding.sv.setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                LogUtil.d("onQueryTextSubmit: $query")
                SoftKeyboardUtil.hideSoftInput(
                    requireContext(), binding.sv.findViewById(R.id.search_src_text)!!
                )
                return true
            }

            override fun onQueryTextChange(keyword: String?): Boolean {
                LogUtil.d("onQueryTextChange:$keyword")
                viewModel.filterAppList(keyword!!)
                return true
            }
        })
        binding.sv.setOnQueryTextFocusChangeListener { _, hasFocus ->
            LogUtil.d("onQueryTextFocusChange:$hasFocus")
            if (hasFocus) {
                SoftKeyboardUtil.showSoftInput(
                    requireContext(), binding.sv.findViewById(R.id.search_src_text)!!
                )
            }
        }

    }

    private fun closeSearchView() {
        TransitionManager.beginDelayedTransition(binding.toolbar, closeSearchViewTransition)
        binding.toolbar.visibility = View.VISIBLE
        binding.sv.visibility = View.GONE
        binding.sv.setQuery("", false)
    }

    private fun openFilterSheet() {
        appFilterDialogFragment.show(childFragmentManager, "filterAppFragment")
        appFilterDialogFragment.onFilter = {
            viewModel.initAppList()
        }
    }

    private fun openSearchView() {
        TransitionManager.beginDelayedTransition(binding.toolbar, openSearchViewTransition)
        binding.toolbar.visibility = View.INVISIBLE
        binding.sv.visibility = View.VISIBLE
        binding.sv.requestFocus()
    }

    private fun createSearchViewTransition(entering: Boolean): MaterialSharedAxis {
        val sharedAxisTransition = MaterialSharedAxis(MaterialSharedAxis.X, entering)
        sharedAxisTransition.addTarget(binding.toolbar)
        sharedAxisTransition.addTarget(binding.sv)
        return sharedAxisTransition
    }

}