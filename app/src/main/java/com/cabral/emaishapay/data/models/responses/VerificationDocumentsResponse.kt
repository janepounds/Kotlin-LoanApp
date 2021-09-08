package com.cabral.emaishapay.data.models.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class VerificationDocumentsResponse (
        @SerializedName("status")
        @Expose
        val status:Int,
        @SerializedName("message")
        @Expose
        val message:String,
        @SerializedName("data")
        @Expose
        val data: VerificationDocumentsData?
        )

class VerificationDocumentsData(
        @SerializedName("business_photo")
        @Expose
        val business_photo:String,
        @SerializedName("selfie_in_business")
        @Expose
        val selfie_in_business:String,
        @SerializedName("neighbourhood_photo")
        @Expose
        val neighbourhood_photo:String,
        @SerializedName("utility_bill")
        @Expose
        val utility_bill:String,
        @SerializedName("business_video")
        @Expose
        val business_video:String
)