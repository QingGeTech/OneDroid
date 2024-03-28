package cn.recommender.androiddevtoolbox.data.local.sp

/**
 * read write shared preference
 */
interface SpApi {
    fun setThemeColor(themeColor: Int)
    fun getThemeColor(): Int
    fun setDarkTheme(isDarkTheme: Boolean)
    fun isDarkTheme(): Boolean
    fun setLastBottomItemId(itemId: Int)
    fun getLastBottomItemId(): Int
}