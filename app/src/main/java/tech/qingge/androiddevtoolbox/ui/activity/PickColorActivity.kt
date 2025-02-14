package tech.qingge.androiddevtoolbox.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.MotionEvent
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import tech.qingge.androiddevtoolbox.R
import tech.qingge.androiddevtoolbox.base.BaseActivity
import tech.qingge.androiddevtoolbox.databinding.ActivityPickColorBinding
import tech.qingge.androiddevtoolbox.util.ColorUtil
import tech.qingge.androiddevtoolbox.util.ViewUtil

@AndroidEntryPoint
class PickColorActivity : BaseActivity<ActivityPickColorBinding>() {

//    private lateinit var binding: ActivityPickColorBinding

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initViews(intent)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initViews(intent: Intent) {
        val filePath = intent.getStringExtra("filePath")
        val bitmap = BitmapFactory.decodeFile(filePath!!)

        binding.toolbar.setNavigationOnClickListener { finish() }
        Glide.with(this).load(bitmap).into(binding.img)
        binding.img.setOnTouchListener { _, e ->
            when (e.action) {
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                    updateColor(bitmap, e.x.toInt(), e.y.toInt())
                    updateMagnify(e.x.toInt(), e.y.toInt())
                }
            }
            true
        }
        binding.img.postDelayed({
            binding.magnify.setTargetView(binding.img)
            updateColor(bitmap, binding.img.width / 2, binding.img.height / 2)
        }, 500)

    }


    private fun updateMagnify(x: Int, y: Int) {
        binding.magnify.updatePoint(x, y)
    }

    private fun updateColor(bitmap: Bitmap, x: Int, y: Int) {
        val realW = binding.img.drawable.intrinsicWidth
        val realH = binding.img.drawable.intrinsicHeight
        val ivW = binding.img.width
        val ivH = binding.img.height
        val distX = (ivW - realW) / 2
        val distY = (ivH - realH) / 2

        var color: Int
        if (x <= distX || x >= distX + realW || y <= distY || y >= distY + realH) {
            color = ViewUtil.getColorByStyledAttr(this, R.attr.colorSecondaryContainer)
        } else {
            val bitmapX = (bitmap.width * (x - distX) / realW).coerceIn(0, bitmap.width - 1)
            val bitmapY = (bitmap.height * (y - distY) / realH).coerceIn(0, bitmap.height - 1)
            color = bitmap.getPixel(bitmapX, bitmapY)

            //在右边界color可能返回0
            if (color == 0) {
                color = ViewUtil.getColorByStyledAttr(this, R.attr.colorSecondaryContainer)
            }
        }

        binding.imgColor.setColor(color)
        binding.tvColor.text = ColorUtil.colorHex(color)
        binding.tvColor.setTextColor(ColorUtil.getContrastingColor(color))
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        initViews(intent)
    }

}