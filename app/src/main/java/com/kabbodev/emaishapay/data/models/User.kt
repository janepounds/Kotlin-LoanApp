package com.kabbodev.emaishapay.data.models

data class User(
    var fullName: String = "",
    var emailAddress: String = "",
    var phoneNumber: String = "",
    var profileImage: String? = "",
    var dateOfBirth: String = "",
    var nin: String = "",
    var regDate: String = "",
    var location: String = "",
    var walletBalance: Double = 0.0,
    var id: Int?=null,
    var accessToken: String?=null,
    val pin: String?=null,
    val interestRate: Float?=null,
    val processingFee: Double?=null,

)