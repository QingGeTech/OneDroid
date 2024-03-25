package cn.recommender.androiddevtoolbox.data.local.sp

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.res.ResourcesCompat
import androidx.core.content.res.ResourcesCompat.ThemeCompat
import cn.recommender.androiddevtoolbox.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SpApiImpl @Inject constructor(private val appContext: Application) : SpApi {

    companion object {
        private const val SP_NAME = "AndroidDevToolbox"
        private const val THEME_KEY = "theme"
        private const val DARK_THEME_KEY = "isDarkTheme"
        private const val LAST_BOTTOM_ITEM_ID = "lastBottomItemId"
    }

    override fun setTheme(theme: Int) {
        getSp().edit().putInt(THEME_KEY, theme).apply()
    }

    override fun getTheme(): Int {
        return getSp().getInt(
            THEME_KEY,
            R.style.AppTheme
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

}