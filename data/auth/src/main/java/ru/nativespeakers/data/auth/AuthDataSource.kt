package ru.nativespeakers.data.auth

import ru.nativespeakers.core.model.AppRole
import ru.nativespeakers.data.auth.dto.LoginDto
import ru.nativespeakers.data.auth.dto.LoginResponse

interface AuthDataSource {
    suspend fun login(loginDto: LoginDto): Result<LoginResponse>
    suspend fun roles(): Result<List<AppRole>>
}