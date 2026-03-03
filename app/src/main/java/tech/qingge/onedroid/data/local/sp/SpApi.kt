package tech.qingge.onedroid.data.local.sp

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

    /**
     * 过滤应用列表
     * type: 0 全部
     * 1: 系统
     * 2: 用户
     */
    fun setAppFilterType(type: Int)
    fun getAppFilterType(): Int

    fun setAppSortType(type: String)
    fun getAppSortType(): String

    fun setAppSortDesc(isDesc: Boolean)
    fun isAppSortDesc(): Boolean

    fun isAgreeProtocol(): Boolean
    fun setAgreeProtocol()

}