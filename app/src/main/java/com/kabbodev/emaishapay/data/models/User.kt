package com.kabbodev.emaishapay.data.models

data class User(
    val fullName: String = "",
    val emailAddress: String = "",
    val phoneNumber: String = "",
    val profileImage: String? = "",
    val dateOfBirth: String = "",
    val nin: String = "",
    val regDate: String = "",
    val location: String = "",
    val walletBalance: Long = 0
)