package ru.nativespeakers.core.network

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.serialization.kotlinx.json.json
import ru.nativespeakers.core.token.TokenLocalDataSource
import javax.inject.Singleton

private const val SERVER_URL = "http://localhost:1010/"

@Module
@InstallIn(SingletonComponent::class)
internal object NetworkModule {
    @Provides
    @Singleton
    fun provideHttpClient(tokenLocalDataSource: TokenLocalDataSource) = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json()
        }

        defaultRequest {
            url(SERVER_URL)
        }

        install(Auth) {
            bearer {
                loadTokens {
                    val token = tokenLocalDataSource.token() ?: return@loadTokens null
                    BearerTokens(accessToken = token, refreshToken = null)
                }
            }
        }
    }
}