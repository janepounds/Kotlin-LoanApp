package com.cabral.emaishapay.ui.fragments.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.cabral.emaishapay.R
import com.cabral.emaishapay.constants.Constants
import com.cabral.emaishapay.data.models.Loan
import com.cabral.emaishapay.data.models.Transaction
import com.cabral.emaishapay.data.models.responses.LoanRepaymentResponse
import com.cabral.emaishapay.databinding.FragmentLoanDetailsBinding
import com.cabral.emaishapay.network.ApiClient
import com.cabral.emaishapay.network.ApiRequests
import com.cabral.emaishapay.singleton.MyApplication
import com.cabral.emaishapay.ui.adapters.TransactionAdapter
import com.cabral.emaishapay.ui.base.BaseFragment
import com.cabral.emaishapay.ui.viewModels.LoanViewModel
import com.cabral.emaishapay.utils.DialogLoader
import com.cabral.emaishapay.utils.generateRequestId
import com.cabral.emaishapay.utils.snackbar
import com.cabral.emaishapay.utils.startAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@AndroidEntryPoint
class LoanDetailsFragment : BaseFragment<FragmentLoanDetailsBinding>() {

    private  val TAG = "LoanDetailsFragment"
    private val mViewModel: LoanViewModel by activityViewModels()
    private lateinit var loan: Loan
    private var weeklyPay:Long? = null
    private val apiRequests: ApiRequests? by lazy { ApiClient.getLoanInstance() }
    private var dialogLoader: DialogLoader? = null


    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentLoanDetailsBinding.inflate(inflater, container, false)

    override fun setupTheme() {
        loan = mViewModel.getLoan()!!
        binding.valueLoanId.text = String.format(getString(R.string.loan_id), loan.loanId)
        binding.valueRemaining.text = String.format(getString(R.string.wallet_balance_value), loan.loanDueAmount)

        getLastRepayment()
        binding.tvPaidToAmt.text = String.format(getString(R.string.wallet_balance_value), loan.amt)
        calculateWeeklyPayments(loan.amt,loan.durationType,loan.duration)


        binding.valueLoanAmt.text = String.format(getString(R.string.wallet_balance_value), MyApplication.getNumberFormattedString(loan.loanDueAmount))
        binding.valueLoanPeriod.text = String.format(getString(R.string.loan_duration)+" "+loan.durationType, loan.duration)
        binding.valueInterestRate.text = String.format(getString(R.string.interest_rate_value), loan.interestRate)
        binding.valueTimeLeft.text = String.format(getString(R.string.loan_duration)+" "+loan.durationType, loan.duration)

    }

    private fun calculateWeeklyPayments(amt: Long, durationType: String, duration: Int) {
        if(durationType.equals("months",ignoreCase = true)){
            weeklyPay = amt /(4 * duration)

            binding.tvWeeklyPaymentValue.text = String.format(getString(R.string.wallet_balance_value), weeklyPay)

        }else if(durationType.equals("weeks",ignoreCase = true)){
            weeklyPay = amt / duration
            binding.tvWeeklyPaymentValue.text = String.format(getString(R.string.wallet_balance_value), weeklyPay)
        }else{
            binding.tvWeeklyPayment.text = getString(R.string.daily_payments)
            weeklyPay = amt/duration
        }

    }

    private fun getLastRepayment(){

    }
    override fun setupClickListeners() {
        binding.backBtn.setOnClickListener { requireActivity().onBackPressed() }
        binding.partialBtn.setOnClickListener { navController.navigate(R.id.action_loanDetailsFragment_to_makePaymentsFragment) }
        binding.fullPaymentBtn.setOnClickListener { navController.navigate(R.id.action_loanDetailsFragment_to_makePaymentsFragment) }
    }

}