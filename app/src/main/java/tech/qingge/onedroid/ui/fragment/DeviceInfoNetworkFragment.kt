package tech.qingge.onedroid.ui.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.net.wifi.WifiManager
import android.os.Build
import android.telephony.SubscriptionInfo
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import dagger.hilt.android.AndroidEntryPoint
import tech.qingge.onedroid.R
import tech.qingge.onedroid.data.entity.CardData
import tech.qingge.onedroid.util.DeviceUtil
import tech.qingge.onedroid.util.DeviceUtil.getGatewayAddress
import tech.qingge.onedroid.util.DeviceUtil.getSubnetMask
import tech.qingge.onedroid.util.DeviceUtil.getV4Ip
import tech.qingge.onedroid.util.DeviceUtil.getV6Ip
import javax.inject.Inject


@AndroidEntryPoint
class DeviceInfoNetworkFragment @Inject constructor() : DeviceInfoBaseFragment() {


    @SuppressLint("MissingPermission")
    override suspend fun initCardDataList(): List<CardData> {
        val cardDataList = mutableListOf(
            CardData(getString(R.string.wifi_info), getWifiInfo()),
            CardData(getString(R.string.ip_info), getIpInfo()),
            CardData(getString(R.string.sim_info), getSimInfo())
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            val subManager =
                requireContext().getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager
            val subInfoList = subManager.activeSubscriptionInfoList
            subInfoList?.forEach { sub ->
                cardDataList.add(
                    CardData(
                        getString(R.string.sim_info) + "#" + sub.simSlotIndex,
                        getSimSubInfo(sub)
                    )
                )
            }
        }

        return cardDataList

    }

    @SuppressLint("NewApi")
    private fun getSimSubInfo(sub: SubscriptionInfo): MutableList<Pair<String, String>> {
        val infos = mutableListOf<Pair<String, String>>(
            Pair(getString(R.string.sim_subcription_id), sub.subscriptionId.toString()),
            Pair(getString(R.string.sim_card_id), sub.cardId.toString()),
            Pair(getString(R.string.sim_carrier_id), sub.carrierId.toString()),
            Pair(getString(R.string.sim_carrier_name), sub.carrierName.toString()),
            Pair(getString(R.string.sim_country_iso), sub.countryIso),
            Pair(getString(R.string.sim_mcc), sub.mccString ?: ""),
            Pair(getString(R.string.sim_mnc), sub.mncString ?: ""),
            Pair(getString(R.string.sim_is_embedded), sub.isEmbedded.toString()),
            Pair(getString(R.string.sim_display_name), sub.displayName.toString()),
        )

        return infos
    }

    @SuppressLint("MissingPermission")
    private fun getSimInfo(): MutableList<Pair<String, String>> {


        val telephonyManager =
            requireContext().getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        val infos = mutableListOf<Pair<String, String>>(
            Pair(getString(R.string.sim_state), getSimState()),
            Pair(getString(R.string.sim_operator), telephonyManager.simOperator),
            Pair(getString(R.string.sim_country_iso), telephonyManager.simCountryIso),
            Pair(getString(R.string.sim_operator_name), telephonyManager.simOperatorName),
            Pair(getString(R.string.network_operator), telephonyManager.networkOperator),
            Pair(getString(R.string.network_operator_name), telephonyManager.networkOperatorName),
            Pair(getString(R.string.network_type), getNetworkType(telephonyManager)),
        )

        return infos

    }

    @SuppressLint("MissingPermission")  // already has permission
    fun getNetworkType(telephonyManager: TelephonyManager): String {
        return when (telephonyManager.networkType) {
            TelephonyManager.NETWORK_TYPE_GPRS -> "2G GPRS"
            TelephonyManager.NETWORK_TYPE_EDGE -> "2G EDGE"
            TelephonyManager.NETWORK_TYPE_UMTS -> "3G UMTS"
            TelephonyManager.NETWORK_TYPE_HSDPA -> "3G HSDPA"
            TelephonyManager.NETWORK_TYPE_HSUPA -> "3G HSUPA"
            TelephonyManager.NETWORK_TYPE_HSPA -> "3G HSPA"
            TelephonyManager.NETWORK_TYPE_LTE -> "4G LTE"
            TelephonyManager.NETWORK_TYPE_NR -> "5G"
            else -> "unknown"
        }
    }

