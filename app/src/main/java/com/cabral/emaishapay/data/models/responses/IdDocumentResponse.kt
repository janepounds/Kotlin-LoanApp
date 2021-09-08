package com.cabral.emaishapay.data.models.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class IdDocumentResponse (
    @SerializedName("status")
    @Expose
    val status:Int,
    @SerializedName("message")
    @Expose
    val message: String,
    @SerializedName("data")
    @Expose
    val data: IdDocumentData?
        )

class IdDocumentData(
    @SerializedName("national_id_front")
    @Expose
    val national_id_front: String,
    @SerializedName("national_id_back")
    @Expose
    val national_id_back: String,
    @SerializedName("selfie_in_business")
    @Expose
    val selfie_in_business: String,
    @SerializedName("profile_picture")
    @Expose
    val profile_picture: String,

)
