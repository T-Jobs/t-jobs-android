package ru.nativespeakers.data.tag

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class TagRemoteModule {
    @Binds
    @Singleton
    abstract fun bindTagRemoteDataSource(
        tagRemoteDataSource: TagRemoteDataSource,
    ): TagDataSource
}