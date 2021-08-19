package com.kabbodev.emaishapay.data.models

import com.kabbodev.emaishapay.data.enums.LoanStatus

data class Loan(
    val loanId: String,
    val status: LoanStatus,
    val amt: Long,
)