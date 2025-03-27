package ru.nativespeakers.core.token

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import ru.nativespeakers.core.common.IoDispatcher
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenLocalDataSource @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    val token = dataStore.data
        .mapLatest { it[PreferencesKeys.ACCESS_TOKEN] }
        .stateIn(
            scope = CoroutineScope(ioDispatcher),
            started = SharingStarted.Eagerly,
            initialValue = null
        )

    suspend fun putToken(token: String) {
        withContext(ioDispatcher) {
            dataStore.edit {
                it[PreferencesKeys.ACCESS_TOKEN] = token
            }
        }
    }

    suspend fun deleteToken() {
        withContext(ioDispatcher) {
            dataStore.edit {
                it.remove(PreferencesKeys.ACCESS_TOKEN)
            }
        }
    }

    private object PreferencesKeys {
        val ACCESS_TOKEN = stringPreferencesKey("access-token")
    }
}