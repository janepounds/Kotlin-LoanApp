package com.cabral.emaishapay.data.models.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ChangePinResponse (
    @SerializedName("status")
    @Expose
    val status: Int?,
    @SerializedName("message")
    @Expose
    val message: String?

        )