package tech.qingge.onedroid.ui.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import tech.qingge.onedroid.tool.ViewNode

class InspectView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var bitmap: Bitmap? = null
    private var rootNode: ViewNode? = null
    private var selectedNode: ViewNode? = null

    // 用于图片适配的矩阵和测量数据
    private val displayMatrix = Matrix()
    private val invertMatrix = Matrix() // 用于将触摸坐标转回原始坐标
    private var scaleFactor = 1f

    private val paint = Paint().apply {
        color = Color.RED
        style = Paint.Style.STROKE
        strokeWidth = 2f
    }

    private val selectedPaint = Paint().apply {
        color = Color.YELLOW
        style = Paint.Style.STROKE
        strokeWidth = 5f
    }

    fun setData(bitmap: Bitmap, rootNode: ViewNode) {
        this.bitmap = bitmap
        this.rootNode = rootNode
        calculateMatrices()
        invalidate()
    }

    private fun calculateMatrices() {
        val b = bitmap ?: return
        if (width == 0 || height == 0) return

        displayMatrix.reset()

        // 计算缩放比例（CenterInside 逻辑）
        val scaleW = width.toFloat() / b.width
        val scaleH = height.toFloat() / b.height
        scaleFactor = minOf(scaleW, scaleH)

        val dx = (width - b.width * scaleFactor) / 2f
        val dy = (height - b.height * scaleFactor) / 2f

        displayMatrix.postScale(scaleFactor, scaleFactor)
        displayMatrix.postTranslate(dx, dy)

        // 计算逆矩阵，用于处理点击事件
        displayMatrix.invert(invertMatrix)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        calculateMatrices()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val b = bitmap ?: return

        // 1. 使用矩阵绘制不拉伸的图片
        canvas.drawBitmap(b, displayMatrix, null)

        // 2. 将 Canvas 坐标系切换到与图片像素一致，这样可以直接用 ViewNode 的 bounds 绘图
        canvas.save()
        canvas.concat(displayMatrix)

        rootNode?.let { drawNodes(canvas, it) }
        selectedNode?.let {
            canvas.drawRect(it.bounds, selectedPaint)
        }

        canvas.restore()
    }

    private fun drawNodes(canvas: Canvas, node: ViewNode) {
        canvas.drawRect(node.bounds, paint)
        for (child in node.children) {
            drawNodes(canvas, child)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            // 将屏幕触摸坐标映射为图片原始像素坐标
            val pts = floatArrayOf(event.x, event.y)
            invertMatrix.mapPoints(pts)

            val target = findDeepestNode(rootNode, pts[0].toInt(), pts[1].toInt())
            if (target != null) {
                selectedNode = target
                onNodeSelectedListener?.invoke(target)
                invalidate()
            }
        }
        return true
    }

    private fun findDeepestNode(node: ViewNode?, x: Int, y: Int): ViewNode? {
        if (node == null || !node.bounds.contains(x, y)) return null
        for (i in node.children.indices.reversed()) {
            val found = findDeepestNode(node.children[i], x, y)
            if (found != null) return found
        }
        return node
    }

    var onNodeSelectedListener: ((ViewNode) -> Unit)? = null
}