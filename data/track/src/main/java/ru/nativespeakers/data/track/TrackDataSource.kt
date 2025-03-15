package ru.nativespeakers.data.track

import ru.nativespeakers.core.model.TrackNetwork

interface TrackDataSource {
    suspend fun findById(id: Long): Result<TrackNetwork>
    suspend fun findById(ids: List<Long>): Result<List<TrackNetwork>>
    suspend fun approveApplication(candidateId: Long, vacancyId: Long): Result<TrackNetwork>
    suspend fun setHrForTrack(trackId: Long, hrId: Long): Result<Unit>
    suspend fun finishTrackById(id: Long): Result<Unit>
    suspend fun createTrack(candidateId: Long, vacancyId: Long): Result<TrackNetwork>
}