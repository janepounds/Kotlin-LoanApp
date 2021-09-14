package com.cabral.emaishapay.ui.fragments.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import com.cabral.emaishapay.R
import com.cabral.emaishapay.data.config.Config
import com.cabral.emaishapay.data.enums.EnterPinType
import com.cabral.emaishapay.databinding.FragmentLoanConfirmationBinding
import com.cabral.emaishapay.singleton.MyApplication
import com.cabral.emaishapay.ui.base.BaseFragment
import com.cabral.emaishapay.ui.viewModels.LoanViewModel
import com.cabral.emaishapay.utils.getPayBackText
import com.cabral.emaishapay.utils.snackbar
import com.cabral.emaishapay.utils.spannedFromHtml
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoanConfirmationFragment : BaseFragment<FragmentLoanConfirmationBinding>() {

    private val mViewModel: LoanViewModel by activityViewModels()


    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentLoanConfirmationBinding.inflate(inflater, container, false)

    override fun setupTheme() {
        val type = when (mViewModel.type) {
            getString(R.string.days) -> getString(R.string.daily)
            getString(R.string.weeks) -> getString(R.string.weekly)
            getString(R.string.months) -> getString(R.string.monthly)
            else -> getString(R.string.weekly)
        }.lowercase()

        binding.valueTotalLoan.text = String.format(getString(R.string.wallet_balance_value), MyApplication.getNumberFormattedString(mViewModel.loanDueAmount))
        binding.tvRepaymentTime.text = String.format(getString(R.string.repayment_time_value), mViewModel.duration, mViewModel.type)
        binding.tvStartingDate.text = String.format(getString(R.string.starting_date), "25 July")
        binding.tvLoanText.text = spannedFromHtml(
            getPayBackText(
                payBackAmount = MyApplication.getNumberFormattedString(mViewModel.loanDueAmount),
                type = type,
                typeAmount = MyApplication.getNumberFormattedString(mViewModel.typePayment),
                interestRate = mViewModel.interestRate
            )
        )
    }

    override fun setupClickListeners() {
        binding.backBtn.setOnClickListener { requireActivity().onBackPressed() }
        binding.confirmBtn.setOnClickListener { checkInputs() }
    }

    private fun checkInputs() {
        val agreementChecked = binding.agreementCheckbox.isChecked
        if (!agreementChecked) {
            binding.root.snackbar("Please accept the loan disclosure, loan agreement and recovery policy.")
            return
        }
        navController.navigate(R.id.action_global_enterPinFragment, bundleOf(Config.LOGIN_TYPE to EnterPinType.NEW_LOAN))
    }

}