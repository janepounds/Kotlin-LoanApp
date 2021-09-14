package com.cabral.emaishapay.data.models

import com.cabral.emaishapay.data.enums.LoanStatus

data class Loan(
    val loanId: String,
    val status: LoanStatus,
    val amt: Long,
    val interestRate:Int,
    val duration:Int,
    val durationType: String,
    val loanDueAmount:Long

)