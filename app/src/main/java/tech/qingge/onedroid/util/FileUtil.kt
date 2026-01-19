package tech.qingge.onedroid.util

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import java.io.File

object FileUtil {
    fun getFileName(filePath: String): String {
        val index = filePath.lastIndexOf("/")
        return filePath.substring(index + 1)
    }

    fun getFileExt(filePath: String): String {
        return filePath.substring(getFileName(filePath).lastIndexOf(".") + 1)
    }

    fun isTextFile(filePath: String): Boolean {
        val extList = setOf(
            "txt",
            "java",
            "kt",
            "kts",
            "xml",
            "js",
            "json",
            "md",
            "gradle",
            "properties",
            "yml",
            "yaml",
            "sh",
            "c",
            "cpp",
            "h",
            "hpp",
            "go",
            "ts",
            "css",
            "html",
            "kt",
            "kts",
            "py",
            "sh",
            "c",
        )
        return extList.contains(getFileExt(filePath))
    }

    fun isImageFile(filePath: String): Boolean {
        val ext = getFileExt(filePath)
        return arrayOf("jpg", "png").contains(ext)
    }

    fun copyUri(context: Context, uri: Uri, target: String) {
        val targetFile = File(target)
        if (targetFile.exists()) {
            targetFile.delete()
        }
        targetFile.createNewFile()
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            targetFile.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
    }

    fun getFileNameFromUri(context: Context, uri: Uri): String? {
        var fileName: String? = null
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex >= 0) {
                    fileName = cursor.getString(nameIndex)
                }
            }
        }
        return fileName
    }

//
//    suspend fun getTmpFileUri(activity: ComponentActivity, fileName: String): Uri {
//
//        return suspendCoroutine<Uri> { continuation ->
//            val activityResultLauncher =
//                activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
//                    if (it.resultCode == RESULT_OK && it.data != null && it.data!!.data != null) {
//                        val uri = it.data!!.data
//                        LogUtil.d("uri:${uri}")
//                        continuation.resume(uri!!)
//                    } else {
//                        continuation.resumeWithException(Exception("getTmpFileUri failed"))
//                    }
//                }
//
//            val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
//            intent.addCategory(Intent.CATEGORY_OPENABLE)
//            intent.putExtra(Intent.EXTRA_TITLE, fileName)
//
//            activityResultLauncher.launch(intent)
//        }
//
//    }

//    fun saveFileToSystem(activity: AppCompatActivity, filePath: String) {
//        val fileName = getFileName(filePath)
//
//        val activityResultLauncher =
//            activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
//                if (it.resultCode == RESULT_OK && it.data != null && it.data!!.data != null) {
//                    val uri = it.data!!.data
//                    LogUtil.d("uri:${uri}")
////                    var fileName = ""
////                    contentResolver.query(uri!!, null, null, null, null).use { cursor ->
////                        fileName =
////                            cursor!!.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
////                        LogUtil.d("fileName:$fileName")
////                    }
//                    LoadingDialog.show(activity)
//                    activity.lifecycleScope.launch(Dispatchers.IO) {
//                        runCatching {
//                            File(filePath).inputStream()
//                                .copyTo(activity.contentResolver.openOutputStream(uri!!)!!)
//                        }.onSuccess {
//                            withContext(Dispatchers.Main) {
//                                LoadingDialog.dismiss()
//                                Dialogs.showMessageTips(
//                                    activity,
//                                    activity.getString(R.string.save_success)
//                                )
//                            }
//                        }.onFailure {
//                            withContext(Dispatchers.Main) {
//                                LoadingDialog.dismiss()
//                                Dialogs.showMessageTips(
//                                    activity,
//                                    activity.getString(R.string.save_fail)
//                                )
//                            }
//                        }
//                    }
//
//                }
//            }
//
//
//        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
//        intent.addCategory(Intent.CATEGORY_OPENABLE)
////        intent.setType(MimeTypeMap.getSingleton().getMimeTypeFromExtension(getFileExt(filePath)))
//        intent.putExtra(Intent.EXTRA_TITLE, fileName)
//
//        activityResultLauncher.launch(intent)
//    }
}