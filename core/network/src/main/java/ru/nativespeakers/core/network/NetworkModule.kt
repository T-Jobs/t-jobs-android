package ru.nativespeakers.core.network

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.HttpRequestPipeline
import io.ktor.http.HttpHeaders
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

        install("AuthInterceptor") {
            requestPipeline.intercept(HttpRequestPipeline.Before) {
                with(context.headers) {
                    remove(HttpHeaders.Authorization)
                    tokenLocalDataSource.token.value?.let {
                        append(HttpHeaders.Authorization, "Bearer $it")
                    }
                }
            }
        }

        install(Logging) {
            level = LogLevel.ALL
            logger = Logger.ANDROID
        }

        defaultRequest {
            host = "10.0.2.2"
            port = 8080
            url { protocol = URLProtocol.HTTP }
        }
    }
}