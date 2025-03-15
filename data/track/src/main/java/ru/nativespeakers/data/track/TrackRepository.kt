package ru.nativespeakers.data.track

import ru.nativespeakers.core.model.TrackNetwork
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrackRepository @Inject constructor(
    private val trackDataSource: TrackDataSource,
) {
    suspend fun findById(id: Long): Result<TrackNetwork> {
        return trackDataSource.findById(id)
    }

    suspend fun findById(ids: List<Long>): Result<List<TrackNetwork>> {
        return trackDataSource.findById(ids)
    }

    suspend fun approveApplication(candidateId: Long, vacancyId: Long): Result<TrackNetwork> {
        return trackDataSource.approveApplication(candidateId, vacancyId)
    }

    suspend fun setHrForTrack(trackId: Long, hrId: Long): Result<Unit> {
        return trackDataSource.setHrForTrack(trackId, hrId)
    }

    suspend fun finishTrackById(id: Long): Result<Unit> {
        return trackDataSource.finishTrackById(id)
    }

    suspend fun createTrack(candidateId: Long, vacancyId: Long): Result<TrackNetwork> {
        return trackDataSource.createTrack(candidateId, vacancyId)
    }
}