package tech.qingge.onedroid.data.local.sp

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.ContextCompat
import tech.qingge.onedroid.Constants
import tech.qingge.onedroid.R
import javax.inject.Inject

class SpApiImpl @Inject constructor(private val appContext: Application) : SpApi {

    companion object {
        private const val SP_NAME = "AndroidDevToolbox"
        private const val THEME_COLOR_KEY = "themeColor"
        private const val DARK_THEME_KEY = "isDarkTheme"
        private const val LAST_BOTTOM_ITEM_ID = "lastBottomItemId"
        private const val APP_FILTER_TYPE = "appFilterType"
        private const val APP_SORT_TYPE = "appSortType"
        private const val App_SORT_DESC = "isAppSortDesc"
    }

    override fun setThemeColor(themeColor: Int) {
        getSp().edit().putInt(THEME_COLOR_KEY, themeColor).apply()
    }

    override fun getThemeColor(): Int {
        return getSp().getInt(
            THEME_COLOR_KEY,
            ContextCompat.getColor(appContext, R.color.color_primary_1)
        )
    }

    private fun getSp(): SharedPreferences {
        return appContext.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
    }


    override fun setDarkTheme(isDarkTheme: Boolean) {
        getSp().edit().putBoolean(DARK_THEME_KEY, isDarkTheme).apply()
    }

    override fun isDarkTheme(): Boolean {
        return getSp().getBoolean(DARK_THEME_KEY, false)
    }

    override fun setLastBottomItemId(itemId: Int) {
        getSp().edit().putInt(LAST_BOTTOM_ITEM_ID, itemId).apply()
    }


    override fun getLastBottomItemId(): Int {
        return getSp().getInt(LAST_BOTTOM_ITEM_ID, R.id.app_manager)
    }

    @SuppressLint("ApplySharedPref")
    override fun setAppFilterType(type: Int) {
        getSp().edit().putInt(APP_FILTER_TYPE, type).commit()
    }

    override fun getAppFilterType(): Int {
        return getSp().getInt(APP_FILTER_TYPE, Constants.APP_FILTER_TYPE_USER)
    }

    override fun setAppSortType(type: String) {
        getSp().edit().putString(APP_SORT_TYPE, type).apply()
    }

    override fun getAppSortType(): String {
        return getSp().getString(APP_SORT_TYPE, Constants.APP_SORT_TYPE_APP_NAME)!!
    }

    override fun setAppSortDesc(isDesc: Boolean) {
        getSp().edit().putBoolean(App_SORT_DESC, isDesc).apply()
    }

    override fun isAppSortDesc(): Boolean {
        return getSp().getBoolean(App_SORT_DESC, false)
    }

}