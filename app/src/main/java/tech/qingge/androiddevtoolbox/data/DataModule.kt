package tech.qingge.androiddevtoolbox.data

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import tech.qingge.androiddevtoolbox.data.local.sp.SpApi
import tech.qingge.androiddevtoolbox.data.local.sp.SpApiImpl
import tech.qingge.androiddevtoolbox.data.local.sys.SysApi
import tech.qingge.androiddevtoolbox.data.local.sys.SysApiImpl


@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    abstract fun bindSpApi(impl: SpApiImpl): SpApi

    @Binds
    abstract fun bindSysApi(impl: SysApiImpl): SysApi
}