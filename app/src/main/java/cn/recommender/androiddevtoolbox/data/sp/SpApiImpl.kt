package cn.recommender.androiddevtoolbox.data.sp

import android.content.Context
import android.content.SharedPreferences

class SpApiImpl(private val appContext: Context) : SpApi {

    companion object {
        private const val SP_NAME = "AndroidDevToolbox"
        private const val THEME_KEY = "theme"
    }

    override fun setTheme(theme: Int) {
        getSp().edit().putInt(THEME_KEY, theme).commit()
    }

    override fun getTheme(): Int {
        return getSp().getInt(THEME_KEY, -1)
    }

    private fun getSp(): SharedPreferences {
        return appContext.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
    }

}