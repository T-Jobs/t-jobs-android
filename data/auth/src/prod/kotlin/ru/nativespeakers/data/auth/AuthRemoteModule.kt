package ru.nativespeakers.data.auth

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class AuthRemoteModule {
    @Binds
    @Singleton
    abstract fun bindAuthDataSource(
        authRemoteDataSource: AuthRemoteDataSource
    ): AuthDataSource
}