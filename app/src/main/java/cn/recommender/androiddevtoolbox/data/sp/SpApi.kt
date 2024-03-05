package cn.recommender.androiddevtoolbox.data.sp

/**
 * read write shared preference
 */
interface SpApi {
    fun setTheme(theme: Int)
    fun getTheme(): Int
}