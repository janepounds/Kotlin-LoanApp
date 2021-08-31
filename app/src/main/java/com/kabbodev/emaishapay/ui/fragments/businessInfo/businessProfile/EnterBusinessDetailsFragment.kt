package com.kabbodev.emaishapay.ui.fragments.businessInfo.businessProfile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.kabbodev.emaishapay.R
import com.kabbodev.emaishapay.constants.Constants
import com.kabbodev.emaishapay.data.models.responses.BusinessDetailsResponse
import com.kabbodev.emaishapay.data.models.responses.IdDocumentResponse
import com.kabbodev.emaishapay.databinding.FragmentEnterBusinessDetailsBinding
import com.kabbodev.emaishapay.network.ApiClient
import com.kabbodev.emaishapay.network.ApiRequests
import com.kabbodev.emaishapay.ui.base.BaseFragment
import com.kabbodev.emaishapay.ui.viewModels.LoginViewModel
import com.kabbodev.emaishapay.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@AndroidEntryPoint
class EnterBusinessDetailsFragment : BaseFragment<FragmentEnterBusinessDetailsBinding>() {

    private val mViewModel: LoginViewModel by activityViewModels()
    private val apiRequests: ApiRequests? by lazy { ApiClient.getLoanInstance() }
    private var dialogLoader: DialogLoader? = null

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentEnterBusinessDetailsBinding.inflate(inflater, container, false)

    override fun setupTheme() {
        loadBusinessDetails()
        binding.etDateRegistered?.editText?.let { addDatePicker(it,context) }
        binding.spinnerBusinessType.initSpinner(this)
        binding.spinnerIndustry.initSpinner(this)
    }

    override fun setupClickListeners() {
        binding.progressLayout.layoutBusinessInfo.backBtn.setOnClickListener { requireActivity().onBackPressed() }
        binding.saveBtn.setOnClickListener { checkInputs(false) }
        binding.saveAndNextBtn.setOnClickListener { checkInputs(true) }
    }

    private fun loadBusinessDetails(){
        dialogLoader = context?.let { DialogLoader(it) }
        dialogLoader?.showProgressDialog()
        var call: Call<BusinessDetailsResponse>? = apiRequests?.getBusinessDetails(
            Constants.ACCESS_TOKEN,
            generateRequestId(),
            "getBusinessDetails"
        )
        call!!.enqueue(object : Callback<BusinessDetailsResponse> {
            override fun onResponse(
                call: Call<BusinessDetailsResponse>,
                response: Response<BusinessDetailsResponse>
            ) {
                if (response.isSuccessful) {
                    dialogLoader?.hideProgressDialog()

                    if (response.body()!!.status == 1) {
                        /************populate all fields in UI*****************/



                    } else {
                        response.body()!!.message?.let { binding.root.snackbar(it) }
                        dialogLoader?.hideProgressDialog()

                    }

                } else if (response.code() == 401) {
                    /***************redirect to auth*********************/
                    response.body()!!.message?.let { binding.root.snackbar(it)}
                    dialogLoader?.hideProgressDialog()
//                    EnterPinFragment.startAuth(true)


                } else {
                    response.body()!!.message?.let { binding.root.snackbar(it) }
                    dialogLoader?.hideProgressDialog()
                }

            }

            override fun onFailure(call: Call<BusinessDetailsResponse>, t: Throwable) {
                t.message?.let { binding.root.snackbar(it) }
                dialogLoader?.hideProgressDialog()


            }
        })


    }

