package ru.nativespeakers.data.auth

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class AuthFakeDataModule {
    @Binds
    abstract fun bindAuthDataSource(
        authFakeDataSource: AuthFakeDataSource
    ): AuthDataSource
}