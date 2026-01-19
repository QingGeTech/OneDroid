package tech.qingge.onedroid.base

import android.app.Service
import android.content.Intent
import android.os.IBinder
import tech.qingge.onedroid.util.LogUtil

abstract class BaseService : Service() {

    override fun onBind(intent: Intent?): IBinder? {
        LogUtil.d("${javaClass.name} onBind")
        return null
    }

    override fun onUnbind(intent: Intent?): Boolean {
        LogUtil.d("${javaClass.name} onUnbind")
        return super.onUnbind(intent)
    }

    override fun onCreate() {
        super.onCreate()
        LogUtil.d("${javaClass.name} onCreate")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        LogUtil.d("${javaClass.name} onStartCommand")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        LogUtil.d("${javaClass.name} onDestroy")
    }

}