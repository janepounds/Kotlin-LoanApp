package com.cabral.emaishapay.ui.fragments.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.cabral.emaishapay.R
import com.cabral.emaishapay.data.models.Loan
import com.cabral.emaishapay.databinding.FragmentLoanDetailsBinding
import com.cabral.emaishapay.singleton.MyApplication
import com.cabral.emaishapay.ui.base.BaseFragment
import com.cabral.emaishapay.ui.viewModels.LoanViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoanDetailsFragment : BaseFragment<FragmentLoanDetailsBinding>() {

    private val mViewModel: LoanViewModel by activityViewModels()
    private lateinit var loan: Loan


    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentLoanDetailsBinding.inflate(inflater, container, false)

    override fun setupTheme() {
        loan = mViewModel.getLoan()!!
        binding.valueLoanId.text = String.format(getString(R.string.loan_id), loan.loanId)
        binding.valueRemaining.text = String.format(getString(R.string.wallet_balance_value), loan.amt)

        binding.tvPaidToAmt.text = String.format(getString(R.string.wallet_balance_value), loan.amt)
        binding.tvWeeklyPaymentValue.text = String.format(getString(R.string.wallet_balance_value), MyApplication.getNumberFormattedString(7500))

        binding.valueLoanAmt.text = String.format(getString(R.string.wallet_balance_value), MyApplication.getNumberFormattedString(loan.amt))
        binding.valueLoanPeriod.text = String.format(getString(R.string.weeks), mViewModel.duration)
        binding.valueInterestRate.text = String.format(getString(R.string.interest_rate_value), mViewModel.interestRate)
        binding.valueTimeLeft.text = String.format(getString(R.string.weeks), 3)
    }

    override fun setupClickListeners() {
        binding.backBtn.setOnClickListener { requireActivity().onBackPressed() }
        binding.partialBtn.setOnClickListener { navController.navigate(R.id.action_loanDetailsFragment_to_makePaymentsFragment) }
        binding.fullPaymentBtn.setOnClickListener { navController.navigate(R.id.action_loanDetailsFragment_to_makePaymentsFragment) }
    }

}