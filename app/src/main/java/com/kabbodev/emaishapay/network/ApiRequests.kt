package com.kabbodev.emaishapay.network

import com.google.gson.JsonObject
import com.kabbodev.emaishapay.data.models.*
import com.kabbodev.emaishapay.data.models.responses.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.*

interface ApiRequests {
    @FormUrlEncoded
    @POST("api/initialise/registration")
    fun signUp(
        @Field("request_id") request_id: String?,
        @Field("action_id") action_id: String?,
        @Field("name") name: String?,
        @Field("phoneNumber") phoneNumber: String?,
        @Field("email") email: String?,

    ): Call<RegistrationResponse>?

    @FormUrlEncoded
    @POST("api/verify/registration")
    fun verifyRegistration(
        @Field("otp") otp: String?,
        @Field("phoneNumber") phoneNumber: String?,
        @Field("action_id") action_id:String?,
        @Field("request_id")request_id: String?,
        @Field("pin")pin:String?

    ): Call<AuthenticationResponse>?


    @FormUrlEncoded
    @POST("api/resend-otp/registration")
    fun resendOtp(
        @Field("phoneNumber") phoneNumber: String?,
        @Field("action_id") action_id: String?,
        @Field("request_id") request_id: String?

    ): Call<RegistrationResponse>?

    @FormUrlEncoded
    @POST("api/initiate/login")
    fun initiateLogin(
        @Field("phoneNumber") phoneNumber: String?,
        @Field("pin") pin: String?,
        @Field("request_id") request_id: String?,
        @Field("action_id") action_id: String?


    ): Call<AuthenticationResponse>?


    @FormUrlEncoded
    @POST("api/change/pin")
    fun changePin(
        @Field("phoneNumber") phoneNumber: String?,
        @Field("currentPin") currentPin: String?,
        @Field("request_id") request_id: String?,
        @Field("action_id") action_id: String?,
        @Field("newPin") newPin: String?,
        @Field("confirmNewPin") confirmNewPin: String?


    ): Call<ChangePinResponse>?


    @FormUrlEncoded
    @POST("api/update/business/personaldetails")
    fun postPersonalDetails(
        @Header("Authorization") token:String?,
        @Field("name") name:String?,
        @Field("gender") gender:String?,
        @Field("dob") dob:String?,
        @Field("education_level")education_level:String?,
        @Field("marital_status")marital_status:String?,
        @Field("years_in_business")years_in_business:Int?,
        @Field("nin") nin:String?,
        @Field("request_id") request_id: String?,
        @Field("action_id") action_id: String?
    ):Call<RegistrationResponse>?



    @GET("api/get/business/personaldetails")
    fun getPersonalDetails(
        @Header("Authorization") token:String?,
        @Query("request_id") request_id: String?,
        @Query("action_id") action_id: String?
    ):Call<UserResponse>?

    @FormUrlEncoded
    @POST("api/update/business/contactdetails")
    fun postContactDetails(
        @Header("Authorization") token:String?,
        @Field("district") district:String?,
        @Field("village") village:String?,
        @Field("residential_type") residential_type:String?,
        @Field("mobile_phone") mobile_phone:String?,
        @Field("landlord") landlord:String?,
        @Field("landlord_contact") landlord_contact:String?,
        @Field("request_id") request_id:String?,
        @Field("action_id") action_id:String?,

    ):Call<ContactResponse>?

    @GET("api/get/business/contactdetails")
    fun getContactDetails(
        @Header("Authorization") token:String?,
        @Query("request_id") request_id: String?,
        @Query("action_id") action_id: String?
    ):Call<ContactResponse>?

    @FormUrlEncoded
    @POST("api/update/business/guarantors")
    fun postGuarantorDetails(
        @Header("Authorization") token:String?,
        @Field("name1") name:String?,
        @Field("gender1") gender:String?,
        @Field("relationship1") relationship:String?,
        @Field("mobile_phone1") phoneNumber:String?,
        @Field("residential_address1") address:String?,
        @Field("name2") name2:String?,
        @Field("gender2") gender2:String?,
        @Field("relationship2") relationship2:String?,
        @Field("mobile_phone2") mobile_phone2:String?,
        @Field("residential_address2") residential_address2:String?,
        @Field("action_id") action_id: String?,
        @Field("request_id") request_id: String?

    ):Call<GuarantorResponse>?

    @GET("api/get/business/guarantors")
    fun getGuarantorDetails(
        @Header("Authorization") token:String?,
        @Query("request_id") request_id: String?,
        @Query("action_id") action_id: String?
    ):Call<GuarantorResponse>?



    @FormUrlEncoded
    @POST("api/update/business/Ids")
    fun postIdDocuments(
        @Header("Authorization") token:String?,
        @Field("profile_picture") pics:JSONObject,
        @Field("request_id") request_id: String?,
        @Field("action_id") action_id: String?
    ):Call<IdDocumentResponse>


    @GET("api/get/business/iddocs")
    fun getIdDocuments(
        @Header("Authorization") token:String?,
        @Query("request_id") request_id: String?,
        @Query("action_id") action_id: String?
    ):Call<IdDocumentResponse>
//
//    @FormUrlEncoded
//    @POST()
//    fun postBusinessDocuments()

}