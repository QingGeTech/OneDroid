package cn.recommender.androiddevtoolbox.ui.fragment;

import android.content.pm.PackageInfo
import android.text.SpannableString
import android.util.Base64
import android.widget.TextView
import androidx.core.content.pm.PackageInfoCompat
import androidx.core.os.BundleCompat
import androidx.core.widget.PopupWindowCompat
import cn.recommender.androiddevtoolbox.R
import cn.recommender.androiddevtoolbox.base.BaseFragment
import cn.recommender.androiddevtoolbox.base.SimpleRvAdapter
import cn.recommender.androiddevtoolbox.data.entity.CardData
import cn.recommender.androiddevtoolbox.databinding.FragmentAppDetailSignInfoBinding
import cn.recommender.androiddevtoolbox.databinding.ItemAppBasicInfoBinding
import cn.recommender.androiddevtoolbox.databinding.ItemAppBasicInfoCardBinding
import cn.recommender.androiddevtoolbox.util.DateTimeUtil
import cn.recommender.androiddevtoolbox.util.LogUtil
import cn.recommender.androiddevtoolbox.util.base64
import cn.recommender.androiddevtoolbox.util.hex
import cn.recommender.androiddevtoolbox.util.md5
import cn.recommender.androiddevtoolbox.util.sha1
import cn.recommender.androiddevtoolbox.util.sha256
import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import java.io.ByteArrayInputStream
import java.security.MessageDigest
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import kotlin.math.sign

// TODO: 长按复制
@AndroidEntryPoint
class AppDetailSignInfoFragment @Inject constructor() :
    BaseFragment<FragmentAppDetailSignInfoBinding>() {

    private lateinit var packageInfo: PackageInfo

    private var cardDataList: MutableList<CardData> = mutableListOf()

    override fun initViews() {
        packageInfo =
            BundleCompat.getParcelable(requireArguments(), "packageInfo", PackageInfo::class.java)!!

        initData()
        initRv()

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
                ) { itemBindingInner, pair, index ->
                    if (index < 3) {
                        itemBindingInner.root.setOnClickListener {
                            toggleCase(itemBindingInner.tvValue)
                        }
                    }
                    itemBindingInner.tvKey.text = pair.first
                    itemBindingInner.tvValue.text = pair.second
                }
            }
    }

    private fun toggleCase(tvValue: TextView) {
        val text = tvValue.text
        if (text.any { it.isUpperCase() }) {
            tvValue.text = text.toString().lowercase()
        } else {
            tvValue.text = text.toString().uppercase()
        }
    }

    private fun initData() {
        val signatures = PackageInfoCompat.getSignatures(
            requireContext().packageManager, packageInfo.packageName
        )
        signatures.forEachIndexed { index, signature ->
            val signBytes = signature.toByteArray()
            val cf = CertificateFactory.getInstance("X.509")
            val cert = cf.generateCertificate(ByteArrayInputStream(signBytes)) as X509Certificate

            val pairs = listOf(
                Pair(getString(R.string.apk_sign_md5), signBytes.md5()),
                Pair(getString(R.string.apk_sign_sha1), signBytes.sha1()),
                Pair(getString(R.string.apk_sign_sha256), signBytes.sha256()),
                Pair(getString(R.string.type), cert.type),
                Pair(getString(R.string.version), cert.version.toString()),
                Pair(getString(R.string.serial_number), cert.serialNumber.toString()),
                Pair(getString(R.string.subject_dn), cert.subjectDN.toString()),
                Pair(
                    getString(R.string.valid_from),
                    DateTimeUtil.getFormattedDateTime(cert.notBefore)
                ),
                Pair(
                    getString(R.string.valid_until),
                    DateTimeUtil.getFormattedDateTime(cert.notAfter)
                ),
                Pair(
                    getString(R.string.sign_alg_name),
                    cert.sigAlgName
                ),
                Pair(
                    getString(R.string.sign_alg_oid),
                    cert.sigAlgOID
                ),
                Pair(getString(R.string.sign_alg_params), cert.sigAlgParams.hex()),
                Pair(getString(R.string.cert_sign_sha256), cert.signature.sha256()),
            )

            val cardData = CardData("${getString(R.string.signature)} : #${index + 1}", pairs)
            cardDataList.add(cardData)
        }

    }

}
