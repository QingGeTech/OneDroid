package cn.recommender.androiddevtoolbox.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

/**
 * view pager2 adapter in MainActivity
 */
class MainVpFragmentAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    val fragments: List<Fragment>
) :
    FragmentStateAdapter(fragmentManager, lifecycle) {


    override fun getItemCount() = fragments.size

    override fun createFragment(position: Int) = fragments[position]
}