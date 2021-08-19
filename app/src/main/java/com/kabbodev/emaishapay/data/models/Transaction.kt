package com.kabbodev.emaishapay.data.models

data class Transaction(
    val txnId: String,
    val txnDate: String,
    val txnAmt: Long,
)