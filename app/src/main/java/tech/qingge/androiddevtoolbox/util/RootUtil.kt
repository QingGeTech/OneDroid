package tech.qingge.androiddevtoolbox.util

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import tech.qingge.androiddevtoolbox.service.FileSystemRootService
import com.topjohnwu.superuser.Shell
import com.topjohnwu.superuser.ipc.RootService
import com.topjohnwu.superuser.nio.FileSystemManager

object RootUtil {

    init {
        Shell.setDefaultBuilder(Shell.Builder.create().setFlags(Shell.FLAG_MOUNT_MASTER))
    }

//    fun rootOperation(context: Context, func: () -> Unit) {
////        if (Shell.isAppGrantedRoot() == null || !Shell.isAppGrantedRoot()!!) {
////            Dialogs.showMessageTips(context, context.getString(R.string.lack_root_permission))
////            return
////        }
//        func()
//    }

    var remoteFs: FileSystemManager? = null

    fun getRemoteFs(context: Context, func: (FileSystemManager) -> Unit) {
        if (remoteFs != null) {
            func(remoteFs!!)
            return
        }
//        rootOperation(context) {
        val rootConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                LogUtil.d("onServiceConnected")
                remoteFs = FileSystemManager.getRemote(service!!)
                func(remoteFs!!)
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                LogUtil.d("onServiceDisconnected")
                remoteFs = null
            }
        }
        val intent = Intent(context, FileSystemRootService::class.java)
        RootService.bind(intent, rootConnection)
//        }

    }

}