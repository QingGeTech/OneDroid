package cn.recommender.androiddevtoolbox.ui.fragment;

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageInfo
import android.os.Bundle
import android.text.SpannableString
import android.util.Base64
import android.view.View
import android.widget.TextView
import androidx.core.content.pm.PackageInfoCompat
import androidx.core.os.BundleCompat
import androidx.core.widget.PopupWindowCompat
import cn.recommender.androiddevtoolbox.R
import cn.recommender.androiddevtoolbox.base.BaseFragment
import cn.recommender.androiddevtoolbox.base.SimpleRvAdapter
import cn.recommender.androiddevtoolbox.data.entity.CardData
import cn.recommender.androiddevtoolbox.databinding.FragmentAppDetailActivityInfoBinding
import cn.recommender.androiddevtoolbox.databinding.FragmentAppDetailPermissionInfoBinding
import cn.recommender.androiddevtoolbox.databinding.FragmentAppDetailServiceInfoBinding
import cn.recommender.androiddevtoolbox.databinding.FragmentAppDetailSignInfoBinding
import cn.recommender.androiddevtoolbox.databinding.ItemActivityListBinding
import cn.recommender.androiddevtoolbox.databinding.ItemAppBasicInfoBinding
import cn.recommender.androiddevtoolbox.databinding.ItemAppBasicInfoCardBinding
import cn.recommender.androiddevtoolbox.databinding.ItemAppListBinding
import cn.recommender.androiddevtoolbox.databinding.ItemServiceListBinding
import cn.recommender.androiddevtoolbox.util.DateTimeUtil
import cn.recommender.androiddevtoolbox.util.LogUtil
import cn.recommender.androiddevtoolbox.util.base64
import cn.recommender.androiddevtoolbox.util.hex
import cn.recommender.androiddevtoolbox.util.md5
import cn.recommender.androiddevtoolbox.util.sha1
import cn.recommender.androiddevtoolbox.util.sha256
import com.bumptech.glide.Glide
import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import java.io.ByteArrayInputStream
import java.io.Serializable
import java.security.MessageDigest
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import kotlin.math.sign

// TODO: 长按复制
// TODO: 解析Activity含义
@AndroidEntryPoint
class AppDetailPermissionInfoFragment @Inject constructor() :
    BaseFragment<FragmentAppDetailPermissionInfoBinding>() {

    private lateinit var packageInfo: PackageInfo


    override fun initViews() {
        packageInfo =
            BundleCompat.getParcelable(requireArguments(), "packageInfo", PackageInfo::class.java)!!

        initRv()
    }

    @SuppressLint("SetTextI18n")
    private fun initRv() {
//        binding.rv.adapter
    }

}
