package cn.recommender.androiddevtoolbox.data.entity

import android.graphics.drawable.Drawable

data class AppData(
    val icon: Drawable,
    val appName: String,
    val pkgName: String,
    val versionCode: Int,
    val versionName: String
)
