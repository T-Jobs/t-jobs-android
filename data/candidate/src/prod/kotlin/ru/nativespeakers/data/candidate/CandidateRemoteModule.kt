package ru.nativespeakers.data.candidate

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class CandidateRemoteModule {
    @Binds
    @Singleton
    abstract fun bindCandidateRemoteDataSource(
        candidateRemoteDataSource: CandidateRemoteDataSource
    ): CandidateDataSource
}