package tech.qingge.androiddevtoolbox.util

import android.graphics.Bitmap
import java.io.File
import java.io.FileOutputStream

object BitmapUtil {
    fun saveBitmapAsPng(bitmap: Bitmap, quality: Int, filePath: String){
        val file = File(filePath)
        file.createNewFile()
        FileOutputStream(file).use {
            bitmap.compress(Bitmap.CompressFormat.PNG, quality, it)
            it.flush()
        }
    }
}