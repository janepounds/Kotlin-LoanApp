package com.kabbodev.emaishapay.data.repositories

import android.content.Context
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import com.kabbodev.emaishapay.data.models.User
import com.kabbodev.emaishapay.data.preferences.UserPreferences
import com.kabbodev.emaishapay.singleton.dataStore
import kotlinx.coroutines.CoroutineScope
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



    fun getCurrentUser(viewModelScope: CoroutineScope, userId: String, reload: Boolean,context: Context): MutableLiveData<User> {
        if (_currentUser == null || reload) viewModelScope.launch { loadCurrentUser(userId,context) }
        currentUser.postValue(_currentUser)
        return currentUser
    }

    private suspend fun loadCurrentUser(userId: String,context: Context) {
        userPreferences = UserPreferences(context.dataStore)
//        if(userId.equals(userPreferences.userId)) {
            _currentUser =  User(
                                fullName = userPreferences.name.asLiveData().value.toString(),
                                emailAddress = userPreferences.email.asLiveData().value.toString(),
                                phoneNumber = userPreferences.phoneNumber.asLiveData().value.toString(),
                                profileImage = null,
                                dateOfBirth = "25/06/1997",
                                nin = "CP1351BN23",
                                regDate = "01/08/2021",
                                location = "Dhaka",
                                walletBalance = 0
                            )

//        }
        currentUser.postValue(_currentUser)
    }

}