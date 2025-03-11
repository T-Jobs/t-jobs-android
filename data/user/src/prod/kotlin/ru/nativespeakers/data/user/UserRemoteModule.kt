package ru.nativespeakers.data.user

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class UserRemoteModule {
    @Binds
    abstract fun bindUserRemoteDataSource(
        userRemoteDataSource: UserRemoteDataSource
    ): UserDataSource
}