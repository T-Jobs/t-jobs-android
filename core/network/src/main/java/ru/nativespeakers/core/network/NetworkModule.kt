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
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.URLProtocol
import io.ktor.serialization.kotlinx.json.json
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import ru.nativespeakers.core.token.TokenLocalDataSource
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object NetworkModule {
    @Provides
    @Singleton
    fun provideHttpClient(tokenLocalDataSource: TokenLocalDataSource) = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(Json {
                serializersModule = SerializersModule {
                    contextual(LocalDateTime::class, LocalDateTimeSerializer)
                }
            })
        }

        install(Logging) {
            logger = Logger.ANDROID
        }

        defaultRequest {
            host = "10.0.2.2"
            port = 8080
            url { protocol = URLProtocol.HTTP }
        }

        install(Auth) {
            bearer {
                loadTokens {
                    val token = tokenLocalDataSource.token.value ?: return@loadTokens null
                    BearerTokens(accessToken = token, refreshToken = null)
                }
                refreshTokens {
                    val token = tokenLocalDataSource.token.value ?: return@refreshTokens null
                    BearerTokens(accessToken = token, refreshToken = null)
                }
            }
        }
    }
}