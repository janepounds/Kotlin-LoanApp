package com.cabral.emaishapay.ui.viewModels

import androidx.lifecycle.ViewModel
import com.cabral.emaishapay.data.repositories.LoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: LoginRepository
) : ViewModel() {

    private var phoneNumber: String? = null

    fun setPhoneNumber(updated: String) {
        phoneNumber = updated
    }

    fun getPhoneNumber() = phoneNumber

   private var otp: String? = null
   private var pin: String? = null

    fun setPin(updated: String){
        pin = updated
    }
    fun getPin() = pin

    fun setOtp(otp_updated:String){
        otp = otp_updated
    }

    fun getOtp() = otp

}