package ru.nativespeakers.data.auth

import kotlinx.coroutines.flow.mapLatest
import ru.nativespeakers.core.token.TokenLocalDataSource
import ru.nativespeakers.data.auth.dto.LoginDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val authDataSource: AuthDataSource,
    private val tokenDataSource: TokenLocalDataSource,
) {
    val isAuthenticated = tokenDataSource.token.mapLatest { it != null }

    suspend fun login(loginDto: LoginDto): Result<Unit> {
        val loginResult = authDataSource.login(loginDto)
        if (loginResult.isSuccess) {
            val loginResponse = loginResult.getOrThrow()
            tokenDataSource.putToken(loginResponse.token)
            return Result.success(Unit)
        } else {
            return Result.failure(loginResult.exceptionOrNull()!!)
        }
    }

    suspend fun roles(): Result<List<ru.nativespeakers.core.model.AppRole>> {
        val rolesResult = authDataSource.roles()
        if (rolesResult.isSuccess) {
            val userRoles = rolesResult.getOrThrow()
            return Result.success(userRoles)
        } else {
            return Result.failure(rolesResult.exceptionOrNull()!!)
        }
    }

    suspend fun logout() {
        tokenDataSource.deleteToken()
    }
}