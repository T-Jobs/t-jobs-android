package ru.nativespeakers.data.track

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class TrackRemoteModule {
    @Binds
    @Singleton
    abstract fun bindTrackRemoteDataSource(
        trackRemoteDataSource: TrackRemoteDataSource
    ): TrackDataSource
}