package ru.nativespeakers.data.auth

import kotlinx.coroutines.delay
import ru.nativespeakers.data.auth.dto.LoginDto
import ru.nativespeakers.data.auth.dto.LoginResponse
import ru.nativespeakers.data.auth.exception.UnauthorizedException
import javax.inject.Inject

internal class AuthFakeDataSource @Inject constructor(): AuthDataSource {
    override suspend fun login(loginDto: LoginDto): Result<LoginResponse> {
        delay(1000)
        return when {
            loginDto.email != UserCredentials["email"] -> Result.failure(UnauthorizedException())
            loginDto.password != UserCredentials["password"] -> Result.failure(UnauthorizedException())
            else -> Result.success(
                LoginResponse(
                    token = "12345",
                    roles = listOf(AppRole.HR, AppRole.TEAM_LEAD, AppRole.INTERVIEWER)
                )
            )
        }
    }
}