package cn.recommender.androiddevtoolbox.ui.activity

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import cn.recommender.androiddevtoolbox.R
import cn.recommender.androiddevtoolbox.base.BaseActivity
import cn.recommender.androiddevtoolbox.databinding.ActivityMainBinding
import cn.recommender.androiddevtoolbox.ui.fragment.AppManagerFragment
import cn.recommender.androiddevtoolbox.ui.fragment.DeviceInfoFragment
import cn.recommender.androiddevtoolbox.util.LogUtil
import cn.recommender.androiddevtoolbox.util.Utils
import com.google.android.material.transition.MaterialFadeThrough

class MainActivity : BaseActivity() {

    private val TAG: String = javaClass.name

    private lateinit var binding: ActivityMainBinding

    private val fragments = listOf(AppManagerFragment, DeviceInfoFragment)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        switchFragment(0)
        binding.bnv.setOnItemSelectedListener {
            val position = Utils.findMenuPosition(binding.bnv.menu, it)
            switchFragment(position)
            true
        }
    }

    private fun switchFragment(position: Int) {
        supportFragmentManager.beginTransaction().replace(R.id.container, fragments[position])
            .setReorderingAllowed(true)
            .commit()
    }

}