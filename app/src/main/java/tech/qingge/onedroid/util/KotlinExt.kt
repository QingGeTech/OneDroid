package tech.qingge.onedroid.util

import android.util.Base64
import java.security.MessageDigest

fun <K, V> Map<K, V>.reverse(): Map<V, K> {
    return this.map { it.value to it.key }.toMap()
}

fun ByteArray.base64(): String {
    return Base64.encodeToString(this, Base64.NO_WRAP)
}

fun ByteArray.hex(): String {
    return this.joinToString(separator = "") { byte -> "%02x".format(byte) }
}

fun ByteArray.md5(): String {
    return MessageDigest.getInstance("MD5").digest(this).hex()
}

fun ByteArray.sha1(): String {
    return MessageDigest.getInstance("SHA-1").digest(this).hex()
}

fun ByteArray.sha256(): String {
    return MessageDigest.getInstance("SHA-256").digest(this).hex()
}
