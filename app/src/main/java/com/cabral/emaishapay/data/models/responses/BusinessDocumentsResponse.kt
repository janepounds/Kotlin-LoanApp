package com.cabral.emaishapay.data.models.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class BusinessDocumentsResponse (
        @SerializedName("status")
        @Expose
        val status:Int,
        @SerializedName("message")
        @Expose
        val message:String,
        @SerializedName("data")
        @Expose
        val data:BusinessDocumentsData?

        )
class BusinessDocumentsData(
        @SerializedName("trade_license")
        @Expose
        val trade_license:String,
        @SerializedName("reg_certificate")
        @Expose
        val reg_certificate:String,
        @SerializedName("tax_reg_certificate")
        @Expose
        val tax_reg_certificate:String,
        @SerializedName("tax_clearance_certificate")
        @Expose
        val tax_clearance_certificate:String,
        @SerializedName("bank_statement")
        @Expose
        val bank_statement:String,
        @SerializedName("audited_financials")
        @Expose
        val audited_financials:String,
        @SerializedName("business_plan")
        @Expose
        val business_plan:String,
        @SerializedName("receipt_book")
        @Expose
        val receipt_book:String,
)
