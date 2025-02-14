package tech.qingge.androiddevtoolbox.util

import java.util.UUID

object RandomUtil {
    fun uuid(): String {
        return UUID.randomUUID().toString()
    }
}