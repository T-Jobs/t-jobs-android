package ru.nativespeakers.data.interview

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class InterviewRemoteModule {
    @Binds
    @Singleton
    abstract fun bindInterviewRemoteDataSource(
        interviewRemoteDataSource: InterviewRemoteDataSource,
    ): InterviewDataSource
}