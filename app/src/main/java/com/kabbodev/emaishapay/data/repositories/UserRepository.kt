package com.kabbodev.emaishapay.data.repositories

import androidx.lifecycle.MutableLiveData
import com.kabbodev.emaishapay.data.models.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "User"

@Singleton
class UserRepository @Inject constructor(

) {
    private var _currentUser: User? = null
    private var currentUser: MutableLiveData<User> = MutableLiveData()


    fun getCurrentUser(viewModelScope: CoroutineScope, userId: String, reload: Boolean): MutableLiveData<User> {
        if (_currentUser == null || reload) viewModelScope.launch { loadCurrentUser(userId) }
        currentUser.postValue(_currentUser)
        return currentUser
    }

    private suspend fun loadCurrentUser(userId: String) {
        _currentUser = User(
            fullName = "Ash Dip",
            emailAddress = "ashdip9@gmail.com",
            phoneNumber = "123456789",
            profileImage = null,
            dateOfBirth = "25/06/1997",
            nin = "CP1351BN23",
            regDate = "01/08/2021",
            location = "Dhaka",
            walletBalance = 25000
        )
        currentUser.postValue(_currentUser)
    }

}