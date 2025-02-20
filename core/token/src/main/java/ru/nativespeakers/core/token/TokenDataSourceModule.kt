package ru.nativespeakers.core.token

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import ru.nativespeakers.core.common.IoDispatcher
import javax.inject.Singleton

private const val DATA_STORE_NAME = "user-preferences"

@Module
@InstallIn(SingletonComponent::class)
internal abstract class TokenDataSourceModule {
    companion object {
        @Provides
        @Singleton
        fun providePreferencesDataStore(
            @ApplicationContext appContext: Context,
            @IoDispatcher ioDispatcher: CoroutineDispatcher
        ): DataStore<Preferences> =
            PreferenceDataStoreFactory.create(
                corruptionHandler = ReplaceFileCorruptionHandler(
                    produceNewData = { emptyPreferences() }
                ),
                scope = CoroutineScope(ioDispatcher + SupervisorJob()),
                produceFile = { appContext.preferencesDataStoreFile(DATA_STORE_NAME) }
            )
    }
}