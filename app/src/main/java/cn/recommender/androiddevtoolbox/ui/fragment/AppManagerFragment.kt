package cn.recommender.androiddevtoolbox.ui.fragment

import android.annotation.SuppressLint
import android.app.Application
import android.content.Intent
import android.content.pm.PackageInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.Transition
import androidx.transition.TransitionManager
import cn.recommender.androiddevtoolbox.R
import cn.recommender.androiddevtoolbox.base.BaseFragment
import cn.recommender.androiddevtoolbox.base.SimpleRvAdapter
import cn.recommender.androiddevtoolbox.data.local.sp.SpApi
import cn.recommender.androiddevtoolbox.databinding.FragmentAppManagerBinding
import cn.recommender.androiddevtoolbox.databinding.ItemAppListBinding
import cn.recommender.androiddevtoolbox.ui.activity.AppDetailActivity
import cn.recommender.androiddevtoolbox.util.LogUtil
import cn.recommender.androiddevtoolbox.util.PackageManagerUtil
import cn.recommender.androiddevtoolbox.util.SoftKeyboardUtil
import cn.recommender.androiddevtoolbox.viewmodel.AppManagerViewModel
import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

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
        adapter =
            SimpleRvAdapter(
                emptyList(),
                ItemAppListBinding::inflate
            ) { itemBinding, item, _ ->
                itemBinding.ivLogo.setImageDrawable(
                    PackageManagerUtil.getAppIcon(
                        item,
                        requireContext()
                    )
                )
                itemBinding.tvAppName.text =
                    PackageManagerUtil.getAppName(item, requireContext())
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
                    requireContext(),
                    binding.sv.findViewById<View>(R.id.search_src_text)
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