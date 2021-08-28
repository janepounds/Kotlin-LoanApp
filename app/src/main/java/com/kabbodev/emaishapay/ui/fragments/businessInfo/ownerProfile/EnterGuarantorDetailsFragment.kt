package com.kabbodev.emaishapay.ui.fragments.businessInfo.ownerProfile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.kabbodev.emaishapay.R
import com.kabbodev.emaishapay.data.models.RegistrationResponse
import com.kabbodev.emaishapay.data.models.responses.GuarantorResponse
import com.kabbodev.emaishapay.data.models.responses.UserResponse
import com.kabbodev.emaishapay.databinding.FragmentEnterGuarantorDetailsBinding
import com.kabbodev.emaishapay.network.ApiClient
import com.kabbodev.emaishapay.network.ApiRequests
import com.kabbodev.emaishapay.ui.base.BaseFragment
import com.kabbodev.emaishapay.ui.viewModels.LoginViewModel
import com.kabbodev.emaishapay.utils.DialogLoader
import com.kabbodev.emaishapay.utils.generateRequestId
import com.kabbodev.emaishapay.utils.initSpinner
import com.kabbodev.emaishapay.utils.snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@AndroidEntryPoint
class EnterGuarantorDetailsFragment : BaseFragment<FragmentEnterGuarantorDetailsBinding>() {

    private val mViewModel: LoginViewModel by activityViewModels()
    private val apiRequests: ApiRequests? by lazy { ApiClient.getLoanInstance() }
    private var dialogLoader: DialogLoader? = null
    var token: String? = ""

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentEnterGuarantorDetailsBinding.inflate(inflater, container, false)

    override fun setupTheme() {
        loadGuarantorDetails()
        binding.firstKin.spinnerGender.initSpinner(this)
        binding.firstKin.spinnerRelationship.initSpinner(this)
        binding.secondKin.spinnerGender.initSpinner(this)
        binding.secondKin.spinnerRelationship.initSpinner(this)
    }

    override fun setupClickListeners() {
        binding.progressLayout.layoutOwnerInfo.backBtn.setOnClickListener { requireActivity().onBackPressed() }
        binding.saveBtn.setOnClickListener { checkInputs(false) }
        binding.saveAndNextBtn.setOnClickListener { checkInputs(true) }
    }

    private fun loadGuarantorDetails(){
        GlobalScope.launch { userPreferences.user!!.collect { token = it.accessToken.toString() } }
        var call: Call<GuarantorResponse>? = apiRequests?.getGuarantorDetails(
            token,
            generateRequestId(),
            "getPersonalDetails"
        )
        call!!.enqueue(object : Callback<GuarantorResponse> {
            override fun onResponse(
                call: Call<GuarantorResponse>,
                response: Response<GuarantorResponse>
            ) {
                if (response.isSuccessful) {

                    if (response.body()!!.status == 1) {
                        /************populate all fields in UI*****************/
                        binding.firstKin.etFullName.editText?.text ?: response.body()!!.data!!.name
                        binding.firstKin.spinnerGender.text ?: response.body()!!.data!!.gender
                        binding.firstKin.spinnerRelationship.text ?: response.body()!!.data!!.relationship
                        binding.firstKin.etMobileNumber.etPhoneNumber.editText?.text ?: response.body()!!.data!!.mobile
                        binding.firstKin.etResidentialAddress.editText?.text ?: response.body()!!.data!!.residential_address
                        binding.secondKin.etFullName.editText?.text ?: response.body()!!.data!!.name2
                        binding.secondKin.spinnerGender.text ?: response.body()!!.data!!.gender2
                        binding.secondKin.spinnerRelationship.text ?: response.body()!!.data!!.relationship2
                        binding.secondKin.etMobileNumber.etPhoneNumber.editText?.text ?: response.body()!!.data!!.mobile2
                        binding.secondKin.etResidentialAddress.editText?.text ?: response.body()!!.data!!.residential_address2

                    } else {
                        response.body()!!.message?.let { binding.root.snackbar(it) }

                    }

                } else if (response.code() == 401) {
                    /***************redirect to auth*********************/
                    response.body()!!.message?.let { binding.root.snackbar(it) }


                } else {
                    response.body()!!.message?.let { binding.root.snackbar(it) }
                }

            }

            override fun onFailure(call: Call<GuarantorResponse>, t: Throwable) {
                t.message?.let { binding.root.snackbar(it) }


            }
        })

    }

