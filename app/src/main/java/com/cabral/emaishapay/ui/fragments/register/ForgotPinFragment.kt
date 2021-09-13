package com.cabral.emaishapay.ui.fragments.register


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.cabral.emaishapay.R
import com.cabral.emaishapay.constants.Constants
import com.cabral.emaishapay.data.config.Config
import com.cabral.emaishapay.data.enums.CreatePinType
import com.cabral.emaishapay.data.models.AuthenticationResponse
import com.cabral.emaishapay.databinding.FragmentForgotPinBinding
import com.cabral.emaishapay.network.ApiClient
import com.cabral.emaishapay.network.ApiRequests
import com.cabral.emaishapay.ui.base.BaseFragment
import com.cabral.emaishapay.ui.viewModels.LoginViewModel
import com.cabral.emaishapay.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@AndroidEntryPoint
class ForgotPinFragment : BaseFragment<FragmentForgotPinBinding>() {
    private val mViewModel: LoginViewModel by activityViewModels()
    private val apiRequests: ApiRequests? by lazy { ApiClient.getLoanInstance() }
    private var dialogLoader: DialogLoader? = null

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentForgotPinBinding.inflate(inflater, container, false)

    override fun setupTheme() {

    }

    override fun setupClickListeners() {
        mViewModel.setPhoneNumber(Constants.PHONE_NUMBER!!)
        binding.etPhoneLayout.setOnClickListener { getOtp()}
        binding.toolbarLayout.backBtn.setOnClickListener { requireActivity().onBackPressed() }

    }
    private fun getOtp(){
        /*******endpoint for get otp********/
        dialogLoader?.showProgressDialog()
        var call: Call<AuthenticationResponse>? = apiRequests?.forgotPassword(
            getString(R.string.phone_code)+mViewModel.getPhoneNumber(),
            generateRequestId(),
            "initiateForgotPin",

        )
        call!!.enqueue(object : Callback<AuthenticationResponse> {
            override fun onResponse(
                call: Call<AuthenticationResponse>,
                response: Response<AuthenticationResponse>
            ) {
                if (response.isSuccessful) {
                    dialogLoader?.hideProgressDialog()
                    if (response.body()!!.status == 1) {
                        navController.navigate(R.id.action_forgotPinFragment_to_otpVerifyFragment,
                            bundleOf(Config.CREATE_PIN_TYPE to CreatePinType.FORGOT_PIN))

                    } else {
                        response.body()!!.message?.let { binding.root.snackbar(it) }
                    }

                }else if(response.code()==401){
                    dialogLoader?.hideProgressDialog()
                    binding.root.snackbar(getString(R.string.session_expired))
                    startAuth(navController)

                } else
                {  if(response.body()!!.message?.isNotEmpty()) {
                    response.body()!!.message?.let { binding.root.snackbar(it) }
                    dialogLoader?.hideProgressDialog()
                 }
                }

            }

            override fun onFailure(call: Call<AuthenticationResponse>, t: Throwable) {

                t.message?.let { binding.root.snackbar(it) }
                dialogLoader?.hideProgressDialog()

            }
        })


    }

}