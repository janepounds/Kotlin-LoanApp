package com.kabbodev.emaishapay.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
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

    val userId: Flow<Int?>
    get() = dataStore.data.map { preferences-> preferences[USER_ID]  }

    val name: Flow<String?>
        get() = dataStore.data.map { preferences-> preferences[NAME]  }

    val phoneNumber: Flow<String?>
        get() = dataStore.data.map { preferences-> preferences[PHONE_NUMBER]  }

    val email: Flow<String?>
        get() = dataStore.data.map { preferences-> preferences[EMAIL]  }

    val balance: Flow<Long?>
        get() = dataStore.data.map { preferences-> preferences[BALANCE]  }

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

    suspend fun saveUserId(userId: Int){
        dataStore.edit { mutablePreferences -> mutablePreferences[USER_ID] = userId }
    }

    suspend fun saveName(name: String){
        dataStore.edit { mutablePreferences -> mutablePreferences[NAME] = name }
    }

    suspend fun savePhoneNumber(phoneNumber: String){
        dataStore.edit { mutablePreferences -> mutablePreferences[PHONE_NUMBER] = phoneNumber }
    }
    suspend fun saveEmail(email: String){
        dataStore.edit { mutablePreferences -> mutablePreferences[EMAIL] = email }
    }
    suspend fun saveBalance(balance: Long){
        dataStore.edit { mutablePreferences -> mutablePreferences[BALANCE] = balance }
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
        private val USER_ID = intPreferencesKey("user_id")
        private val NAME = stringPreferencesKey("name")
        private val PHONE_NUMBER = stringPreferencesKey("phoneNumber")
        private val EMAIL = stringPreferencesKey("email")
        private val BALANCE = longPreferencesKey("balance")

    }

}