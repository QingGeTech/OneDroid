package cn.recommender.androiddevtoolbox.ui.fragment

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
import androidx.core.content.res.ResourcesCompat.ThemeCompat
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.Transition
import androidx.transition.TransitionManager
import cn.recommender.androiddevtoolbox.App
import cn.recommender.androiddevtoolbox.R
import cn.recommender.androiddevtoolbox.base.BaseFragment
import cn.recommender.androiddevtoolbox.databinding.FragmentAppManagerBinding
import cn.recommender.androiddevtoolbox.ui.adapter.AppListRvAdapter
import cn.recommender.androiddevtoolbox.viewmodel.AppManagerViewModel
import com.google.android.material.transition.MaterialSharedAxis

class AppManagerFragment : BaseFragment() {

    companion object {
        private const val TAG = "AppManagerFragment"
    }

    private lateinit var binding: FragmentAppManagerBinding

    private lateinit var openSearchViewTransition: Transition
    private lateinit var closeSearchViewTransition: Transition

    private val viewModel by activityViewModels<AppManagerViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentAppManagerBinding.inflate(layoutInflater, container, false)

        initTransition()
        initSearchView()

        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.search -> openSearchView()
                R.id.filter -> openFilterSheet()
                R.id.theme -> chooseTheme()
            }
            true
        }

        binding.rv.apply {
            adapter = AppListRvAdapter(emptyList())
            layoutManager = LinearLayoutManager(requireContext())
        }

        viewModel.appList.observe(viewLifecycleOwner) {
            (binding.rv.adapter as AppListRvAdapter).appData = it
            (binding.rv.adapter as AppListRvAdapter).notifyDataSetChanged()
        }

        viewModel.loadAppData()

        return binding.root
    }

    /**
     * TODO: 测试，后续移动到个性化模块
     */
    private fun chooseTheme() {

        AlertDialog.Builder(requireContext()).setAdapter(
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_list_item_1,
                listOf("Light", "Dark", "AppTheme1", "AppTheme2")
            )
        ) { _, position ->
            when (position) {
                0 -> {
                    AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)
                }

                1 -> {
                    AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES)
                }

                2 -> {
                    App.sp.setTheme(R.style.AppTheme)
                    requireActivity().recreate()
                }

                3 -> {
                    App.sp.setTheme(R.style.AppTheme2)
                    requireActivity().recreate()
                }
            }
        }.show()
    }

    private fun initTransition() {
        openSearchViewTransition = createSearchViewTransition(true)
        closeSearchViewTransition = createSearchViewTransition(false)
    }

    private fun initSearchView() {
        binding.sv.setOnClickListener {
            closeSearchView()
        }
        binding.sv.setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false

            override fun onQueryTextChange(newText: String?): Boolean {
//                TODO("filter apps")
                return true
            }
        })
    }

    private fun closeSearchView() {
        TransitionManager.beginDelayedTransition(binding.toolbar, closeSearchViewTransition)
        binding.toolbar.visibility = View.VISIBLE
        binding.sv.visibility = View.GONE
        binding.sv.setQuery("", false)
    }

    private fun openFilterSheet() {
        AppFilterDialogFragment().show(childFragmentManager, "filterAppFragment")
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