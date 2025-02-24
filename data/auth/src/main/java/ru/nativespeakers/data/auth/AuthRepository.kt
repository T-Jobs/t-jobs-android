package ru.nativespeakers.data.auth

import ru.nativespeakers.core.token.TokenLocalDataSource
import ru.nativespeakers.data.auth.dto.LoginDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val authDataSource: AuthDataSource,
    private val tokenDataSource: TokenLocalDataSource,
) {
    suspend fun login(loginDto: LoginDto): Result<List<AppRole>> {
        val loginResult = authDataSource.login(loginDto)
        if (loginResult.isSuccess) {
            val loginResponse = loginResult.getOrThrow()
            tokenDataSource.putToken(loginResponse.token)
            return Result.success(loginResponse.roles)
        } else {
            return Result.failure(loginResult.exceptionOrNull()!!)
        }
    }

    suspend fun logout() {
        tokenDataSource.deleteToken()
    }
}