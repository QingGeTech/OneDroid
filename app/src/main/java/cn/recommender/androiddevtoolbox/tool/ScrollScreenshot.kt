package cn.recommender.androiddevtoolbox.tool

import android.app.Application
import cn.recommender.androiddevtoolbox.ui.view.FloatingFrameLayout
import dagger.hilt.android.scopes.ServiceScoped
import javax.inject.Inject

@ServiceScoped
class ScrollScreenshot @Inject constructor(val appContext: Application) :
    BaseMediaProjectionTool() {

    private var imageFilePathList: MutableList<String>? = null

//    fun start(mediaProjectionService: MediaProjectionService) {
//        imageFilePathList = mutableListOf()
//        mediaProjectionService.screenshot { _, filePath ->
//            imageFilePathList!!.add(filePath)
//        }
//    }
//
//    fun onTouchOutside(mediaProjectionService: MediaProjectionService) {
//        if (imageFilePathList == null) {
//            return
//        }
//        mediaProjectionService.screenshot { _, filePath ->
//            imageFilePathList!!.add(filePath)
//        }
//    }
//
//    fun stop(mediaProjectionService: MediaProjectionService) {
//        mediaProjectionService.screenshot { _, filePath ->
//            imageFilePathList!!.add(filePath)
//            //合成图片，清空状态， 启动Activity
//            LogUtil.d("imageFilePathList:$imageFilePathList")
//            imageFilePathList = null
//        }
//    }
//
//    fun isStarted(): Boolean {
//        return imageFilePathList != null
//    }


    override fun onServiceConnected() {
    }

    override fun init(ffl: FloatingFrameLayout) {
    }

    override fun deInit() {
    }


}