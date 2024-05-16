package cn.recommender.androiddevtoolbox

import android.app.Application
import android.content.Context
import android.view.WindowManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideWindowManager(@ApplicationContext appContext: Context): WindowManager {
        return appContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }


}