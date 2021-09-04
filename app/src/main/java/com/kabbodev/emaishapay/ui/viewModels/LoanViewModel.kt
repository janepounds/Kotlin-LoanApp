package com.kabbodev.emaishapay.ui.viewModels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kabbodev.emaishapay.constants.Constants
import com.kabbodev.emaishapay.data.models.Loan
import com.kabbodev.emaishapay.data.models.User
import com.kabbodev.emaishapay.data.models.Withdraw
import com.kabbodev.emaishapay.data.models.responses.LoanResponse
import com.kabbodev.emaishapay.data.repositories.UserRepository
import com.kabbodev.emaishapay.network.ApiClient
import com.kabbodev.emaishapay.network.ApiRequests
import com.kabbodev.emaishapay.utils.DialogLoader
import com.kabbodev.emaishapay.utils.generateRequestId
import com.kabbodev.emaishapay.utils.snackbar
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
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
    var interestRate: Int =0
    var processingFee: Long =0
    private var withdraw: Withdraw? = null


    fun getCurrentUser(reload: Boolean,context:Context): LiveData<User> = userRepository.getCurrentUser(viewModelScope,  reload,context)

    fun setLoanData(amt: Long, dueAmt: Long, loanDuration: Int, loanDurationType: String, loanTypePayment: Long,loanInterestRate:Int,loanProcessingFee:Long) {
        loanAmount = amt
        loanDueAmount = dueAmt
        duration = loanDuration
        type = loanDurationType
        typePayment = loanTypePayment
        interestRate = loanInterestRate
        processingFee = loanProcessingFee
    }

    fun getLoan() = loan

    fun setLoan(updated: Loan) {
        loan = updated
    }


    fun setWithdraw(updated: Withdraw) {
        withdraw = updated
    }

    fun getWithdraw() = withdraw


    fun setPayment(payment: Withdraw) {
        withdraw = payment
    }

    fun getPayment() = withdraw

}