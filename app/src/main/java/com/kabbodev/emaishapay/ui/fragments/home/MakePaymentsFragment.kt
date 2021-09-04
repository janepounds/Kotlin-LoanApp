package com.kabbodev.emaishapay.ui.fragments.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import com.kabbodev.emaishapay.R
import com.kabbodev.emaishapay.constants.Constants
import com.kabbodev.emaishapay.data.config.Config
import com.kabbodev.emaishapay.data.enums.EnterPinType
import com.kabbodev.emaishapay.data.models.Transaction
import com.kabbodev.emaishapay.data.models.Withdraw
import com.kabbodev.emaishapay.data.models.responses.LoanRepaymentResponse
import com.kabbodev.emaishapay.data.models.responses.WithdrawResponse
import com.kabbodev.emaishapay.databinding.FragmentMakePaymentsBinding
import com.kabbodev.emaishapay.network.ApiClient
import com.kabbodev.emaishapay.network.ApiRequests
import com.kabbodev.emaishapay.singleton.MyApplication
import com.kabbodev.emaishapay.ui.adapters.TransactionAdapter
import com.kabbodev.emaishapay.ui.base.BaseFragment
import com.kabbodev.emaishapay.ui.viewModels.LoanViewModel
import com.kabbodev.emaishapay.utils.DialogLoader
import com.kabbodev.emaishapay.utils.generateRequestId
import com.kabbodev.emaishapay.utils.isPhoneNumberValid
import com.kabbodev.emaishapay.utils.snackbar
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@AndroidEntryPoint
class MakePaymentsFragment : BaseFragment<FragmentMakePaymentsBinding>() {

    private val mViewModel: LoanViewModel by activityViewModels()
    private lateinit var adapter: TransactionAdapter
    private val apiRequests: ApiRequests? by lazy { ApiClient.getLoanInstance() }
    private var dialogLoader: DialogLoader? = null


    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentMakePaymentsBinding.inflate(inflater, container, false)

    override fun setupTheme() {
        binding.tvBalance.text = String.format(getString(R.string.balance_amt), MyApplication.getNumberFormattedString(1500))

        adapter = TransactionAdapter()
        binding.recyclerView.adapter = adapter
        getRepayments()
    }
    private fun getRepayments(){
        /**********************Retrofit to call repayments Endpoint *********************/
        var call: Call<LoanRepaymentResponse>? = apiRequests?.getPastRepayment(
            Constants.ACCESS_TOKEN,
            mViewModel.getLoan()!!.loanId,
            generateRequestId(),
            "getPaymentHistory"
        )
        call!!.enqueue(object : Callback<LoanRepaymentResponse> {
            override  fun onResponse(
                call: Call<LoanRepaymentResponse>,
                response: Response<LoanRepaymentResponse>
            ) {
                if (response.isSuccessful) {
                    dialogLoader?.hideProgressDialog()
                    if (response.body()!!.status == 1) {
                        adapter = TransactionAdapter()
                        binding.recyclerView.adapter = adapter
                        val tempList = ArrayList<Transaction>()
                        for (i in response.body()!!.data!!){
                            tempList.add(Transaction(txnId = i.txnId, txnAmt =i.txnAmt.toLong(), txnDate = i.txnDate))
                        }
                        adapter.updateList(tempList)

                    }else{
                        response.body()!!.message?.let { binding.root.snackbar(it) }
                    }

                } else {
                    response.body()!!.message?.let { binding.root.snackbar(it) }
                }

            }

            override fun onFailure(call: Call<LoanRepaymentResponse>, t: Throwable) {
                t.message?.let { binding.root.snackbar(it) }
                dialogLoader?.hideProgressDialog()

            }
        })


    }

    override fun setupClickListeners() {
        binding.toolbarLayout.backBtn.setOnClickListener { requireActivity().onBackPressed() }
        binding.payBtn.setOnClickListener { checkInputs() }
        binding.tvViewMore.setOnClickListener { loadMoreTransactions() }
    }

    private fun loadMoreTransactions() {
        dialogLoader = context?.let { DialogLoader(it) }
        dialogLoader?.showProgressDialog()

        /**********************Retrofit to call get repayments Endpoint *********************/
        var call: Call<LoanRepaymentResponse>? = apiRequests?.getPastRepayment(
            Constants.ACCESS_TOKEN,
            mViewModel.getLoan()!!.loanId,
            generateRequestId(),
            "getRepaymentHistory"
        )
        call!!.enqueue(object : Callback<LoanRepaymentResponse> {
            override  fun onResponse(
                call: Call<LoanRepaymentResponse>,
                response: Response<LoanRepaymentResponse>
            ) {
                if (response.isSuccessful) {
                    dialogLoader?.hideProgressDialog()
                    if (response.body()!!.status == 1) {
                        val newAddList = ArrayList<Transaction>()
                        adapter.addNewItems(newAddList)
                        for (i in response.body()!!.data!!){
                            newAddList.add(Transaction(txnId = i.txnId, txnAmt =i.txnAmt.toLong(), txnDate = i.txnDate))
                        }
                        adapter.addNewItems(newAddList)

                    }else{
                        response.body()!!.message?.let { binding.root.snackbar(it) }
                    }

                } else {
                    response.body()!!.message?.let { binding.root.snackbar(it) }
                }

            }

            override fun onFailure(call: Call<LoanRepaymentResponse>, t: Throwable) {
                t.message?.let { binding.root.snackbar(it) }
                dialogLoader?.hideProgressDialog()

            }
        })

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

        mViewModel.setPayment(Withdraw(amount = amount.toLong(),getString(R.string.phone_code)+mobileNumber))
        payAmount()
    }

    private fun payAmount() {
        navController.navigate(R.id.action_global_enterPinFragment, bundleOf(Config.LOGIN_TYPE to EnterPinType.MAKE_PAYMENT))
    }

}