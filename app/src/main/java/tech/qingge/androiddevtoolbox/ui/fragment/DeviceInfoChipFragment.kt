package tech.qingge.androiddevtoolbox.ui.fragment

import dagger.hilt.android.AndroidEntryPoint
import tech.qingge.androiddevtoolbox.data.entity.CardData
import javax.inject.Inject


@AndroidEntryPoint
class DeviceInfoChipFragment @Inject constructor() :DeviceInfoBaseFragment() {


    override suspend fun initCardDataList(): List<CardData> {
        return emptyList()
    }

}