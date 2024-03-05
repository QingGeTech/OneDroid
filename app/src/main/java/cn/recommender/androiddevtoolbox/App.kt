package cn.recommender.androiddevtoolbox

import android.app.Activity
import android.app.Application
import cn.recommender.androiddevtoolbox.data.sp.SpApi
import cn.recommender.androiddevtoolbox.data.sp.SpApiImpl
import com.google.android.material.color.DynamicColors
import com.google.android.material.color.DynamicColorsOptions
import com.google.android.material.color.HarmonizedColors
import com.google.android.material.color.HarmonizedColorsOptions

class App : Application() {

    companion object {
        lateinit var sp: SpApi
    }

    override fun onCreate() {
        super.onCreate()
        sp = SpApiImpl(this)
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