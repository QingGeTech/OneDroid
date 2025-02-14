package tech.qingge.androiddevtoolbox.util

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.content.pm.ServiceInfo
import android.view.accessibility.AccessibilityManager


object AccessibilityUtil {
    fun isAccessibilityServiceEnabled(
        context: Context,
        service: Class<out AccessibilityService?>
    ): Boolean {
        val am = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        val enabledServices =
            am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
        for (enabledService in enabledServices) {
            val serviceInfo: ServiceInfo? = enabledService.resolveInfo.serviceInfo
            if (serviceInfo != null && service.name == serviceInfo.name) {
                return true
            }
        }
        return false
    }
}