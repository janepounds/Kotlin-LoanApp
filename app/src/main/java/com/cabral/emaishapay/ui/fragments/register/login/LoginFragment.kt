package com.cabral.emaishapay.ui.fragments.register.login

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import com.cabral.emaishapay.R
import com.cabral.emaishapay.constants.Constants
import com.cabral.emaishapay.data.config.Config
import com.cabral.emaishapay.data.enums.CreatePinType
import com.cabral.emaishapay.data.enums.EnterPinType
import com.cabral.emaishapay.databinding.FragmentLoginBinding
import com.cabral.emaishapay.ui.base.BaseFragment
import com.cabral.emaishapay.ui.fragments.register.RegisterFragment
import com.cabral.emaishapay.ui.viewModels.LoginViewModel
import com.cabral.emaishapay.utils.hideKeyboard
import com.cabral.emaishapay.utils.isPhoneNumberValid
import com.cabral.emaishapay.utils.snackbar

import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>() {

    private val mViewModel: LoginViewModel by activityViewModels()


    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentLoginBinding.inflate(inflater, container, false)

    override fun setupTheme() {
    }

    override fun setupClickListeners() {
        binding.nextBtn.setOnClickListener { checkInputs() }
        binding.forgetPassTv.setOnClickListener {}
        binding.createAccountTv.setOnClickListener { RegisterFragment.updateCurrentItem(1, true) }
    }

    private fun checkInputs() {
        val phoneNumber = binding.etPhoneNumber.editText?.text.toString().trim()
        val error: String? = phoneNumber.isPhoneNumberValid()

        if (!error.isNullOrEmpty()) {
            binding.root.snackbar(error)
            return
        }
        binding.etPhoneNumber.hideKeyboard()
        mViewModel.setPhoneNumber(phoneNumber)
        Constants.PHONE_NUMBER = phoneNumber
        navController.navigate(R.id.action_global_enterPinFragment, bundleOf(Config.LOGIN_TYPE to EnterPinType.LOGIN))
    }

}