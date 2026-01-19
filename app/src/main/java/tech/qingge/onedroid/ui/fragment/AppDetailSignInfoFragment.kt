package tech.qingge.onedroid.ui.fragment

import android.content.pm.PackageInfo
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.pm.PackageInfoCompat
import dagger.hilt.android.AndroidEntryPoint
import tech.qingge.onedroid.R
import tech.qingge.onedroid.base.BaseFragment
import tech.qingge.onedroid.base.SimpleRvAdapter
import tech.qingge.onedroid.data.entity.CardData
import tech.qingge.onedroid.data.local.sys.SysApi
import tech.qingge.onedroid.databinding.FragmentAppDetailSignInfoBinding
import tech.qingge.onedroid.databinding.ItemAppBasicInfoBinding
import tech.qingge.onedroid.databinding.ItemAppBasicInfoCardBinding
import tech.qingge.onedroid.util.ClipboardUtil
import tech.qingge.onedroid.util.DateTimeUtil
import tech.qingge.onedroid.util.hex
import tech.qingge.onedroid.util.md5
import tech.qingge.onedroid.util.sha1
import tech.qingge.onedroid.util.sha256
import java.io.ByteArrayInputStream
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import javax.inject.Inject

// TODO: 长按复制
@AndroidEntryPoint
class AppDetailSignInfoFragment @Inject constructor() :
    BaseFragment<FragmentAppDetailSignInfoBinding>() {

    private lateinit var packageInfo: PackageInfo

    private var cardDataList: MutableList<CardData> = mutableListOf()

    @Inject
    lateinit var sysApi: SysApi

    override fun initViews() {
//        packageInfo =
//            BundleCompat.getParcelable(requireArguments(), "packageInfo", PackageInfo::class.java)!!

        val packageName = requireArguments().getString("packageName")
        packageInfo = sysApi.getPackageInfo(packageName!!)

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
                    itemBindingInner.root.setOnLongClickListener {
                        val popupMenu = PopupMenu(requireContext(), itemBindingInner.tvValue)
                        popupMenu.inflate(R.menu.popup_menu_app_detail)
                        popupMenu.setOnMenuItemClickListener {
                            ClipboardUtil.copyToClipboard(
                                requireContext(),
                                itemBindingInner.tvValue.text.toString()
                            )
                            true
                        }
                        popupMenu.show()
                        return@setOnLongClickListener true
                    }
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

            val pairs = mutableListOf(
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
