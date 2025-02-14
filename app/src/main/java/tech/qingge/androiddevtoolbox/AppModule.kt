package tech.qingge.androiddevtoolbox

import android.content.Context
import android.media.projection.MediaProjectionManager
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

    @Provides
    @Singleton
    fun provideMediaProjectionManager(@ApplicationContext appContext: Context): MediaProjectionManager {
        return appContext.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
    }

    @Provides
    @Singleton
    fun provideApp(@ApplicationContext appContext: Context): App {
        return appContext as App
    }


}