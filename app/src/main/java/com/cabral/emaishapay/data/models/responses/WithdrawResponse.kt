package com.cabral.emaishapay.data.models.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class WithdrawResponse (
    @SerializedName("status")
    @Expose
    val status:Int,
    @SerializedName("message")
    @Expose
    val message:String,
    @SerializedName("data")
    @Expose
    val data: List<WithdrawData>?

        )
class WithdrawData(
    @SerializedName("txnId")
    @Expose
    val txnId:String,
    @SerializedName("txnAmt")
    @Expose
    val txnAmt:Double,
    @SerializedName("txnDate")
    @Expose
    val txnDate:String,
    @SerializedName("txnCurrency")
    @Expose
    val txnCurrency:String,
    @SerializedName("detination")
    @Expose
    val detination:String
)
