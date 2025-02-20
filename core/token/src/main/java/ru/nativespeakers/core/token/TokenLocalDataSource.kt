package ru.nativespeakers.core.token

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import ru.nativespeakers.core.common.IoDispatcher
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenLocalDataSource @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    suspend fun token(): String? = withContext(ioDispatcher) {
        dataStore.data.first()[PreferencesKeys.ACCESS_TOKEN]
    }

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