package com.cabral.emaishapay.data.repositories

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.cabral.emaishapay.data.models.User
import com.cabral.emaishapay.data.preferences.UserPreferences
import com.cabral.emaishapay.singleton.dataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "User"

@Singleton
class UserRepository @Inject constructor(

) {
    private var _currentUser: User? = null
    private lateinit var userPreferences: UserPreferences
    private var currentUser: MutableLiveData<User> = MutableLiveData()




    fun getCurrentUser(viewModelScope: CoroutineScope, reload: Boolean,context: Context): MutableLiveData<User> {
        if (_currentUser == null || reload) viewModelScope.launch { loadCurrentUser(context) }
        currentUser.postValue(_currentUser)
        return currentUser
    }



    private suspend fun loadCurrentUser(context: Context) {
        userPreferences = UserPreferences(context.dataStore)

        userPreferences.user?.collect {
            _currentUser =  User(
                fullName = it.fullName,
                emailAddress = it.emailAddress,
                phoneNumber = it.phoneNumber,
                profileImage = null,
                dateOfBirth = it.dateOfBirth,
                nin = it.nin,
                regDate = it.regDate,
                location = it.location,
                walletBalance = it.walletBalance,

                )

            currentUser.postValue(_currentUser)
        }


    }

}