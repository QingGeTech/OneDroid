package tech.qingge.onedroid.tool

import tech.qingge.onedroid.ui.view.FloatingFrameLayout

abstract class BaseTool {
    abstract fun init(ffl: FloatingFrameLayout)
    abstract fun deInit()


}