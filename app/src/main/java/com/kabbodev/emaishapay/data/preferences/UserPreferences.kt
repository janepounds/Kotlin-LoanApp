package com.kabbodev.emaishapay.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferences(private val dataStore: DataStore<Preferences>) {

    val showIntro: Flow<Boolean?>
        get() = dataStore.data.map { preferences ->
            preferences[KEY_SHOW_INTRO]
        }

    val isLoggedIn: Flow<Boolean?>
        get() = dataStore.data.map { preferences ->
            preferences[KEY_LOGGED_IN]
        }

    suspend fun saveShowIntro(showIntro: Boolean) {
        dataStore.edit { mutablePreferences ->
            mutablePreferences[KEY_SHOW_INTRO] = showIntro
        }
    }

    suspend fun saveIsLoggedIn(isLoggedIn: Boolean) {
        dataStore.edit { mutablePreferences ->
            mutablePreferences[KEY_LOGGED_IN] = isLoggedIn
        }
    }

    suspend fun clear() {
        dataStore.edit { mutablePreferences ->
            mutablePreferences.clear()
            saveShowIntro(false)
        }
    }

    companion object {
        private val KEY_SHOW_INTRO = booleanPreferencesKey("show_intro")
        private val KEY_LOGGED_IN = booleanPreferencesKey("logged_in")
    }

}