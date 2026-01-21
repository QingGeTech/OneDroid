package tech.qingge.onedroid.tool

import tech.qingge.onedroid.ui.view.DraggableFrameLayout

abstract class BaseTool {
    abstract fun init(ffl: DraggableFrameLayout)
    abstract fun deInit()


}