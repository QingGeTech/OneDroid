package cn.recommender.androiddevtoolbox.util

fun <K, V> Map<K, V>.reverse(): Map<V, K> {
    return this.map { it.value to it.key }.toMap()
}