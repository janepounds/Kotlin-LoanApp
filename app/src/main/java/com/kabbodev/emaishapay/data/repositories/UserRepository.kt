package com.kabbodev.emaishapay.data.repositories

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import com.kabbodev.emaishapay.data.models.User
import com.kabbodev.emaishapay.data.preferences.UserPreferences
import com.kabbodev.emaishapay.singleton.dataStore
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
                dateOfBirth = "25/06/1997",
                nin = "CP1351BN23",
                regDate = "01/08/2021",
                location = "Dhaka",
                walletBalance = it.walletBalance,

                )

            currentUser.postValue(_currentUser)
        }


    }

}