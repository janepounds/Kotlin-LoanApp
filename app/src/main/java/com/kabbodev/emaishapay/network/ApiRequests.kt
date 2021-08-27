package com.kabbodev.emaishapay.network

import com.kabbodev.emaishapay.data.models.*
import com.kabbodev.emaishapay.data.models.responses.ChangePinResponse
import com.kabbodev.emaishapay.data.models.responses.UserResponse
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
    @POST()
    fun postContactDetails()

    @FormUrlEncoded
    @POST()
    fun postGuarantorDetails()
//
//    @FormUrlEncoded
//    @POST()
//    fun postDocuments()
//
//    @FormUrlEncoded
//    @POST()
//    fun postBusinessDetails()
//
//    @FormUrlEncoded
//    @POST()
//    fun postBusinessDocuments()

}