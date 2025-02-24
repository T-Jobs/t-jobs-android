package ru.nativespeakers.data.auth

import ru.nativespeakers.data.auth.dto.LoginDto
import ru.nativespeakers.data.auth.dto.LoginResponse
import ru.nativespeakers.data.auth.exception.UnauthorizedException

class AuthFakeDataSource : AuthDataSource {
    override suspend fun login(loginDto: LoginDto): Result<LoginResponse> {
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