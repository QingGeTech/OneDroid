package cn.recommender.androiddevtoolbox.util

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.media.Image
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import cn.recommender.androiddevtoolbox.R
import cn.recommender.androiddevtoolbox.ui.dialog.Dialogs
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

object MediaUtil {

    fun saveVideo(context: Context, filePath: String, onResult: (Boolean) -> Unit) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            XXPermissions.with(context).permission(Permission.WRITE_EXTERNAL_STORAGE)
                .request(object : CommonPermissionCallback(context) {
                    override fun onAllGranted() {
                        val videoFile = File(filePath)
                        val targetFile = File(
                            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES),
                            videoFile.name
                        )
                        FileOutputStream(targetFile).use { out ->
                            FileInputStream(videoFile).use { input ->
                                input.copyTo(out)
                                onResult(true)
                            }
                        }
                    }
                })
        } else {
            val videoFile = File(filePath)
            val contentValues = ContentValues().apply {
                put(MediaStore.Video.Media.DISPLAY_NAME, videoFile.name)
                put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
                put(MediaStore.Video.Media.RELATIVE_PATH, Environment.DIRECTORY_MOVIES)
            }
            val uri = context.contentResolver.insert(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )
            context.contentResolver.openOutputStream(uri!!)!!.use { out ->
                FileInputStream(videoFile).use { input ->
                    input.copyTo(out)
                    onResult(true)
                }
            }
        }


    }

    fun imageToBitmap(image: Image): Bitmap {
        val planes = image.planes;
        val buffer = planes[0].buffer;
        val pixelStride = planes[0].pixelStride;
        val rowStride = planes[0].rowStride;
        val rowPadding = rowStride - pixelStride * image.width;

        val bitmap = Bitmap.createBitmap(
            image.width + rowPadding / pixelStride,
            image.height,
            Bitmap.Config.ARGB_8888
        );
        bitmap.copyPixelsFromBuffer(buffer)
        return bitmap
    }
}