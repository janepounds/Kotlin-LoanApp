package com.cabral.emaishapay.ui.fragments.businessInfo.businessProfile

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.cabral.emaishapay.R
import com.cabral.emaishapay.constants.Constants
import com.cabral.emaishapay.data.models.responses.BusinessDetailsResponse
import com.cabral.emaishapay.databinding.FragmentEnterBusinessDetailsBinding
import com.cabral.emaishapay.network.ApiClient
import com.cabral.emaishapay.network.ApiRequests
import com.cabral.emaishapay.ui.base.BaseFragment
import com.cabral.emaishapay.ui.viewModels.LoginViewModel
import com.cabral.emaishapay.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@AndroidEntryPoint
class EnterBusinessDetailsFragment : BaseFragment<FragmentEnterBusinessDetailsBinding>() {
    private  val TAG = "EnterBusinessDetailsFra"

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
                        binding.etBusinessName.editText?.setText(response.body()!!.data!!.business_name)
                        binding.etDateRegistered.editText?.setText(response.body()!!.data!!.reg_date)
                        binding.etRegistrationNo.editText?.setText(response.body()!!.data!!.reg_no)
                        binding.etLocation.editText?.setText(response.body()!!.data!!.location)
                        binding.etContactPerson.editText?.setText(response.body()!!.data!!.contact_person)
                        binding.etPhoneNumber.editText?.setText(response.body()!!.data!!.phone_number.substring(3))
                        binding.etNumberOfEmployees.editText?.setText(response.body()!!.data!!.no_employees)
                        binding.etAvgMonthlyRevenue.editText?.setText(response.body()!!.data!!.avg_monthly_revenue)
                        binding.spinnerBusinessType.getSpinnerAdapter<String>().spinnerView.text = response.body()!!.data!!.business_type
                        binding.spinnerIndustry.getSpinnerAdapter<String>().spinnerView.text = response.body()!!.data!!.industry


                    } else {
                        response.body()!!.message?.let { binding.root.snackbar(it) }
                        dialogLoader?.hideProgressDialog()

                    }

                } else if (response.code() == 401) {
                    /***************redirect to auth*********************/
                    dialogLoader?.hideProgressDialog()
                    lifecycleScope.launch { userPreferences.user?.first()?.let {
                        mViewModel.setPhoneNumber(
                            it.phoneNumber.substring(3 ))
                    } }
                    binding.root.snackbar(getString(R.string.session_expired))
                    startAuth(navController)
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

        if (!binding.spinnerIndustry.text.equals(getString(R.string.select))) {
            industry = binding.spinnerIndustry.text.toString().trim()
        } else {
            error = String.format(getString(R.string.select_error), getString(R.string.industry))
        }

        if (!binding.spinnerBusinessType.text.equals(getString(R.string.select))) {
            businessType = binding.spinnerBusinessType.text.toString().trim()
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
                                Log.d(TAG, "onResponse: date"+userPreferences.businessInfo?.first()?.regDate +"locat"+userPreferences.businessInfo?.first()?.location)
                            }
                            response.body()!!.message?.let { binding.root.snackbar(it) }
                            navController.navigate(R.id.action_enterBusinessDetailsFragment_to_verificationDocumentsFragment)

                        } else {
                            response.body()!!.message?.let { binding.root.snackbar(it) }
                            dialogLoader?.hideProgressDialog()

                        }

                    } else if (response.code() == 401) {
                        /***************redirect to auth*********************/
                        lifecycleScope.launch { userPreferences.user?.first()?.let {
                            mViewModel.setPhoneNumber(
                                it.phoneNumber.substring(3 ))
                        } }
                        dialogLoader?.hideProgressDialog()
                        binding.root.snackbar(getString(R.string.session_expired))
                        startAuth(navController)


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