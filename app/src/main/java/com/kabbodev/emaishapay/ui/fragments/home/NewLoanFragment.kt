package com.kabbodev.emaishapay.ui.fragments.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.kabbodev.emaishapay.R
import com.kabbodev.emaishapay.databinding.FragmentNewLoanBinding
import com.kabbodev.emaishapay.singleton.MyApplication
import com.kabbodev.emaishapay.ui.base.BaseFragment
import com.kabbodev.emaishapay.ui.viewModels.LoanViewModel
import com.kabbodev.emaishapay.utils.calculation.CalculationUtils
import com.kabbodev.emaishapay.utils.initSpinner
import com.kabbodev.emaishapay.utils.snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class NewLoanFragment : BaseFragment<FragmentNewLoanBinding>() {

    private val mViewModel: LoanViewModel by activityViewModels()
    private var interestRate:Double = 0.0
    private var processingFee:Double = 0.0



    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentNewLoanBinding.inflate(inflater, container, false)

    override fun setupTheme() {
        binding.spinnerDuration.initSpinner(this)
        /************get user from shared preferences********************/
        lifecycleScope.launch(Dispatchers.IO) {

            withContext(Dispatchers.Main) {
                context?.let {
                    userPreferences.user?.first().let { user ->

                        interestRate = user!!.interestRate!!.toDouble()
                        processingFee = user!!.processingFee!!.toDouble()
                    }

                }
            }
        }

    }

    override fun setupClickListeners() {
        binding.enterBtn.setOnClickListener { checkInputs() }
        binding.applyBtn.setOnClickListener { applyLoan() }
    }

    private fun checkInputs() {
        val durationTypeArray: List<String> = listOf(*resources.getStringArray(R.array.duration))

        val amount = binding.amtEditText.text.toString().trim()
        val duration = binding.durationEditText.text.toString().trim()
        var durationType: String? = null
        var error: String? = null

        if (binding.spinnerDuration.selectedIndex >= 0) {
            durationType = durationTypeArray[binding.spinnerDuration.selectedIndex]
        } else {
            error = String.format(getString(R.string.select_error), getString(R.string.duration_type))
        }
        if (duration.isEmpty()) error = String.format(getString(R.string.cannot_be_empty_error), getString(R.string.duration))
        if (amount.isEmpty()) error = String.format(getString(R.string.cannot_be_empty_error), getString(R.string.amount))

        if (!error.isNullOrEmpty()) {
            binding.root.snackbar(error)
            return
        }

        val loanDueAmount = CalculationUtils.calculateLoanDueAmount(amount.toLong(), interestRate.toInt(), processingFee.toLong())
        calculateInputs(amount.toLong(), loanDueAmount, interestRate.toInt(), processingFee.toLong(), duration.toInt(), durationType!!)
    }

    private fun calculateInputs(loanAmount: Long, loanDueAmount: Long, interestRate: Int, processingFee: Long, duration: Int, durationType: String) {
        val payment: Long = loanDueAmount / duration
        val type = when (durationType) {
            "DAYS" -> getString(R.string.days)
            "WKS" -> getString(R.string.weeks)
            "MTHS" -> getString(R.string.months)
            else -> getString(R.string.weeks)
        }
        binding.valueDueAmt.text = String.format(getString(R.string.wallet_balance_value), MyApplication.getNumberFormattedString(loanDueAmount))
        binding.valueInterestRate.text = String.format(getString(R.string.interest_rate_value), interestRate)
        binding.valueProcessingFee.text = String.format(getString(R.string.wallet_balance_value), MyApplication.getNumberFormattedString(processingFee))
        binding.valueDuration.text = String.format(getString(R.string.duration_with_type_value), duration, type)
        binding.valuePayment.text = String.format(getString(R.string.ugx_with_duration), MyApplication.getNumberFormattedString(payment), type.lowercase().substring(0, type.length - 1))

        mViewModel.setLoanData(loanAmount, loanDueAmount, duration, type, payment,interestRate,processingFee)
    }

    private fun applyLoan() {
        if (binding.valueDueAmt.text.toString().isEmpty()) {
            binding.root.snackbar("Please click on enter and then you can apply for loan!")
            return
        }
        navController.navigate(R.id.action_global_loanConfirmationFragment)
    }

}