    private fun getSimState(): String {
        val telephonyManager =
            requireContext().getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        when (telephonyManager.simState) {
            TelephonyManager.SIM_STATE_UNKNOWN -> return getString(R.string.sim_state_unknown)
            TelephonyManager.SIM_STATE_ABSENT -> return getString(R.string.sim_state_absent)
            TelephonyManager.SIM_STATE_CARD_IO_ERROR -> return getString(R.string.sim_state_card_io_error)

            TelephonyManager.SIM_STATE_CARD_RESTRICTED -> return getString(R.string.sim_state_card_restricted)

            TelephonyManager.SIM_STATE_NETWORK_LOCKED -> return getString(R.string.sim_state_network_locked)

            TelephonyManager.SIM_STATE_NOT_READY -> return getString(R.string.sim_state_not_ready)

            TelephonyManager.SIM_STATE_PERM_DISABLED -> return getString(R.string.sim_state_perm_disabled)

            TelephonyManager.SIM_STATE_PIN_REQUIRED -> return getString(R.string.sim_state_pin_required)

            TelephonyManager.SIM_STATE_PUK_REQUIRED -> return getString(R.string.sim_state_puk_required)

            TelephonyManager.SIM_STATE_READY -> return getString(R.string.sim_state_ready)
        }
        return getString(R.string.sim_state_unknown)

    }

    private fun getIpInfo(): MutableList<Pair<String, String>> {
        val infos = mutableListOf<Pair<String, String>>(
            Pair(getString(R.string.ip_v4), getV4Ip()),
            Pair(getString(R.string.ip_v6), getV6Ip()),
            Pair(
                getString(R.string.ip_gateway),
                getGatewayAddress(requireContext()).joinToString("\n")
            ),
            Pair(getString(R.string.ip_subnet_mask), getSubnetMask(requireContext())),
            Pair(
                getString(R.string.ip_dns),
                DeviceUtil.getDnsServers(requireContext()).joinToString("\n")
            ),
        )

        return infos
    }

    override fun getNeedPermissions(): List<String> {
        return listOf(
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.READ_PHONE_STATE
        )
    }

    private fun getWifiInfo(): MutableList<Pair<String, String>> {

        val wifiManager =
            requireContext().applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

        val infos = mutableListOf<Pair<String, String>>(
            Pair(getString(R.string.wifi_state), getWifiStateString(wifiManager.wifiState)),
            Pair(getString(R.string.wifi_ssid), wifiManager.connectionInfo.ssid),
            Pair(getString(R.string.wifi_bssid), wifiManager.connectionInfo.bssid),
            Pair(getString(R.string.wifi_signal_level), wifiManager.connectionInfo.rssi.toString()),
            Pair(
                getString(R.string.wifi_frequency),
                wifiManager.connectionInfo.frequency.toString() + "MHz"
            ),
            Pair(
                getString(R.string.wifi_link_speed),
                wifiManager.connectionInfo.linkSpeed.toString() + "Mbps"
            ),
        )


        return infos

    }

    fun getWifiStateString(state: Int): String {
        when (state) {
            WifiManager.WIFI_STATE_DISABLING -> return getString(R.string.wifi_state_disabling)
            WifiManager.WIFI_STATE_DISABLED -> return getString(R.string.wifi_state_disabled)
            WifiManager.WIFI_STATE_ENABLING -> return getString(R.string.wifi_state_enabling)
            WifiManager.WIFI_STATE_ENABLED -> return getString(R.string.wifi_state_enabled)
            WifiManager.WIFI_STATE_UNKNOWN -> return getString(R.string.wifi_state_unknown)
        }
        return getString(R.string.wifi_state_unknown)
    }


}