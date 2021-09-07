package com.kabbodev.emaishapay.ui.fragments.home

import android.app.Dialog
import android.provider.SyncStateContract
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.kabbodev.emaishapay.R
import com.kabbodev.emaishapay.constants.Constants
import com.kabbodev.emaishapay.data.config.Config
import com.kabbodev.emaishapay.data.enums.EnterPinType
import com.kabbodev.emaishapay.data.models.Transaction
import com.kabbodev.emaishapay.data.models.Withdraw
import com.kabbodev.emaishapay.data.models.responses.LoanResponse
import com.kabbodev.emaishapay.data.models.responses.WithdrawResponse
import com.kabbodev.emaishapay.databinding.DialogWithdrawFundsConfirmationBinding
import com.kabbodev.emaishapay.databinding.FragmentWithdrawFundsBinding
import com.kabbodev.emaishapay.network.ApiClient
import com.kabbodev.emaishapay.network.ApiRequests
import com.kabbodev.emaishapay.singleton.MyApplication
import com.kabbodev.emaishapay.ui.adapters.TransactionAdapter
import com.kabbodev.emaishapay.ui.base.BaseFragment
import com.kabbodev.emaishapay.ui.viewModels.LoanViewModel
import com.kabbodev.emaishapay.ui.viewModels.LoginViewModel
import com.kabbodev.emaishapay.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@AndroidEntryPoint
class WithdrawFundsFragment : BaseFragment<FragmentWithdrawFundsBinding>() {

    private val mViewModel: LoanViewModel by activityViewModels()
    private val loginViewModel: LoginViewModel by activityViewModels()
    private lateinit var adapter: TransactionAdapter
    private lateinit var withdrawConfirmationDialogBinding: DialogWithdrawFundsConfirmationBinding
    private lateinit var withdrawFundsDialog: Dialog
    private val apiRequests: ApiRequests? by lazy { ApiClient.getLoanInstance() }
    private var dialogLoader: DialogLoader? = null


    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentWithdrawFundsBinding.inflate(inflater, container, false)

    override fun setupTheme() {
        setupDialog()
        binding.tvBalance.text = String.format(getString(R.string.balance_amt), MyApplication.getNumberFormattedString(1500))
        getPastWithdraw()
    }

    private fun getPastWithdraw(){
        dialogLoader = context?.let { DialogLoader(it) }
        dialogLoader?.showProgressDialog()

        /**********************Retrofit to call withdraw funds Endpoint *********************/
        var call: Call<WithdrawResponse>? = apiRequests?.getPastWithdraw(
            Constants.ACCESS_TOKEN,
            generateRequestId(),
            "getWithdrawHistory"
        )
        call!!.enqueue(object : Callback<WithdrawResponse> {
            override  fun onResponse(
                call: Call<WithdrawResponse>,
                response: Response<WithdrawResponse>
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

                }else if(response.code()==401){
                    lifecycleScope.launch { userPreferences.user?.first()?.let {
                        loginViewModel.setPhoneNumber(
                            it.phoneNumber.substring(3 ))
                    } }
                    dialogLoader?.hideProgressDialog()
                    binding.root.snackbar(getString(R.string.session_expired))
                    startAuth(navController)
                } else {
                    response.body()!!.message?.let { binding.root.snackbar(it) }
                }

            }

            override fun onFailure(call: Call<WithdrawResponse>, t: Throwable) {
                t.message?.let { binding.root.snackbar(it) }
                dialogLoader?.hideProgressDialog()

            }
        })


    }

    override fun setupClickListeners() {
        binding.toolbarLayout.backBtn.setOnClickListener { requireActivity().onBackPressed() }
        binding.withdrawBtn.setOnClickListener { checkInputs() }
        binding.tvViewMore.setOnClickListener { loadMoreTransactions() }
    }

    private fun setupDialog() {
        withdrawConfirmationDialogBinding = DialogWithdrawFundsConfirmationBinding.inflate(layoutInflater, binding.root as ViewGroup, false)
        withdrawFundsDialog = createFullScreenDialog(withdrawConfirmationDialogBinding, R.drawable.slider_bg, true)
        withdrawConfirmationDialogBinding.confirmBtn.setOnClickListener { withdrawFunds() }
    }

    private fun loadMoreTransactions() {
        dialogLoader = context?.let { DialogLoader(it) }
        dialogLoader?.showProgressDialog()

        /**********************Retrofit to call get past withdraws funds Endpoint *********************/
        var call: Call<WithdrawResponse>? = apiRequests?.getPastWithdraw(
            Constants.ACCESS_TOKEN,
            generateRequestId(),
            "getWithdrawHistory"
        )
        call!!.enqueue(object : Callback<WithdrawResponse> {
            override  fun onResponse(
                call: Call<WithdrawResponse>,
                response: Response<WithdrawResponse>
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

                }else if(response.code()==401){
                    lifecycleScope.launch { userPreferences.user?.first()?.let {
                        loginViewModel.setPhoneNumber(
                            it.phoneNumber.substring(3 ))
                    } }
                    binding.root.snackbar(getString(R.string.session_expired))
                    dialogLoader?.hideProgressDialog()
                    startAuth(navController)
                } else {
                    response.body()!!.message?.let { binding.root.snackbar(it) }
                }

            }

            override fun onFailure(call: Call<WithdrawResponse>, t: Throwable) {
                t.message?.let { binding.root.snackbar(it) }
                dialogLoader?.hideProgressDialog()

            }
        })

    }

    private fun checkInputs() {
        val amount = binding.etAmount.editText.editText?.text.toString()
        val recipient = binding.etRecipient.editText.editText?.text.toString()
        var error: String? = recipient.isPhoneNumberValid()

        if (amount.isEmpty()) error = "Amount cannot be empty!"
        if (amount.isNotEmpty() && amount.toLong() <= 0) error = "Amount cannot be negative or zero!"
        if (error != null) {
            binding.root.snackbar(error)
            return
        }
        mViewModel.setWithdraw(Withdraw(amount = amount.toLong(),phoneNumber = Constants.PREPIN+recipient))


        withdrawConfirmationDialogBinding.valueText1.text = "John Doe"
        withdrawConfirmationDialogBinding.valueText2.text = recipient
        withdrawConfirmationDialogBinding.valueText3.text = String.format(getString(R.string.wallet_balance_value), MyApplication.getNumberFormattedString(amount.toLong()))
        withdrawConfirmationDialogBinding.valueText4.text = String.format(getString(R.string.wallet_balance_value), MyApplication.getNumberFormattedString(950))
        withdrawFundsDialog.show()
    }

    private fun withdrawFunds() {
        withdrawFundsDialog.dismiss()
        navController.navigate(R.id.action_global_enterPinFragment, bundleOf(Config.LOGIN_TYPE to EnterPinType.WITHDRAW))
    }

}