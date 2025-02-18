package tech.qingge.androiddevtoolbox.ui.fragment

import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import dagger.hilt.android.AndroidEntryPoint
import tech.qingge.androiddevtoolbox.data.entity.CardData
import javax.inject.Inject


@AndroidEntryPoint
class DeviceInfoCameraFragment @Inject constructor() : DeviceInfoBaseFragment() {

    override fun getNeedPermissions(): List<String> {
        return listOf(
            android.Manifest.permission.CAMERA
        )
    }

    override suspend fun initCardDataList(): List<CardData> {
        val cameraManager =
            requireContext().getSystemService(Context.CAMERA_SERVICE) as CameraManager
        return cameraManager.cameraIdList.map { cameraId ->
            cameraManager.getCameraCharacteristics(
                cameraId
            )
        }.mapIndexed { index, cameraCharacteristics ->
            CardData("Camera #$index", getCameraInfo(cameraCharacteristics))
        }

    }

    private fun getCameraInfo(cc: CameraCharacteristics): MutableList<Pair<String, String>> {
        return cc.keys.map { k -> Pair(k.name, cc.get(k).toString()) }.toMutableList()
    }

}