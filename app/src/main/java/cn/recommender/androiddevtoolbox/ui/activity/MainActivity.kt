package cn.recommender.androiddevtoolbox.ui.activity

import android.os.Bundle
import androidx.core.view.MenuProvider
import androidx.core.view.get
import androidx.lifecycle.ReportFragment.Companion.reportFragment
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import cn.recommender.androiddevtoolbox.R
import cn.recommender.androiddevtoolbox.base.BaseActivity
import cn.recommender.androiddevtoolbox.databinding.ActivityMainBinding
import cn.recommender.androiddevtoolbox.ui.adapter.MainVpFragmentAdapter
import cn.recommender.androiddevtoolbox.ui.fragment.AppManagerFragment
import cn.recommender.androiddevtoolbox.ui.fragment.DeviceInfoFragment
import cn.recommender.androiddevtoolbox.util.Utils

class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.vp.adapter = MainVpFragmentAdapter(
            supportFragmentManager,
            lifecycle,
            listOf(AppManagerFragment, DeviceInfoFragment)
        )
        binding.vp.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                binding.bnv.selectedItemId = binding.bnv.menu[position].itemId
                initToolbar(position)
            }
        })
        binding.bnv.setOnItemSelectedListener {
            val position = Utils.findMenuPosition(binding.bnv.menu, it)
            binding.vp.setCurrentItem(position, true)
            true
        }
    }


    private fun initToolbar(position: Int) {
        val menuResMap = mapOf(0 to R.menu.menu_app_manager)
        binding.toolbar.title = binding.bnv.menu[position].title
        binding.toolbar.menu.clear()
        menuResMap[position]?.let { binding.toolbar.inflateMenu(it) }
    }
}