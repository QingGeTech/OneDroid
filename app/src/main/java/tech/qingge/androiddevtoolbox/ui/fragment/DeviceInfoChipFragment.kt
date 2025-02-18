package tech.qingge.androiddevtoolbox.ui.fragment

import dagger.hilt.android.AndroidEntryPoint
import tech.qingge.androiddevtoolbox.R
import tech.qingge.androiddevtoolbox.data.entity.CardData
import javax.inject.Inject
import javax.microedition.khronos.egl.EGL10
import javax.microedition.khronos.egl.EGLContext


@AndroidEntryPoint
class DeviceInfoChipFragment @Inject constructor() : DeviceInfoBaseFragment() {


    override suspend fun initCardDataList(): List<CardData> {
        return mutableListOf(
            CardData(getString(R.string.cpu_info), getCpuInfo()),
            CardData(getString(R.string.gpu_info), getGpuInfo())
        )
    }

    private fun getGpuInfo(): MutableList<Pair<String, String>> {
        val egl = EGLContext.getEGL() as EGL10
        val display = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY)
        egl.eglInitialize(display, null)

        // 获取 GPU 信息
        val vendor = egl.eglQueryString(display, EGL10.EGL_VENDOR) ?: "Unknown"
        val version = egl.eglQueryString(display, EGL10.EGL_VERSION) ?: "Unknown"
//        val renderer = egl.eglQueryString(display, EGL10.EGL_RENDERER) ?: "Unknown"
//        val extensions = egl.eglQueryString(display, EGL10.EGL_EXTENSIONS) ?: "Unknown"
//        val renderer = GLES20.glGetString(GLES20.GL_RENDERER) ?: "Unknown"


        // 获取 EGL 配置信息
//        val configInfo = getEGLConfigs(egl, display)

        egl.eglTerminate(display) // 释放资源


        return mutableListOf(
            Pair(getString(R.string.gpu_vendor), vendor),
            Pair(getString(R.string.gpu_version), version),
//            Pair(getString(R.string.gpu_renderer), renderer),
//            Pair(getString(R.string.gpu_extensions), extensions),
//            Pair(getString(R.string.gpu_egl_configurations), configInfo)
        )
    }

//    private fun getEGLConfigs(egl: EGL10, display: EGLDisplay): String {
//        val configs = arrayOfNulls<EGLConfig>(100)
//        val numConfigs = IntArray(1)
//
//        // 获取 EGL 可用的配置
//        egl.eglGetConfigs(display, configs, 100, numConfigs)
//
//        val sb = StringBuilder()
//        for (i in 0 until numConfigs[0]) {
//            val config = configs[i] ?: continue
//            sb.append("\nConfig $i:\n")
//            sb.append(getConfigAttributes(egl, display, config))
//        }
//
//        return sb.toString()
//    }
//
//    private fun getConfigAttributes(egl: EGL10, display: EGLDisplay, config: EGLConfig): String {
//        val attributes = mapOf(
//            EGL10.EGL_BUFFER_SIZE to "Buffer Size",
//            EGL10.EGL_ALPHA_SIZE to "Alpha Size",
//            EGL10.EGL_BLUE_SIZE to "Blue Size",
//            EGL10.EGL_GREEN_SIZE to "Green Size",
//            EGL10.EGL_RED_SIZE to "Red Size",
//            EGL10.EGL_DEPTH_SIZE to "Depth Size",
//            EGL10.EGL_STENCIL_SIZE to "Stencil Size",
//            EGL10.EGL_RENDERABLE_TYPE to "Renderable Type",
//            EGL10.EGL_SURFACE_TYPE to "Surface Type"
//        )
//
//        val sb = StringBuilder()
//        val value = IntArray(1)
//
//        for ((key, name) in attributes) {
//            egl.eglGetConfigAttrib(display, config, key, value)
//            sb.append("$name: ${value[0]}\n")
//        }
//
//        return sb.toString()
//    }



    private fun getCpuInfo(): MutableList<Pair<String, String>> {
        val p = ProcessBuilder("cat", "/proc/cpuinfo").redirectErrorStream(true).start()
        val map = hashMapOf<String,String>()
        p.inputStream.bufferedReader().readLines()
            .filter { it.isNotEmpty() && it.contains(":") }.forEach {
                val parts = it.split(":")
                map.put(parts[0].trim(), parts[1].trim())
            }

        return map.entries.map { Pair(it.key, it.value) }.toMutableList()
    }

}