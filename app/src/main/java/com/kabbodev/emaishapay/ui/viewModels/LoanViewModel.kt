package com.kabbodev.emaishapay.ui.viewModels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kabbodev.emaishapay.data.models.Loan
import com.kabbodev.emaishapay.data.models.User
import com.kabbodev.emaishapay.data.repositories.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

@HiltViewModel
class LoanViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private var loan: Loan? = null
    var loanAmount: Long = 0
    var loanDueAmount: Long = 0
    var duration: Int = 0
    var type: String = ""
    var typePayment: Long = 0

    fun getCurrentUser(reload: Boolean,context:Context,user:User): LiveData<User> = userRepository.getCurrentUser(viewModelScope,  reload,context,user)

    fun setLoanData(amt: Long, dueAmt: Long, loanDuration: Int, loanDurationType: String, loanTypePayment: Long) {
        loanAmount = amt
        loanDueAmount = dueAmt
        duration = loanDuration
        type = loanDurationType
        typePayment = loanTypePayment
    }

    fun getLoan() = loan

    fun setLoan(updated: Loan) {
        loan = updated
    }

}