    private fun checkInputs(proceedNext: Boolean) {

        val businessTypes: List<String> = listOf(*resources.getStringArray(R.array.business_type))
        val industryArray: List<String> = listOf(*resources.getStringArray(R.array.industry))

        val businessName = binding.etBusinessName.editText?.text.toString().trim()
        val dateRegistered = binding.etDateRegistered.editText?.text.toString().trim()
        val registrationNo = binding.etRegistrationNo.editText?.text.toString().trim()
        val location = binding.etLocation.editText?.text.toString().trim()
        val contactPerson = binding.etContactPerson.editText?.text.toString().trim()
        val phoneNumber = binding.etPhoneNumber.editText?.text.toString().trim()
        val numberOfEmployees = binding.etNumberOfEmployees.editText?.text.toString().trim()
        val avgMonthlyRevenue = binding.etAvgMonthlyRevenue.editText?.text.toString().trim()

        var industry: String? = null
        var businessType: String? = null
        var error: String? = phoneNumber.isPhoneNumberValid()

        if (binding.spinnerIndustry.selectedIndex >= 0) {
            industry = industryArray[binding.spinnerIndustry.selectedIndex]
        } else {
            error = String.format(getString(R.string.select_error), getString(R.string.industry))
        }

        if (binding.spinnerBusinessType.selectedIndex >= 0) {
            businessType = businessTypes[binding.spinnerBusinessType.selectedIndex]
        } else {
            error = String.format(getString(R.string.select_error), getString(R.string.business_type))
        }

        if (avgMonthlyRevenue.isEmpty()) error = String.format(getString(R.string.cannot_be_empty_error), getString(R.string.avg_monthly_revenue))
        if (numberOfEmployees.isEmpty()) error = String.format(getString(R.string.cannot_be_empty_error), getString(R.string.number_of_employees))
        if (contactPerson.isEmpty()) error = String.format(getString(R.string.cannot_be_empty_error), getString(R.string.contact_person))
        if (location.isEmpty()) error = String.format(getString(R.string.cannot_be_empty_error), getString(R.string.location))
        if (registrationNo.isEmpty()) error = String.format(getString(R.string.cannot_be_empty_error), getString(R.string.registration_no))
        if (dateRegistered.isEmpty()) error = String.format(getString(R.string.cannot_be_empty_error), getString(R.string.date_of_birth))
        if (businessName.isEmpty()) error = String.format(getString(R.string.cannot_be_empty_error), getString(R.string.business_name))

        if (!error.isNullOrEmpty()) {
            binding.root.snackbar(error)
            return
        }
        if (proceedNext) {
            dialogLoader?.showProgressDialog()
            var call: Call<BusinessDetailsResponse>? = apiRequests?.postBusinessDetails(
                Constants.ACCESS_TOKEN,
                businessName,businessType,registrationNo,dateRegistered,industry,location,contactPerson,getString(R.string.phone_code)+phoneNumber,
                numberOfEmployees,avgMonthlyRevenue.toDouble(),
                generateRequestId(),
                "saveBusinessDetails"
            )
            call!!.enqueue(object : Callback<BusinessDetailsResponse> {
                override fun onResponse(
                    call: Call<BusinessDetailsResponse>,
                    response: Response<BusinessDetailsResponse>
                ) {
                    if (response.isSuccessful) {
                        dialogLoader?.hideProgressDialog()

                        if (response.body()!!.status == 1) {
                            /************save values reg and location in the shared preferences*****************/
                            lifecycleScope.launch {
                                userPreferences.saveBusinessInfo(
                                    dateRegistered,
                                    location
                                )
                            }
                            navController.navigate(R.id.action_enterBusinessDetailsFragment_to_verificationDocumentsFragment)

                        } else {
                            response.body()!!.message?.let { binding.root.snackbar(it) }
                            dialogLoader?.hideProgressDialog()

                        }

                    } else if (response.code() == 401) {
                        /***************redirect to auth*********************/
                        response.body()!!.message?.let { binding.root.snackbar(it)}
                        dialogLoader?.hideProgressDialog()
//                    EnterPinFragment.startAuth(true)


                    } else {
                        response.body()!!.message?.let { binding.root.snackbar(it) }
                        dialogLoader?.hideProgressDialog()
                    }

                }

                override fun onFailure(call: Call<BusinessDetailsResponse>, t: Throwable) {
                    t.message?.let { binding.root.snackbar(it) }
                    dialogLoader?.hideProgressDialog()


                }
            })


        }
    }


}