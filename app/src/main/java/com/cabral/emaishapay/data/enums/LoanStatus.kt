package com.cabral.emaishapay.data.enums

enum class LoanStatus(val status: String) {
    APPROVED("Approved"),
    PAID("Paid"),
    PENDING("Pending"),
    PARTIALLY_PAID("Partially Paid"),
    REJECTED("Rejected")
}