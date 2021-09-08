package com.cabral.emaishapay.ui.fragments.register.signup

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.cabral.emaishapay.R
import com.cabral.emaishapay.data.models.RegistrationResponse
import com.cabral.emaishapay.databinding.FragmentSignUpBinding
import com.cabral.emaishapay.network.ApiClient
import com.cabral.emaishapay.network.ApiRequests
import com.cabral.emaishapay.ui.base.BaseFragment
import com.cabral.emaishapay.ui.fragments.register.RegisterFragment
import com.cabral.emaishapay.ui.viewModels.LoginViewModel
import com.cabral.emaishapay.utils.DialogLoader
import com.cabral.emaishapay.utils.generateRequestId
import com.cabral.emaishapay.utils.isPhoneNumberValid
import com.cabral.emaishapay.utils.snackbar
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@AndroidEntryPoint
class SignUpFragment : BaseFragment<FragmentSignUpBinding>() {

    private val mViewModel: LoginViewModel by activityViewModels()
//    private var apiRequests: ApiRequests = getIn

    private val apiRequests: ApiRequests? by lazy { ApiClient.getLoanInstance() }
    private var dialogLoader: DialogLoader? = null

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentSignUpBinding.inflate(
            inflater,
            container,
            false
        )

    override fun setupTheme() {

    }

    override fun setupClickListeners() {
        binding.createAccountBtn.setOnClickListener { checkInputs() }
        binding.signInTv.setOnClickListener { RegisterFragment.updateCurrentItem(0, true) }
    }

    private fun checkInputs() {
        dialogLoader = context?.let { DialogLoader(it) }
        dialogLoader?.showProgressDialog()
        val fullName = binding.etFullName.editText?.text.toString().trim()
        val emailAddress = binding.etEmailAddress.editText?.text.toString().trim()
        val phoneNumber = binding.etPhoneNumber.editText?.text.toString().trim()
        var error: String? = phoneNumber.isPhoneNumberValid()

        if (!binding.termsAndConditionCheckbox.isChecked) error =
            getString(R.string.tac_not_accepted)
        if (!Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()) error =
            getString(R.string.invalid_email)
        if (emailAddress.isEmpty()) error = getString(R.string.email_address_cannot_be_empty)
        if (fullName.isEmpty()) error = getString(R.string.full_name_cannot_be_empty)

        if (!error.isNullOrEmpty()) {
            binding.root.snackbar(error)
            return
        }
        mViewModel.setPhoneNumber(phoneNumber)


        /*****************Retrofit call for sending otp******************************/
        var call: Call<RegistrationResponse>? = apiRequests?.signUp(
            generateRequestId(),
            "registerUser",
            fullName,
            getString(R.string.phone_code)+ phoneNumber,
            emailAddress
        )
        call!!.enqueue(object : Callback<RegistrationResponse> {
            override fun onResponse(
                call: Call<RegistrationResponse>,
                response: Response<RegistrationResponse>
            ) {
                if (response.isSuccessful) {
                    dialogLoader?.hideProgressDialog()
                    if (response.body()!!.status == 1) {
                        arguments = Bundle().apply {
                            putString("phone",getString(R.string.phone_code)+phoneNumber)
                        }

                        /**********navigate to otp fragment**************/
                        navController.navigate(R.id.action_registerFragment_to_otpVerifyFragment,arguments)
                    }else{
                        response.body()!!.message?.let { binding.root.snackbar(it) }
                    }

                } else {
                    response.body()!!.message?.let { binding.root.snackbar(it) }
                }

            }

            override fun onFailure(call: Call<RegistrationResponse>, t: Throwable) {
                t.message?.let { binding.root.snackbar(it) }
                dialogLoader?.hideProgressDialog()

            }
        })


    }
}





