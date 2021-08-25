package com.kabbodev.emaishapay.data.preferences

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
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
        /**************functions for retrieving user preferences*****************************/

    val userId: Flow<Int?>
    get() = dataStore.data.map { preferences-> preferences[USER_ID]  }

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





    /****************functions for saving  user preference****************************************/

    suspend fun saveUserId(userId: Int){
        dataStore.edit { mutablePreferences -> mutablePreferences[USER_ID] = userId }
    }

    suspend fun saveDoublePreference(value: Double,myKey: Preferences.Key<Double>){
        dataStore.edit { mutablePreferences -> mutablePreferences[myKey] = value }
    }
    suspend fun saveInterestRate(interest_rate: Float){
        dataStore.edit { mutablePreferences -> mutablePreferences[INTEREST_RATE] = interest_rate }
    }

    suspend fun savePreference( value: String, myKey: Preferences.Key<String>  ){
        dataStore.edit { mutablePreferences -> mutablePreferences[myKey] = value }
    }

    /***********clear user preferences****************/
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
        private val ACCESS_TOKEN = stringPreferencesKey("access_token")
        private val PIN = stringPreferencesKey("pin")
        private val NAME = stringPreferencesKey("name")
        private val PHONE_NUMBER = stringPreferencesKey("phoneNumber")
        private val EMAIL = stringPreferencesKey("email")
        private val BALANCE = doublePreferencesKey("balance")
        private val INTEREST_RATE = floatPreferencesKey("interest_rate")
        private val PROCESSING_FEE = doublePreferencesKey("processing_fee")

    }

}