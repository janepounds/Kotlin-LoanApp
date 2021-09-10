package com.cabral.emaishapay.network

import com.google.gson.JsonObject
import com.cabral.emaishapay.data.models.*
import com.cabral.emaishapay.data.models.responses.*
import okhttp3.RequestBody
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
        @Field("pin") pin: String?,
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
    @POST("api/initiate/forgotpin")
    fun forgotPassword(
        @Field("phoneNumber") phoneNumber: String?,
        @Field("request_id") request_id: String?,
        @Field("action_id") action_id: String?,

    ): Call<AuthenticationResponse>?

    @FormUrlEncoded
    @POST("api/confirm/changepin")
    fun confirmPin(
        @Field("phoneNumber") phoneNumber: String?,
        @Field("request_id") request_id: String?,
        @Field("action_id") action_id: String?,
        @Field("otp") otp: String?,
        @Field("newpin") newpin: String?,

        ): Call<AuthenticationResponse>?

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
        @Body pics:JsonObject,
        @Field("request_id") request_id: String?,
        @Field("action_id") action_id: String?
    ):Call<IdDocumentResponse>


    @GET("api/get/business/iddocs")
    fun getIdDocuments(
        @Header("Authorization") token:String?,
        @Query("request_id") request_id: String?,
        @Query("action_id") action_id: String?
    ):Call<IdDocumentResponse>

    @FormUrlEncoded
    @POST("api/update/businessdetails")
    fun postBusinessDetails(
        @Header("Authorization") token:String?,
        @Field("business_name") business_name:String?,
        @Field("business_type") business_type:String?,
        @Field("reg_no") reg_no:String?,
        @Field("reg_date") reg_date:String?,
        @Field("industry") industry:String?,
        @Field("location") location:String?,
        @Field("contact_person") contact_person:String?,
        @Field("phone_number") phone_number:String?,
        @Field("no_employees") no_employees:String?,
        @Field("avg_monthly_revenue") avg_monthly_revenue:Double?,
        @Field("request_id") request_id: String?,
        @Field("action_id") action_id: String?
    ):Call<BusinessDetailsResponse>


    @GET("api/get/businessdetails")
    fun getBusinessDetails(
        @Header("Authorization") token:String?,
        @Query("request_id") request_id: String?,
        @Query("action_id") action_id: String?
    ):Call<BusinessDetailsResponse>


    @POST("api/update/businessVerification")
    fun postVerificationDocuments(
        @Header("Authorization") token:String?,
        @Body request: JsonObject,
        @Query("request_id") request_id: String?,
        @Query("action_id") action_id: String?,
    ):Call<VerificationDocumentsResponse>


    @GET("api/get/businessVerification")
    fun getVerificationDocuments(
        @Header("Authorization") token:String?,
        @Query("request_id") request_id: String?,
        @Query("action_id") action_id: String?
    ):Call<VerificationDocumentsResponse>


    @POST("api/update/businessDocuments")
    fun postBusinessDocuments(
        @Header("Authorization") token:String?,
        @Body data:JsonObject,
        @Query("request_id") request_id: String?,
        @Query("action_id") action_id: String?
    ):Call<BusinessDocumentsResponse>


    @GET("api/get/businessDocuments")
    fun getBusinessDocuments(
        @Header("Authorization") token:String?,
        @Query("request_id") request_id: String?,
        @Query("action_id") action_id: String?
    ):Call<BusinessDocumentsResponse>

    @GET("api/get/businessdetails")
    fun getUser(
        @Header("Authorization") token:String?,
        @Query("request_id") request_id: String?,
        @Query("action_id") action_id: String?
    ):Call<UserResponse>

    @GET("api/get/creditscore")
    fun getCreditScore(
        @Header("Authorization") token:String?,
        @Query("request_id") request_id: String?,
        @Query("action_id") action_id: String?
    ):Call<CreditScoreResponse>

    @GET("api/get/pastwithdraws")
    fun getPastWithdraw(
        @Header("Authorization") token:String?,
        @Query("request_id") request_id: String?,
        @Query("action_id") action_id: String?
    ):Call<WithdrawResponse>

    @GET("api/get/businessdetails")
    fun getWithdrawRecepient(
        @Header("Authorization") token:String?,
        @Query("request_id") request_id: String?,
        @Query("action_id") action_id: String?
    ):Call<CreditScoreResponse>

    @FormUrlEncoded
    @POST("api/momo/withdraw")
    fun withdrawFunds(
        @Header("Authorization") token:String?,
        @Field("receipt_number") receipt_number:String?,
        @Field("amount") amount:Long?,
        @Field("pin") pin:String?,
        @Field("currency_code") currency_code:String?,
        @Field("request_id") request_id: String?,
        @Field("action_id") action_id: String?
    ):Call<WithdrawResponse>

    @FormUrlEncoded
    @POST("api/apply/loan")
    fun postNewLoan(
        @Header("Authorization") token:String?,
        @Field("amount") loanAmount:Double?,
        @Field("duration") duration:Int?,
        @Field("duration_units") duration_units:String?,
        @Field("amount_due") amount_due:Double?,
        @Field("interest_rate") interestRate:Int?,
        @Field("processing_fee") processingFee:Double?,
        @Field("pin") pin:String?,
        @Field("request_id") request_id:String?,
        @Field("action_id") action_id:String?,
    ):Call<LoanInitiationResponse>

    @GET("api/get/loanhistory")
    fun getLoanHistory(
        @Header("Authorization") token:String?,
        @Query("request_id") request_id: String?,
        @Query("action_id") action_id: String?
    ):Call<LoanResponse>

    @GET("api/get/pastpayments")
    fun getPastRepayment(
        @Header("Authorization") token:String?,
        @Query("loan_id") loan_id: String?,
        @Query("request_id") request_id: String?,
        @Query("action_id") action_id: String?
    ):Call<LoanRepaymentResponse>

    @FormUrlEncoded
    @POST("api/momo/loanpayment")
    fun makeLoanPayment(
        @Header("Authorization") token:String?,
        @Field("amount") amount: Double?,
        @Field("mobile_number") mobile_number: String?,
        @Field("pin") pin: String?,
        @Field("currency_code") currency_code: String?,
        @Field("loan_id") loan_id: String?,
        @Field("request_id") request_id: String?,
        @Field("action_id") action_id: String?
    ):Call<LoanRepaymentResponse>
}