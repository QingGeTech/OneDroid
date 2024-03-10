package cn.recommender.androiddevtoolbox.data.local.sys

import cn.recommender.androiddevtoolbox.data.entity.AppData

interface SysApi {
    fun getAppList(): List<AppData>
}