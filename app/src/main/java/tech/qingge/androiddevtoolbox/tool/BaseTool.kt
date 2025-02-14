package tech.qingge.androiddevtoolbox.tool

import tech.qingge.androiddevtoolbox.ui.view.FloatingFrameLayout

abstract class BaseTool {
    abstract fun init(ffl: FloatingFrameLayout)
    abstract fun deInit()


}