package com.cabral.emaishapay.ui.fragments.businessInfo.ownerProfile

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.cabral.emaishapay.R
import com.cabral.emaishapay.constants.Constants
import com.cabral.emaishapay.data.models.RegistrationResponse
import com.cabral.emaishapay.data.models.responses.UserResponse
import com.cabral.emaishapay.databinding.FragmentEnterPersonalDetailsBinding
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
class EnterPersonalDetailsFragment : BaseFragment<FragmentEnterPersonalDetailsBinding>() {

    private  val TAG = "EnterPersonalDetailsFra"
    private val mViewModel: LoginViewModel by activityViewModels()
    private val apiRequests: ApiRequests? by lazy { ApiClient.getLoanInstance() }
    private var dialogLoader: DialogLoader? = null

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentEnterPersonalDetailsBinding.inflate(inflater, container, false)


    override fun setupTheme() {
        /*********load personal details from the server*************/
        loadPersonalDetails()
        binding.etDateOfBirth?.editText?.let { addDatePicker(it,context) }
        binding.spinnerGender.initSpinner(this)
        binding.spinnerEducationLevel.initSpinner(this)
        binding.spinnerMaritalStatus.initSpinner(this)

    }

    override fun setupClickListeners() {
        binding.progressLayout.layoutOwnerInfo.backBtn.setOnClickListener { requireActivity().onBackPressed() }
        binding.saveBtn.setOnClickListener { checkInputs(false) }
        binding.saveAndNextBtn.setOnClickListener { checkInputs(true) }
    }

    private fun loadPersonalDetails() {
        dialogLoader = context?.let { DialogLoader(it) }
        dialogLoader?.showProgressDialog()
        var call: Call<UserResponse>? = apiRequests?.getPersonalDetails(
            Constants.ACCESS_TOKEN,
            generateRequestId(),
            "getPersonalDetails"
        )
        call!!.enqueue(object : Callback<UserResponse> {
            override fun onResponse(
                call: Call<UserResponse>,
                response: Response<UserResponse>
            ) {
                if (response.isSuccessful) {
                    dialogLoader?.hideProgressDialog()

                    if (response.body()!!.status == 1) {
                        /************populate all fields in UI*****************/

                        binding.etFullName.editText?.setText(response.body()!!.data.name)
                        binding.etDateOfBirth.editText?.setText(response.body()!!.data.dob)
                        binding.spinnerGender.getSpinnerAdapter<String>().spinnerView.text = response.body()!!.data.gender
                        binding.spinnerEducationLevel.getSpinnerAdapter<String>().spinnerView.text = response.body()!!.data.education_level
                        binding.spinnerMaritalStatus.getSpinnerAdapter<String>().spinnerView.text = response.body()!!.data.marital_status
                        binding.etYearInBusiness.editText?.setText(response.body()!!.data.years_in_business.toString())
                        binding.etNationalId.editText?.setText( response.body()!!.data.nin)
                        response.body()!!.message?.let { binding.root.snackbar(it) }


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

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                t.message?.let { binding.root.snackbar(it) }
                dialogLoader?.hideProgressDialog()


            }
        })


    }

    private fun checkInputs(proceedNext: Boolean) {
        val fullName = binding.etFullName.editText?.text.toString().trim()
        val dateOfBirth = binding.etDateOfBirth.editText?.text.toString().trim()
        val yearsInBusiness = binding.etYearInBusiness.editText?.text.toString().trim()
        val nationalId = binding.etNationalId.editText?.text.toString().trim()
        var gender: String? = null
        var educationLevel: String? = null
        var maritalStatus: String? = null

        var error: String? = null
        if (!binding.spinnerGender.text.equals(getString(R.string.select))) {
            gender = binding.spinnerGender.text.toString().trim()
        } else {
            error = String.format(getString(R.string.select_error), getString(R.string.gender))
        }

        if (!binding.spinnerEducationLevel.text.equals(getString(R.string.select))) {
            educationLevel = binding.spinnerEducationLevel.text.toString().trim()
        } else {
            error =
                String.format(getString(R.string.select_error), getString(R.string.education_level))
        }

        if (!binding.spinnerMaritalStatus.text.equals(getString(R.string.select))) {
            maritalStatus = binding.spinnerMaritalStatus.text.toString().trim()
        } else {
            error =
                String.format(getString(R.string.select_error), getString(R.string.marital_status))
        }

        if (nationalId.isEmpty()) error = String.format(
            getString(R.string.cannot_be_empty_error),
            getString(R.string.national_id)
        )
        if (yearsInBusiness.isEmpty()) error = String.format(
            getString(R.string.cannot_be_empty_error),
            getString(R.string.years_in_business)
        )
        if (dateOfBirth.isEmpty()) error = String.format(
            getString(R.string.cannot_be_empty_error),
            getString(R.string.date_of_birth)
        )
        if (fullName.isEmpty()) error = getString(R.string.full_name_cannot_be_empty)

        if (!error.isNullOrEmpty()) {
            binding.root.snackbar(error)
            return
        }
        if (proceedNext) {
            dialogLoader?.showProgressDialog()
            /***************endpoint for updating personal details*********************/
            var call: Call<RegistrationResponse>? = apiRequests?.postPersonalDetails(
                Constants.ACCESS_TOKEN,
                name = fullName,
                gender = gender,
                dateOfBirth,
                educationLevel,
                maritalStatus,
                years_in_business = yearsInBusiness.toInt(),
                nin = nationalId,
                generateRequestId(),
                "savePersonalDetails"

            )
            call!!.enqueue(object : Callback<RegistrationResponse> {
                override fun onResponse(
                    call: Call<RegistrationResponse>,
                    response: Response<RegistrationResponse>
                ) {
                    if (response.isSuccessful) {
                        dialogLoader?.hideProgressDialog()
                        if (response.body()!!.status == 1) {
                            /************save values dob and nin in the shared preferences*****************/

                            lifecycleScope.launch {
                                userPreferences.savePersonalInfo(
                                    nationalId,
                                    dateOfBirth
                                )
                            }

                            response.body()!!.message?.let { binding.root.snackbar(it) }
                            navController.navigate(R.id.action_enterPersonalDetailsFragment_to_enterContactDetailsFragment)
                        } else {
                            response.body()!!.message?.let { binding.root.snackbar(it) }

                        }

                    } else if(response.code()==401) {
                        lifecycleScope.launch { userPreferences.user?.first()?.let {
                            mViewModel.setPhoneNumber(
                                it.phoneNumber.substring(3 ))
                        } }
                        dialogLoader?.hideProgressDialog()
                        binding.root.snackbar(getString(R.string.session_expired))
                        startAuth(navController)
                    }else{
                        response.body()!!.message?.let { binding.root.snackbar(it) }
                        dialogLoader?.hideProgressDialog()
                    }

                }

                override fun onFailure(call: Call<RegistrationResponse>, t: Throwable) {
                    t.message?.let { binding.root.snackbar(it) }
                    dialogLoader?.hideProgressDialog()

                }
            })

        }

    }
}

