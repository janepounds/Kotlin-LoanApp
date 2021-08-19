package com.kabbodev.emaishapay.ui.fragments.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.kabbodev.emaishapay.R
import com.kabbodev.emaishapay.data.enums.LoanStatus
import com.kabbodev.emaishapay.data.models.Loan
import com.kabbodev.emaishapay.databinding.FragmentLoanHistoryBinding
import com.kabbodev.emaishapay.ui.adapters.LoanAdapter
import com.kabbodev.emaishapay.ui.base.BaseFragment
import com.kabbodev.emaishapay.ui.viewModels.LoanViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoanHistoryFragment : BaseFragment<FragmentLoanHistoryBinding>() {

    private val mViewModel: LoanViewModel by activityViewModels()
    private lateinit var adapter: LoanAdapter


    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentLoanHistoryBinding.inflate(inflater, container, false)

    override fun setupTheme() {
        adapter = LoanAdapter {
            mViewModel.setLoan(it)
            navController.navigate(R.id.action_global_loanDetailsFragment)
        }
        binding.adapter = adapter

        val list = ArrayList<Loan>()
        list.add(Loan(loanId = "100161700011", status = LoanStatus.COMPLETED, amt = 760066))
        list.add(Loan(loanId = "100161700012", status = LoanStatus.IN_PROGRESS, amt = 500))
        list.add(Loan(loanId = "100161700013", status = LoanStatus.PAID, amt = 10000))
        list.add(Loan(loanId = "100161700014", status = LoanStatus.REJECTED, amt = 2222))
        list.add(Loan(loanId = "100161700015", status = LoanStatus.COMPLETED, amt = 2525))
        list.add(Loan(loanId = "100161700016", status = LoanStatus.REJECTED, amt = 2525))
        list.add(Loan(loanId = "100161700017", status = LoanStatus.REJECTED, amt = 255252))
        list.add(Loan(loanId = "100161700018", status = LoanStatus.IN_PROGRESS, amt = 31565))
        adapter.updateList(list)
    }

    override fun setupClickListeners() {
    }

}