package com.kabbodev.emaishapay.ui.fragments.register.signup

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.kabbodev.emaishapay.R
import com.kabbodev.emaishapay.constants.Constants
import com.kabbodev.emaishapay.data.config.Config
import com.kabbodev.emaishapay.data.enums.CreatePinType
import com.kabbodev.emaishapay.data.enums.EnterPinType
import com.kabbodev.emaishapay.data.models.AuthenticationResponse
import com.kabbodev.emaishapay.data.preferences.UserPreferences
import com.kabbodev.emaishapay.databinding.FragmentCreatePinBinding
import com.kabbodev.emaishapay.network.ApiClient
import com.kabbodev.emaishapay.network.ApiRequests
import com.kabbodev.emaishapay.ui.base.BaseFragment
import com.kabbodev.emaishapay.ui.fragments.register.RegisterFragment
import com.kabbodev.emaishapay.ui.viewModels.LoginViewModel
import com.kabbodev.emaishapay.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@AndroidEntryPoint
class CreatePinFragment : BaseFragment<FragmentCreatePinBinding>() {

    private val TAG = "CreatePinFragment"
    private val mViewModel: LoginViewModel by activityViewModels()
    private val apiRequests: ApiRequests? by lazy { ApiClient.getLoanInstance() }
    private var dialogLoader: DialogLoader? = null
    private var loginType: CreatePinType = CreatePinType.SIGNUP



    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentCreatePinBinding.inflate(inflater, container, false)

    override fun setupTheme() {
        loginType = requireArguments().getSerializable(Config.CREATE_PIN_TYPE) as CreatePinType
        binding.isForgotPin = loginType == CreatePinType.FORGOT_PIN
        binding.etPin.editText?.transformationMethod = AsteriskPasswordTransformationMethod()
        binding.etConfirmPin.editText?.transformationMethod = AsteriskPasswordTransformationMethod()
        binding.etPin.addEndIconClickListener()
        binding.etConfirmPin.addEndIconClickListener()
    }

    override fun setupClickListeners() {
        binding.toolbarLayout.backBtn.setOnClickListener { requireActivity().onBackPressed() }
        binding.tvRememberMe.setOnClickListener { onRememberMeClick() }
        binding.confirmPinBtn.setOnClickListener { checkInputs() }
        binding.signInTv.setOnClickListener {
            navController.navigateUsingPopUp(R.id.registerFragment, R.id.action_global_registerFragment, bundleOf(RegisterFragment.KEY_SHOW_LOGIN_FIRST to true))
        }
    }

    private fun onRememberMeClick() {
        val tag = binding.tvRememberMe.tag.toString()
        if (tag == "remembered") {
            binding.tvRememberMe.tag = "not_remembered"
            binding.tvRememberMe.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check, 0, 0, 0)
        } else {
            binding.tvRememberMe.tag = "remembered"
            binding.tvRememberMe.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_filled, 0, 0, 0)
        }
    }

    private fun checkInputs() {
        dialogLoader = context?.let { DialogLoader(it) }

        val pin = binding.etPin.editText?.text.toString().trim()
        val confirmPin = binding.etConfirmPin.editText?.text.toString().trim()
        val rememberMeValue = binding.tvRememberMe.tag
        var error: String? = null

        if (pin != confirmPin) error = getString(R.string.confirm_pin_not_matches)
        if (pin.length != 4 || confirmPin.length != 4) error = getString(R.string.invalid_pin)
        if (confirmPin.isEmpty()) error = getString(R.string.confirm_pin_cannot_be_empty)
        if (pin.isEmpty()) error = getString(R.string.pin_cannot_be_empty)

        if (!error.isNullOrEmpty()) {
            binding.root.snackbar(error)
            return
        }

        when (loginType) {
            CreatePinType.SIGNUP -> {

                /*************Retrofit call to verify otp******************************/
                dialogLoader?.showProgressDialog()
                var call: Call<AuthenticationResponse>? = apiRequests?.verifyRegistration(
                    mViewModel.getOtp(),
                    getString(R.string.phone_code) + mViewModel.getPhoneNumber(),
                    "verifyUserRegistration",
                    generateRequestId(),
                    Constants.PREPIN + pin

                )
                call!!.enqueue(object : Callback<AuthenticationResponse> {
                    override fun onResponse(
                        call: Call<AuthenticationResponse>,
                        response: Response<AuthenticationResponse>
                    ) {
                        if (response.isSuccessful) {
                            dialogLoader?.hideProgressDialog()
                            if (response.body()!!.status == 1) {
                                Constants.ACCESS_TOKEN = response.body()!!.access_token.toString()

                                /***************save user details and login user***************/
                                lifecycleScope.launch {
                                    response.body()?.let {
                                        userPreferences.saveUserData(
                                            it!!.data,
                                            it!!.access_token,
                                            pin,
                                            true
                                        )
                                    }
                                }

                                /**********navigate to home fragment**************/
                                navController.navigateUsingPopUp(
                                    R.id.welcomeFragment,
                                    R.id.action_global_homeFragment
                                )
                            } else {
                                response.body()!!.message?.let { binding.root.snackbar(it) }
                            }

                        } else {
                            response.body()!!.message?.let { binding.root.snackbar(it) }
                        }

                    }

                    override fun onFailure(call: Call<AuthenticationResponse>, t: Throwable) {
                        t.message?.let { binding.root.snackbar(it) }
                        dialogLoader?.hideProgressDialog()

                    }
                })


            }
            CreatePinType.FORGOT_PIN -> {
                /***************save new user data and navigate to home***************/
                dialogLoader?.showProgressDialog()
                var call: Call<AuthenticationResponse>? = apiRequests?.forgotPassword(
                    mViewModel.getOtp(),
                    Constants.PREPIN + pin,
                    generateRequestId(),
                    "verifyUserRegistration"

                )
                call!!.enqueue(object : Callback<AuthenticationResponse> {
                    override fun onResponse(
                        call: Call<AuthenticationResponse>,
                        response: Response<AuthenticationResponse>
                    ) {
                        if (response.isSuccessful) {
                            dialogLoader?.hideProgressDialog()
                            if (response.body()!!.status == 1) {
                                Constants.ACCESS_TOKEN = response.body()!!.access_token.toString()

                                /***************save user details and login user***************/
                                lifecycleScope.launch {
                                    response.body()?.let {
                                        userPreferences.saveUserData(
                                            it!!.data,
                                            it!!.access_token,
                                            pin,
                                            true
                                        )
                                    }
                                }

                                /**********navigate to home fragment**************/
                                navController.navigateUsingPopUp(
                                    R.id.welcomeFragment,
                                    R.id.action_global_homeFragment
                                )
                            } else {
                                response.body()!!.message?.let { binding.root.snackbar(it) }
                            }

                        } else {
                            response.body()!!.message?.let { binding.root.snackbar(it) }
                        }

                    }

                    override fun onFailure(call: Call<AuthenticationResponse>, t: Throwable) {
                        t.message?.let { binding.root.snackbar(it) }
                        dialogLoader?.hideProgressDialog()

                    }
                })

            }
        }
    }


}

