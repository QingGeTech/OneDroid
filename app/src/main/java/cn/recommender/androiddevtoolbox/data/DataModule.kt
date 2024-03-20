package cn.recommender.androiddevtoolbox.data

import android.content.Context
import cn.recommender.androiddevtoolbox.data.local.sp.SpApi
import cn.recommender.androiddevtoolbox.data.local.sp.SpApiImpl
import cn.recommender.androiddevtoolbox.data.local.sys.SysApi
import cn.recommender.androiddevtoolbox.data.local.sys.SysApiImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    abstract fun bindSpApi(impl: SpApiImpl): SpApi

    @Binds
    abstract fun bindSysApi(impl: SysApiImpl): SysApi

}