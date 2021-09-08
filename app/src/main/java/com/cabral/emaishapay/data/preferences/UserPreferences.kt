package com.cabral.emaishapay.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import com.cabral.emaishapay.data.models.User
import com.cabral.emaishapay.data.models.UserData
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

   val personalInfo: Flow<User>?
   get() = dataStore.data.catch { exception ->
       if(exception is IOException){
           emit(emptyPreferences())
       }else{
           throw exception
       }
   }.map { preferences ->
       User(
           nin =preferences[NIN]!!,
           dateOfBirth = preferences[DOB]!!
       )
   }

    val businessInfo: Flow<User>?
        get() = dataStore.data.catch { exception ->
            if(exception is IOException){
                emit(emptyPreferences())
            }else{
                throw exception
            }
        }.map { preferences ->
            User(
                regDate = preferences[REG_DATE]!!,
                location = preferences[LOCATION]!!
            )
        }

        /**************functions for retrieving user preferences*****************************/
        val user: Flow<User>?
            get() = dataStore.data.catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }.map {preferences->
                User(
                    id=preferences[USER_ID],
                    fullName = preferences[NAME]!!,
                    emailAddress = preferences[EMAIL]!!,
                    phoneNumber = preferences[PHONE_NUMBER]!!,
                    accessToken=preferences[ACCESS_TOKEN],
                    pin=preferences[PIN],
                    walletBalance= preferences[BALANCE]!!,
                    interestRate=preferences[INTEREST_RATE],
                    processingFee=preferences[PROCESSING_FEE],

                )
            }


    /***********clear user preferences****************/
    suspend fun clear() {
        dataStore.edit { mutablePreferences ->
            mutablePreferences.clear()

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


    suspend fun savePersonalInfo(nin: String,dob:String){
        dataStore.edit { mutablePreferences ->

                mutablePreferences[NIN] = nin
                mutablePreferences[DOB] = dob

        }


    }

    suspend fun saveBusinessInfo(reg_date: String,location:String){
        dataStore.edit { mutablePreferences ->

            mutablePreferences[LOCATION] = location
            mutablePreferences[REG_DATE] = reg_date

        }

        suspend fun saveGuarantorInfo(nin: String,dob:String){
            dataStore.edit { mutablePreferences ->

                mutablePreferences[NIN] = nin
                mutablePreferences[DOB] = dob

            }


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
        private val NIN = stringPreferencesKey("nin")
        private val DOB = stringPreferencesKey("dob")
        private val REG_DATE = stringPreferencesKey("reg_date")
        private val LOCATION = stringPreferencesKey("location")


        private val KEY_SHOW_INTRO = booleanPreferencesKey("show_intro")
        private val KEY_LOGGED_IN = booleanPreferencesKey("logged_in")

    }

}