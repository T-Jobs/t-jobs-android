package ru.nativespeakers.data.auth

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.nativespeakers.core.common.IoDispatcher
import ru.nativespeakers.data.auth.dto.LoginDto
import ru.nativespeakers.data.auth.dto.LoginResponse
import ru.nativespeakers.data.auth.exception.UnauthorizedException
import javax.inject.Inject

internal class AuthRemoteDataSource @Inject constructor(
    private val httpClient: HttpClient,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : AuthDataSource {
    override suspend fun login(loginDto: LoginDto): Result<LoginResponse> =
        withContext(ioDispatcher) {
            val response = httpClient.post("/auth/login") {
                contentType(ContentType.Application.Json)
                setBody(loginDto)
            }

            when (response.status) {
                HttpStatusCode.OK -> Result.success(response.body())
                HttpStatusCode.Unauthorized -> Result.failure(UnauthorizedException())
                else -> throw IllegalStateException("Unknown code response - ${response.status.value}")
            }
        }
}