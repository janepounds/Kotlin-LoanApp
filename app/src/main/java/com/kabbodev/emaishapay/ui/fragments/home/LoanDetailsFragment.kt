package com.kabbodev.emaishapay.ui.fragments.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.kabbodev.emaishapay.R
import com.kabbodev.emaishapay.data.models.Loan
import com.kabbodev.emaishapay.databinding.FragmentLoanDetailsBinding
import com.kabbodev.emaishapay.singleton.MyApplication
import com.kabbodev.emaishapay.ui.base.BaseFragment
import com.kabbodev.emaishapay.ui.viewModels.LoanViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoanDetailsFragment : BaseFragment<FragmentLoanDetailsBinding>() {

    private val mViewModel: LoanViewModel by activityViewModels()
    private lateinit var loan: Loan


    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentLoanDetailsBinding.inflate(inflater, container, false)

    override fun setupTheme() {
        loan = mViewModel.getLoan()!!
        binding.valueLoanId.text = String.format(getString(R.string.loan_id), loan.loanId)
        binding.valueRemaining.text = String.format(getString(R.string.wallet_balance_value), MyApplication.getNumberFormattedString(50000))

        binding.tvPaidToAmt.text = String.format(getString(R.string.wallet_balance_value), MyApplication.getNumberFormattedString(25000))
        binding.tvWeeklyPaymentValue.text = String.format(getString(R.string.wallet_balance_value), MyApplication.getNumberFormattedString(7500))

        binding.valueLoanAmt.text = String.format(getString(R.string.wallet_balance_value), MyApplication.getNumberFormattedString(loan.amt))
        binding.valueLoanPeriod.text = String.format(getString(R.string.weeks), 12)
        binding.valueInterestRate.text = String.format(getString(R.string.interest_rate_value), 10)
        binding.valueTimeLeft.text = String.format(getString(R.string.weeks), 3)
    }

    override fun setupClickListeners() {
        binding.backBtn.setOnClickListener { requireActivity().onBackPressed() }
        binding.partialBtn.setOnClickListener { navController.navigate(R.id.action_loanDetailsFragment_to_makePaymentsFragment) }
        binding.fullPaymentBtn.setOnClickListener { navController.navigate(R.id.action_loanDetailsFragment_to_makePaymentsFragment) }
    }

}