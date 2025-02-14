package tech.qingge.androiddevtoolbox.util

import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import android.os.SystemClock
import android.util.DisplayMetrics
import android.view.WindowManager
import java.io.File
import java.net.Inet4Address
import java.net.Inet6Address
import java.net.NetworkInterface

object DeviceUtil {
    fun getScreenWidth(context: Context): Int {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            windowManager.currentWindowMetrics.bounds.width()
        } else {
            windowManager.defaultDisplay.width
        }
    }

    fun getScreenHeight(context: Context): Int {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            windowManager.currentWindowMetrics.bounds.height()
        } else {
            windowManager.defaultDisplay.height
        }
    }

    fun getScreenDensityDpi(context: Context): Int {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.densityDpi
    }

    fun isRoot(): Boolean {
        val paths = arrayOf(
            "/system/bin/su",
            "/system/xbin/su",
            "/sbin/su",
            "/data/local/xbin/su"
        )
        return paths.any  { File(it).exists() }    }

    fun getUptime(): String {
        val elapsedMillis = SystemClock.elapsedRealtime()
        val seconds = (elapsedMillis / 1000) % 60
        val minutes = (elapsedMillis / (1000 * 60)) % 60
        val hours = (elapsedMillis / (1000 * 60 * 60)) % 24
        val days = (elapsedMillis / (1000 * 60 * 60 * 24))
        return "${days}days ${hours}hours ${minutes}minutes ${seconds}seconds"
    }

    fun getV4Ip(): String {
        val v4IpList = mutableListOf<String>()
        NetworkInterface.getNetworkInterfaces()?.toList()?.forEach { networkInterface ->
            networkInterface.inetAddresses?.toList()?.forEach { inetAddress ->
                if (inetAddress is Inet4Address && inetAddress.hostAddress != null) {
                    v4IpList.add(inetAddress.hostAddress!!)
                }
            }
        }
        return v4IpList.joinToString("\n")
    }

    fun getV6Ip(): String {
        val v6IpList = mutableListOf<String>()
        NetworkInterface.getNetworkInterfaces()?.toList()?.forEach { networkInterface ->
            networkInterface.inetAddresses?.toList()?.forEach { inetAddress ->
                if (inetAddress is Inet6Address && inetAddress.hostAddress != null) {
                    v6IpList.add(inetAddress.hostAddress!!)
                }
            }
        }
        return v6IpList.joinToString("\n")
    }

    fun getGatewayAddress(context: Context): List<String> {
        val addressList = mutableListOf<String>()
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val networks = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            listOf(cm.activeNetwork)
        } else {
            cm.allNetworks.toList()
        }

        for (network in networks) {
            val linkProperties = cm.getLinkProperties(network) ?: continue
            for (route in linkProperties.routes) {
                if (route.isDefaultRoute && route.gateway!=null && route.gateway!!.hostAddress!=null) {
                    addressList.add(route.gateway!!.hostAddress!!)
                }
            }
        }
        return addressList
    }

    fun getSubnetMask(context: Context): String {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val networks = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            listOf(cm.activeNetwork)
        } else {
            cm.allNetworks.toList()
        }

        for (network in networks) {
            val linkProperties = cm.getLinkProperties(network) ?: continue
            for (address in linkProperties.linkAddresses) {
                return prefixLengthToSubnetMask(address.prefixLength)
            }
        }
        return ""
    }

    fun getDnsServers(context: Context): List<String> {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val networks = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            listOf(cm.activeNetwork)
        } else {
            cm.allNetworks.toList()
        }

        val dnsList = mutableListOf<String>()

        for (network in networks) {
            val linkProperties = cm.getLinkProperties(network) ?: continue
            linkProperties.dnsServers.forEach { dnsList.add(it.hostAddress ?: "") }
        }

        return dnsList
    }


    fun prefixLengthToSubnetMask(prefixLength: Int): String {
        val mask = (0xFFFFFFFF shl (32 - prefixLength)).toInt()
        return "${mask ushr 24 and 0xFF}.${mask ushr 16 and 0xFF}.${mask ushr 8 and 0xFF}.${mask and 0xFF}"
    }

}