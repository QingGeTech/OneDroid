package cn.recommender.androiddevtoolbox.ui.activity

import android.content.Intent
import android.os.Bundle
import cn.recommender.androiddevtoolbox.base.BaseActivity
import cn.recommender.androiddevtoolbox.databinding.ActivityAppDetailBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AppDetailActivity : BaseActivity() {

    private lateinit var binding: ActivityAppDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAppDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
    }

    private fun initViews() {

    }

}