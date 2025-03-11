package tech.qingge.androiddevtoolbox

import android.content.Context
import android.media.projection.MediaProjectionManager
import android.view.WindowManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import tech.qingge.androiddevtoolbox.data.net.ApiService
import java.util.concurrent.TimeUnit
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


    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        // 获取服务器公钥sha256base64
        // openssl x509 -in cert.pem -pubkey -noout | openssl pkey -pubin -outform der | openssl dgst -sha256 -binary | openssl enc -base64
        val certificatePinner = CertificatePinner.Builder()
            .add("api.qingge.tech", "sha256/TrOw79a2NOG6kFuw4+7XX0Xity0o6IN12tGE83+Adxw=")
            .build()

        return OkHttpClient.Builder().addInterceptor(loggingInterceptor)
            .connectTimeout(10, TimeUnit.SECONDS).readTimeout(10, TimeUnit.SECONDS)
            .certificatePinner(certificatePinner)
            .writeTimeout(10, TimeUnit.SECONDS).callTimeout(10, TimeUnit.SECONDS).build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.qingge.tech/adtb/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }


}