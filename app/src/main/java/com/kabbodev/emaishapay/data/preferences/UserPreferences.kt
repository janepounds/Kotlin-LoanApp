package com.kabbodev.emaishapay.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import com.kabbodev.emaishapay.data.models.UserData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class UserPreferences(private val dataStore: DataStore<Preferences>) {

    val showIntro: Flow<Boolean?>
        get() = dataStore.data.map { preferences ->
            preferences[KEY_SHOW_INTRO]
        }

    val isLoggedIn: Flow<Boolean?>
        get() = dataStore.data.map { preferences ->
            preferences[KEY_LOGGED_IN]
        }



        /**************functions for retrieving user preferences*****************************/

    val userId: Flow<Int?>
    get() = dataStore.data.catch { exception ->
        if (exception is IOException) {
            emit(emptyPreferences())
        } else {
            throw exception
        }
    }.map { preferences-> preferences[USER_ID]  }

    val name: Flow<String?>
        get() = dataStore.data.map { preferences-> preferences[NAME]  }

    val phoneNumber: Flow<String?>
        get() = dataStore.data.map { preferences-> preferences[PHONE_NUMBER]  }

    val email: Flow<String?>
        get() = dataStore.data.map { preferences-> preferences[EMAIL]  }

    val balance: Flow<Double?>
        get() = dataStore.data.map { preferences-> preferences[BALANCE]  }

    val interest_rate: Flow<Float?>
        get() = dataStore.data.map { preferences-> preferences[INTEREST_RATE]  }

    val processing_fee: Flow<Double?>
        get() = dataStore.data.map { preferences-> preferences[PROCESSING_FEE]  }

    val acess_token: Flow<String?>
        get() = dataStore.data.map { preferences-> preferences[ACCESS_TOKEN]  }

    val pin :Flow<String?>
    get() = dataStore.data.map { preferences-> preferences[PIN] }



    /***********clear user preferences****************/
    suspend fun clear() {
        dataStore.edit { mutablePreferences ->
            mutablePreferences.clear()
            saveShowIntro(false)
            saveIsLoggedIn(false)
        }
    }

    suspend fun saveShowIntro(showIntro: Boolean) {
        dataStore.edit { mutablePreferences ->
            mutablePreferences[KEY_SHOW_INTRO] = showIntro
        }
    }

    suspend fun saveIsLoggedIn(login: Boolean) {
        dataStore.edit { mutablePreferences ->
            mutablePreferences[KEY_LOGGED_IN] = login
        }
    }

    suspend fun saveUserData(userData: UserData?, accessToken: String?, pinValue: String,isLoggedIn: Boolean) {
        dataStore.edit { mutablePreferences ->
            userData?.let {
                mutablePreferences[USER_ID] = it.id
                mutablePreferences[NAME] = it.name
                mutablePreferences[PHONE_NUMBER] = it.phoneNumber
                mutablePreferences[EMAIL] = it.email
                mutablePreferences[BALANCE] = it.balance
                mutablePreferences[INTEREST_RATE] = it.interest_rate
                mutablePreferences[PROCESSING_FEE] = it.processing_fee
            }
            accessToken?.let { mutablePreferences[ACCESS_TOKEN] =it }
            mutablePreferences[PIN] = pinValue
            mutablePreferences[KEY_LOGGED_IN] = isLoggedIn
        }
    }


    companion object {
        private val USER_ID = intPreferencesKey("user_id")
        private val ACCESS_TOKEN = stringPreferencesKey("access_token")
        private val PIN = stringPreferencesKey("pin")
        private val NAME = stringPreferencesKey("name")
        private val PHONE_NUMBER = stringPreferencesKey("phoneNumber")
        private val EMAIL = stringPreferencesKey("email")
        private val BALANCE = doublePreferencesKey("balance")
        private val INTEREST_RATE = floatPreferencesKey("interest_rate")
        private val PROCESSING_FEE = doublePreferencesKey("processing_fee")


        private val KEY_SHOW_INTRO = booleanPreferencesKey("show_intro")
        private val KEY_LOGGED_IN = booleanPreferencesKey("logged_in")

    }

}