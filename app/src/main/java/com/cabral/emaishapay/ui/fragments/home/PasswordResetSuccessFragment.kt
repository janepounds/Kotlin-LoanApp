package com.cabral.emaishapay.ui.fragments.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import com.cabral.emaishapay.R
import com.cabral.emaishapay.data.config.Config
import com.cabral.emaishapay.data.enums.CreatePinType
import com.cabral.emaishapay.databinding.FragmentPasswordResetSuccessBinding
import com.cabral.emaishapay.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PasswordResetSuccessFragment : BaseFragment<FragmentPasswordResetSuccessBinding>()  {
    private var loginType: CreatePinType = CreatePinType.FORGOT_PIN

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentPasswordResetSuccessBinding.inflate(inflater, container, false)

    override fun setupTheme() {
        loginType = requireArguments().getSerializable(Config.CREATE_PIN_TYPE) as CreatePinType
        binding.isForgotPin = loginType == CreatePinType.FORGOT_PIN

    }

    override fun setupClickListeners() {
        binding.tvCreatePin.setOnClickListener{navController.navigate(R.id.action_passwordResetSuccessFragment_to_createPinFragment, bundleOf(Config.CREATE_PIN_TYPE to CreatePinType.FORGOT_PIN))}

    }

}