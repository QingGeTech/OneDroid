package tech.qingge.androiddevtoolbox.data

import tech.qingge.androiddevtoolbox.data.local.sp.SpApi
import tech.qingge.androiddevtoolbox.data.local.sp.SpApiImpl
import tech.qingge.androiddevtoolbox.data.local.sys.SysApi
import tech.qingge.androiddevtoolbox.data.local.sys.SysApiImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    abstract fun bindSpApi(impl: SpApiImpl): SpApi

    @Binds
    abstract fun bindSysApi(impl: SysApiImpl): SysApi

}