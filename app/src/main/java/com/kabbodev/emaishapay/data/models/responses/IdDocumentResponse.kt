package com.kabbodev.emaishapay.data.models.responses

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
    @SerializedName("user_id")
    @Expose
    val user_id:Int,
    @SerializedName("national_id_front")
    @Expose
    val national_id_front: String,
    @SerializedName("updated_at")
    @Expose
    val updated_at: String,
    @SerializedName("created_at")
    @Expose
    val created_at: String,
    @SerializedName("id")
    @Expose
    val id: String,
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
