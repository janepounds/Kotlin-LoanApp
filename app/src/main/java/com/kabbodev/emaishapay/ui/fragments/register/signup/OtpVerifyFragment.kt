package com.kabbodev.emaishapay.ui.fragments.register.signup

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.kabbodev.emaishapay.R
import com.kabbodev.emaishapay.databinding.FragmentOtpVerifyBinding
import com.kabbodev.emaishapay.ui.base.BaseFragment
import com.kabbodev.emaishapay.ui.viewModels.LoginViewModel
import com.kabbodev.emaishapay.utils.isPhoneNumberValid
import com.kabbodev.emaishapay.utils.snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*

@AndroidEntryPoint
class OtpVerifyFragment : BaseFragment<FragmentOtpVerifyBinding>() {

    private val mViewModel: LoginViewModel by activityViewModels()
    private var timeLeft: Int = 20

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentOtpVerifyBinding.inflate(inflater, container, false)

    override fun setupTheme() {
        binding.phoneNumber = mViewModel.getPhoneNumber()
        binding.timeLeft = timeLeft
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                while (timeLeft > 0) updateTimeLeft()
            }
        }
    }

    override fun setupClickListeners() {
        binding.toolbarLayout.backBtn.setOnClickListener { requireActivity().onBackPressed() }
        binding.tvResendCode.setOnClickListener { resendCode() }
        binding.continueBtn.setOnClickListener { checkInputs() }
    }

    private suspend fun updateTimeLeft() {
        delay(1000)
        timeLeft--
        withContext(Dispatchers.Main) {
            binding.timeLeft = timeLeft
        }
    }

    private fun resendCode() {
        if (timeLeft == 0) {
        } else {
            binding.root.snackbar("Please wait. You can resend otp again after $timeLeft seconds.")
        }
    }

    private fun checkInputs() {
        val otp1 = binding.etOtp1.editText?.text.toString().trim()
        val otp2 = binding.etOtp2.editText?.text.toString().trim()
        val otp3 = binding.etOtp3.editText?.text.toString().trim()
        val otp4 = binding.etOtp4.editText?.text.toString().trim()
        var error: String? = null

        if (otp1.isEmpty()) error = getString(R.string.invalid_pin)
        if (otp2.isEmpty()) error = getString(R.string.invalid_pin)
        if (otp3.isEmpty()) error = getString(R.string.invalid_pin)
        if (otp4.isEmpty()) error = getString(R.string.invalid_pin)

        if (!error.isNullOrEmpty()) {
            binding.root.snackbar(error)
            return
        }

        navController.navigate(R.id.action_otpVerifyFragment_to_createPinFragment)
    }

}