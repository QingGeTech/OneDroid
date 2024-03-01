package cn.recommender.androiddevtoolbox.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.transition.Transition
import androidx.transition.TransitionManager
import cn.recommender.androiddevtoolbox.R
import cn.recommender.androiddevtoolbox.base.BaseFragment
import cn.recommender.androiddevtoolbox.databinding.FragmentAppManagerBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.transition.MaterialSharedAxis

object AppManagerFragment : BaseFragment() {

    private val TAG = javaClass.name

    private lateinit var binding: FragmentAppManagerBinding

    private lateinit var openSearchViewTransition: Transition
    private lateinit var closeSearchViewTransition: Transition

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAppManagerBinding.inflate(layoutInflater, container, false)

        initTransition()
        initSearchView()

        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.search -> openSearchView()
                R.id.filter -> openFilterSheet()
            }
            true
        }

        return binding.root
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
        AppFilterDialogFragment.show(childFragmentManager,"filterAppFragment")
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