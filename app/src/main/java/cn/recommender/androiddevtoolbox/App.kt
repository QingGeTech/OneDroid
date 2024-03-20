package cn.recommender.androiddevtoolbox

import android.app.Activity
import android.app.Application
import cn.recommender.androiddevtoolbox.data.local.sp.SpApi
import cn.recommender.androiddevtoolbox.data.local.sp.SpApiImpl
import cn.recommender.androiddevtoolbox.data.local.sys.SysApi
import cn.recommender.androiddevtoolbox.data.local.sys.SysApiImpl
import com.google.android.material.color.DynamicColors
import com.google.android.material.color.DynamicColorsOptions
import com.google.android.material.color.HarmonizedColors
import com.google.android.material.color.HarmonizedColorsOptions
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {

    override fun onCreate() {
        super.onCreate()
//        DynamicColors.applyToActivitiesIfAvailable(this,
//            DynamicColorsOptions.Builder()
//                .setPrecondition { _, _ -> true }
//                .setOnAppliedCallback { activity: Activity ->
//                    HarmonizedColors.applyToContextIfAvailable(
//                        activity, HarmonizedColorsOptions.createMaterialDefaults()
//                    )
//                }
//                .build())
    }

}