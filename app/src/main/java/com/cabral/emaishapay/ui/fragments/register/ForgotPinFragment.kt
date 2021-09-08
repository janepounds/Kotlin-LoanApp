package com.cabral.emaishapay.ui.fragments.register


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import com.cabral.emaishapay.R
import com.cabral.emaishapay.constants.Constants
import com.cabral.emaishapay.data.config.Config
import com.cabral.emaishapay.data.enums.CreatePinType
import com.cabral.emaishapay.databinding.FragmentForgotPinBinding
import com.cabral.emaishapay.ui.base.BaseFragment
import com.cabral.emaishapay.ui.viewModels.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForgotPinFragment : BaseFragment<FragmentForgotPinBinding>() {
    private val mViewModel: LoginViewModel by activityViewModels()

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentForgotPinBinding.inflate(inflater, container, false)

    override fun setupTheme() {

    }

    override fun setupClickListeners() {
        mViewModel.setPhoneNumber(Constants.PHONE_NUMBER!!)
        binding.etPhoneLayout.setOnClickListener { getOtp()}

    }
    private fun getOtp(){
        /*******endpoint for get otp********/

        navController.navigate(R.id.action_forgotPinFragment_to_otpVerifyFragment,
            bundleOf(Config.CREATE_PIN_TYPE to CreatePinType.FORGOT_PIN))


    }

}