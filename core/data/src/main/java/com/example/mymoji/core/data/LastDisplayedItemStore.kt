package com.example.mymoji.core.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

private const val DATASTORE_NAME = "last_displayed_item"

private val Context.lastDisplayedItemDataStore: DataStore<Preferences> by preferencesDataStore(
    name = DATASTORE_NAME,
    produceMigrations = { context -> listOf(SharedPreferencesMigration(context, DATASTORE_NAME)) }
)

@Singleton
class LastDisplayedItemStore @Inject constructor(
    @ApplicationContext context: Context
) {
    private val dataStore = context.lastDisplayedItemDataStore

    suspend fun save(item: LastDisplayedItem) {
        dataStore.edit { prefs ->
            when (item) {
                is LastDisplayedItem.Emoji -> {
                    prefs[KEY_TYPE] = TYPE_EMOJI
                    prefs[KEY_VALUE] = item.name
                }
                is LastDisplayedItem.GitHubUser -> {
                    prefs[KEY_TYPE] = TYPE_GITHUB_USER
                    prefs[KEY_VALUE] = item.login
                }
            }
        }
    }

    suspend fun load(): LastDisplayedItem? {
        val prefs = dataStore.data.first()
        val value = prefs[KEY_VALUE] ?: return null
        return when (prefs[KEY_TYPE]) {
            TYPE_EMOJI -> LastDisplayedItem.Emoji(value)
            TYPE_GITHUB_USER -> LastDisplayedItem.GitHubUser(value)
            else -> null
        }
    }

    private companion object {
        val KEY_TYPE = stringPreferencesKey("type")
        val KEY_VALUE = stringPreferencesKey("value")
        const val TYPE_EMOJI = "emoji"
        const val TYPE_GITHUB_USER = "github_user"
    }
}
