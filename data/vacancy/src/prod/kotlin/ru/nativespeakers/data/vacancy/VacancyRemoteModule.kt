package ru.nativespeakers.data.vacancy

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class VacancyRemoteModule {
    @Binds
    @Singleton
    abstract fun bindVacancyRemoteDataSource(
        vacancyRemoteDataSource: VacancyRemoteDataSource,
    ): VacancyDataSource
}