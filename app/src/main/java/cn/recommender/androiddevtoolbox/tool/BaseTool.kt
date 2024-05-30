package cn.recommender.androiddevtoolbox.tool

import cn.recommender.androiddevtoolbox.ui.view.FloatingFrameLayout

abstract class BaseTool {
    abstract fun init(ffl: FloatingFrameLayout)
    abstract fun deInit()


}