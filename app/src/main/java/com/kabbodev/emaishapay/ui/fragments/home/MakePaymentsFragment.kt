package com.kabbodev.emaishapay.ui.fragments.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import com.kabbodev.emaishapay.R
import com.kabbodev.emaishapay.data.config.Config
import com.kabbodev.emaishapay.data.enums.EnterPinType
import com.kabbodev.emaishapay.data.models.Transaction
import com.kabbodev.emaishapay.databinding.FragmentMakePaymentsBinding
import com.kabbodev.emaishapay.singleton.MyApplication
import com.kabbodev.emaishapay.ui.adapters.TransactionAdapter
import com.kabbodev.emaishapay.ui.base.BaseFragment
import com.kabbodev.emaishapay.ui.viewModels.LoanViewModel
import com.kabbodev.emaishapay.utils.isPhoneNumberValid
import com.kabbodev.emaishapay.utils.snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MakePaymentsFragment : BaseFragment<FragmentMakePaymentsBinding>() {

    private val mViewModel: LoanViewModel by activityViewModels()
    private lateinit var adapter: TransactionAdapter


    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentMakePaymentsBinding.inflate(inflater, container, false)

    override fun setupTheme() {
        binding.tvBalance.text = String.format(getString(R.string.balance_amt), MyApplication.getNumberFormattedString(1500))

        adapter = TransactionAdapter()
        binding.recyclerView.adapter = adapter

        val tempList = ArrayList<Transaction>()
        tempList.add(Transaction(txnId = "41221115215", txnAmt = 3500, txnDate = "1 August 2021"))
        tempList.add(Transaction(txnId = "55555511111", txnAmt = 2100, txnDate = "25 July 2021"))
        tempList.add(Transaction(txnId = "15221515215", txnAmt = 300, txnDate = "21 July 2021"))
        adapter.updateList(tempList)
    }

    override fun setupClickListeners() {
        binding.toolbarLayout.backBtn.setOnClickListener { requireActivity().onBackPressed() }
        binding.payBtn.setOnClickListener { checkInputs() }
        binding.tvViewMore.setOnClickListener { loadMoreTransactions() }
    }

    private fun loadMoreTransactions() {
        val newAddList = ArrayList<Transaction>()
        newAddList.add(Transaction(txnId = "63321515215", txnAmt = 2500, txnDate = "10 July 2021"))
        newAddList.add(Transaction(txnId = "96964015215", txnAmt = 1500, txnDate = "5 July 2021"))
        adapter.addNewItems(newAddList)
    }

    private fun checkInputs() {
        val amount = binding.etAmount.editText.editText?.text.toString()
        val mobileNumber = binding.etMobileNumber.editText.editText?.text.toString()
        var error: String? = mobileNumber.isPhoneNumberValid()

        if (amount.isEmpty()) error = "Amount cannot be empty!"
        if (amount.isNotEmpty() && amount.toDouble() <= 0) error = "Amount cannot be negative or zero!"
        if (error != null) {
            binding.root.snackbar(error)
            return
        }

        payAmount()
    }

    private fun payAmount() {
        navController.navigate(R.id.action_global_enterPinFragment, bundleOf(Config.LOGIN_TYPE to EnterPinType.MAKE_PAYMENT))
    }

}