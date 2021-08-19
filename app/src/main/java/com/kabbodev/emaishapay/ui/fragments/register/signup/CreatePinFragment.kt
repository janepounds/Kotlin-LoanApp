package com.kabbodev.emaishapay.ui.fragments.register.signup

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.kabbodev.emaishapay.R
import com.kabbodev.emaishapay.databinding.FragmentCreatePinBinding
import com.kabbodev.emaishapay.ui.base.BaseFragment
import com.kabbodev.emaishapay.ui.fragments.register.RegisterFragment
import com.kabbodev.emaishapay.ui.viewModels.LoginViewModel
import com.kabbodev.emaishapay.utils.AsteriskPasswordTransformationMethod
import com.kabbodev.emaishapay.utils.addEndIconClickListener
import com.kabbodev.emaishapay.utils.navigateUsingPopUp
import com.kabbodev.emaishapay.utils.snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CreatePinFragment : BaseFragment<FragmentCreatePinBinding>() {

    private val mViewModel: LoginViewModel by activityViewModels()


    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentCreatePinBinding.inflate(inflater, container, false)

    override fun setupTheme() {
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
        lifecycleScope.launch { userPreferences.saveIsLoggedIn(true) }
        navController.navigateUsingPopUp(R.id.welcomeFragment, R.id.action_global_homeFragment)
    }

}