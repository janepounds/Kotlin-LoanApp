package com.kabbodev.emaishapay.ui.fragments.businessInfo.ownerProfile

import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.kabbodev.emaishapay.R
import com.kabbodev.emaishapay.constants.Constants
import com.kabbodev.emaishapay.data.models.RegistrationResponse
import com.kabbodev.emaishapay.data.models.User
import com.kabbodev.emaishapay.data.models.responses.UserResponse
import com.kabbodev.emaishapay.databinding.FragmentEnterPersonalDetailsBinding
import com.kabbodev.emaishapay.network.ApiClient
import com.kabbodev.emaishapay.network.ApiRequests
import com.kabbodev.emaishapay.ui.activities.MainActivity
import com.kabbodev.emaishapay.ui.base.BaseFragment
import com.kabbodev.emaishapay.ui.fragments.EnterPinFragment
import com.kabbodev.emaishapay.ui.viewModels.LoginViewModel
import com.kabbodev.emaishapay.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
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
//                        selectSpinnerItemByValue(binding.spinnerGender,response.body()!!.data.gender)
//                        binding.etDateOfBirth.editText?.setText(response.body()!!.data.dob)
//                        binding.spinnerEducationLevel.text ?: response.body()!!.data.education_level
//                        binding.spinnerMaritalStatus.text ?: response.body()!!.data.marital_status

                        binding.etYearInBusiness.editText?.setText(response.body()!!.data.years_in_business.toString())

                        binding.etNationalId.editText?.setText( response.body()!!.data.nin)


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

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                t.message?.let { binding.root.snackbar(it) }
                dialogLoader?.hideProgressDialog()


            }
        })


    }

    private fun checkInputs(proceedNext: Boolean) {
        val genders: List<String> = listOf(*resources.getStringArray(R.array.gender))
        val educationLevelArray: List<String> =
            listOf(*resources.getStringArray(R.array.education_level))
        val maritalStatusArray: List<String> =
            listOf(*resources.getStringArray(R.array.marital_status))

        val fullName = binding.etFullName.editText?.text.toString().trim()
        val dateOfBirth = binding.etDateOfBirth.editText?.text.toString().trim()
        val yearsInBusiness = binding.etYearInBusiness.editText?.text.toString().trim()
        val nationalId = binding.etNationalId.editText?.text.toString().trim()
        var gender: String? = null
        var educationLevel: String? = null
        var maritalStatus: String? = null

        var error: String? = null
        if (binding.spinnerGender.selectedIndex >= 0) {
            gender = genders[binding.spinnerGender.selectedIndex]
        } else {
            error = String.format(getString(R.string.select_error), getString(R.string.gender))
        }

        if (binding.spinnerEducationLevel.selectedIndex >= 0) {
            educationLevel = educationLevelArray[binding.spinnerEducationLevel.selectedIndex]
        } else {
            error =
                String.format(getString(R.string.select_error), getString(R.string.education_level))
        }

        if (binding.spinnerMaritalStatus.selectedIndex >= 0) {
            maritalStatus = maritalStatusArray[binding.spinnerMaritalStatus.selectedIndex]
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
                            navController.navigate(R.id.action_enterPersonalDetailsFragment_to_enterContactDetailsFragment)
                        } else {
                            response.body()!!.message?.let { binding.root.snackbar(it) }

                        }

                    } else if(response.code()==401) {
                        response.body()!!.message?.let { binding.root.snackbar(it) }
                        dialogLoader?.hideProgressDialog()
//                        EnterPinFragment.startAuth(true)
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

