package com.kabbodev.emaishapay.ui.fragments.register.signup

import android.util.Patterns
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.kabbodev.emaishapay.R
import com.kabbodev.emaishapay.databinding.FragmentSignUpBinding
import com.kabbodev.emaishapay.ui.base.BaseFragment
import com.kabbodev.emaishapay.ui.fragments.register.RegisterFragment
import com.kabbodev.emaishapay.ui.viewModels.LoginViewModel
import com.kabbodev.emaishapay.utils.isPhoneNumberValid
import com.kabbodev.emaishapay.utils.snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpFragment : BaseFragment<FragmentSignUpBinding>() {

    private  val mViewModel: LoginViewModel by activityViewModels()


    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentSignUpBinding.inflate(inflater, container, false)

    override fun setupTheme() {

    }

    override fun setupClickListeners() {
        binding.createAccountBtn.setOnClickListener { checkInputs() }
        binding.signInTv.setOnClickListener { RegisterFragment.updateCurrentItem(0, true) }
    }

    private fun checkInputs() {
        val fullName = binding.etFullName.editText?.text.toString().trim()
        val emailAddress = binding.etEmailAddress.editText?.text.toString().trim()
        val phoneNumber = binding.etPhoneNumber.editText?.text.toString().trim()
        var error: String? = phoneNumber.isPhoneNumberValid()

        if (!binding.termsAndConditionCheckbox.isChecked) error = getString(R.string.tac_not_accepted)
        if (!Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()) error = getString(R.string.invalid_email)
        if (emailAddress.isEmpty()) error = getString(R.string.email_address_cannot_be_empty)
        if (fullName.isEmpty()) error = getString(R.string.full_name_cannot_be_empty)

        if (!error.isNullOrEmpty()) {
            binding.root.snackbar(error)
            return
        }
        mViewModel.setPhoneNumber(phoneNumber)
        navController.navigate(R.id.action_registerFragment_to_otpVerifyFragment)
    }

}