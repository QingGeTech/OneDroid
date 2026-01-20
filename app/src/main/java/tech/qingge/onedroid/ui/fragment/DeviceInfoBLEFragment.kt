package tech.qingge.onedroid.ui.fragment

import android.annotation.SuppressLint
import dagger.hilt.android.AndroidEntryPoint
import tech.qingge.onedroid.data.entity.CardData
import javax.inject.Inject


@AndroidEntryPoint
class DeviceInfoBLEFragment @Inject constructor() : DeviceInfoBaseFragment() {


    @SuppressLint("DefaultLocale")
    override suspend fun initCardDataList(): List<CardData> {
        return emptyList()
    }


}

