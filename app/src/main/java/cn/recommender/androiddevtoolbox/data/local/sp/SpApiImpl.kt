package cn.recommender.androiddevtoolbox.data.local.sp

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SpApiImpl @Inject constructor(private val appContext: Application) : SpApi {

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