    private fun checkInputs(proceedNext: Boolean) {
        val genders: List<String> = listOf(*resources.getStringArray(R.array.gender))
        val relationshipArray: List<String> = listOf(*resources.getStringArray(R.array.relationship))
        val maritalStatusArray: List<String> = listOf(*resources.getStringArray(R.array.marital_status))

        val fullName1 = binding.firstKin.etFullName.editText?.text.toString().trim()
        val fullName2 = binding.secondKin.etFullName.editText?.text.toString().trim()
        val phone1 = binding.firstKin.etMobileNumber.etPhoneNumber.editText?.text.toString().trim()
        val phone2 = binding.secondKin.etMobileNumber.etPhoneNumber.editText?.text.toString().trim()
        val residential1 = binding.firstKin.etResidentialAddress.editText?.text.toString().trim()
        val residential2 = binding.secondKin.etResidentialAddress.editText?.text.toString().trim()
        var gender1: String? = null
        var gender2: String? = null
        var relationship1: String? = null
        var relationship2: String? = null


        var error: String? = null
        if (binding.firstKin.spinnerGender.selectedIndex >= 0) {
            gender1 = genders[binding.firstKin.spinnerGender.selectedIndex]
        } else {
            error = String.format(getString(R.string.select_error), getString(R.string.gender))
        }
        if (binding.secondKin.spinnerGender.selectedIndex >= 0) {
            gender2 = genders[binding.firstKin.spinnerGender.selectedIndex]
        } else {
            error = String.format(getString(R.string.select_error), getString(R.string.gender))
        }

        if (binding.firstKin.spinnerRelationship.selectedIndex >= 0) {
            relationship1 = relationshipArray[binding.firstKin.spinnerRelationship.selectedIndex]
        } else {
            error = String.format(getString(R.string.select_error), getString(R.string.education_level))
        }
        if (binding.secondKin.spinnerRelationship.selectedIndex >= 0) {
            relationship2 = relationshipArray[binding.firstKin.spinnerRelationship.selectedIndex]
        } else {
            error = String.format(getString(R.string.select_error), getString(R.string.education_level))
        }


        if (fullName1.isEmpty()) error = getString(R.string.full_name_cannot_be_empty)
        if (fullName2.isEmpty()) error = getString(R.string.full_name_cannot_be_empty)
        if (phone1.isEmpty()) error =  getString(R.string.phone_cannot_be_empty)
        if (phone2.isEmpty()) error =  getString(R.string.phone_cannot_be_empty)
        if (residential1.isEmpty()) error =  String.format(
            getString(R.string.cannot_be_empty_error),
            getString(R.string.residential_address)
        )
        if (residential2.isEmpty()) error =  String.format(
            getString(R.string.cannot_be_empty_error),
            getString(R.string.residential_address)
        )

        if (!error.isNullOrEmpty()) {
            binding.root.snackbar(error)
            return
        }
        if (proceedNext){
            dialogLoader?.showProgressDialog()
            /***************endpoint for updating guarantor details*********************/
            var call: Call<GuarantorResponse>? = apiRequests?.postGuarantorDetails(
                token,fullName1,gender1,relationship1,phone1,residential1,fullName2,gender2,relationship2,phone2,residential2,
                "saveGuarantors",
                generateRequestId(),

            )
            call!!.enqueue(object : Callback<GuarantorResponse> {
                override fun onResponse(
                    call: Call<GuarantorResponse>,
                    response: Response<GuarantorResponse>
                ) {
                    if (response.isSuccessful) {
                        dialogLoader?.hideProgressDialog()
                        if (response.body()!!.status == 1) {

                            navController.navigate(R.id.action_enterGuarantorDetailsFragment_to_uploadIdDocumentsFragment)
                        } else {
                            response.body()!!.message?.let { binding.root.snackbar(it) }

                        }

                    } else {
                        response.body()!!.message?.let { binding.root.snackbar(it) }
                        dialogLoader?.hideProgressDialog()
                    }

                }

                override fun onFailure(call: Call<GuarantorResponse>, t: Throwable) {
                    t.message?.let { binding.root.snackbar(it) }
                    dialogLoader?.hideProgressDialog()

                }
            })

        }

    }

}