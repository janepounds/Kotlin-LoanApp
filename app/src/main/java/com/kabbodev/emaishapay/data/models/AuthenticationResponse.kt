package com.kabbodev.emaishapay.data.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AuthenticationResponse(
    @SerializedName("status")
    @Expose
    val status:Int,
    @SerializedName("message")
    @Expose
    val message: String?,
    @SerializedName("access_token")
    @Expose
    val access_token: String?,
    @SerializedName("data")
    @Expose
    val data: UserData?

)

    class UserData(

        @SerializedName("id")
        @Expose
        val id:Int,
        @SerializedName("name")
        @Expose
        val name: String?,
        @SerializedName("email")
        @Expose
        val email: String?,
        @SerializedName("phoneNumber")
        @Expose
        val phoneNumber: String?,
        @SerializedName("balance")
        @Expose
        var balance: Double?
)
