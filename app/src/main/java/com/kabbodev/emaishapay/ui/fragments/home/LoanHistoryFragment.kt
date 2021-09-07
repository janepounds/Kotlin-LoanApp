package com.kabbodev.emaishapay.ui.fragments.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.kabbodev.emaishapay.R
import com.kabbodev.emaishapay.constants.Constants
import com.kabbodev.emaishapay.data.enums.LoanStatus
import com.kabbodev.emaishapay.data.models.Loan
import com.kabbodev.emaishapay.data.models.responses.ContactResponse
import com.kabbodev.emaishapay.data.models.responses.LoanResponse
import com.kabbodev.emaishapay.databinding.FragmentLoanHistoryBinding
import com.kabbodev.emaishapay.network.ApiClient
import com.kabbodev.emaishapay.network.ApiRequests
import com.kabbodev.emaishapay.ui.adapters.LoanAdapter
import com.kabbodev.emaishapay.ui.base.BaseFragment
import com.kabbodev.emaishapay.ui.viewModels.LoanViewModel
import com.kabbodev.emaishapay.ui.viewModels.LoginViewModel
import com.kabbodev.emaishapay.utils.DialogLoader
import com.kabbodev.emaishapay.utils.generateRequestId
import com.kabbodev.emaishapay.utils.snackbar
import com.kabbodev.emaishapay.utils.startAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@AndroidEntryPoint
class LoanHistoryFragment : BaseFragment<FragmentLoanHistoryBinding>() {

    private val mViewModel: LoanViewModel by activityViewModels()
    private val loginViewModel: LoginViewModel by activityViewModels()
    private lateinit var adapter: LoanAdapter
    private val apiRequests: ApiRequests? by lazy { ApiClient.getLoanInstance() }
    private var dialogLoader: DialogLoader? = null


    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentLoanHistoryBinding.inflate(inflater, container, false)

    override fun setupTheme() {

        adapter = LoanAdapter {
            mViewModel.setLoan(it)
            navController.navigate(R.id.action_global_loanDetailsFragment)
        }
        binding.adapter = adapter
        getLoanHistory()

    }

    private fun getLoanHistory(){
        dialogLoader = context?.let { DialogLoader(it) }
        dialogLoader?.showProgressDialog()
        /**********************Retrofit to call loan history Endpoint *********************/
        var call: Call<LoanResponse>? = apiRequests?.getLoanHistory(
            Constants.ACCESS_TOKEN,
            generateRequestId(),
            "getLoanHistory"
        )
        call!!.enqueue(object : Callback<LoanResponse> {
            override  fun onResponse(
                call: Call<LoanResponse>,
                response: Response<LoanResponse>
            ) {
                if (response.isSuccessful) {
                    dialogLoader?.hideProgressDialog()
                    if (response.body()!!.status == 1) {
                        val list = ArrayList<Loan>()
                        for (i in response.body()!!.data!!){

                            list.add(Loan(loanId = i.loan_id, status = LoanStatus.valueOf(i.status.uppercase()), amt = i.amount.toLong()))
                        }
                        adapter.updateList(list)

                    }else{
                        dialogLoader?.hideProgressDialog()
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
                    dialogLoader?.hideProgressDialog()
                    response.body()!!.message?.let { binding.root.snackbar(it) }
                }

            }

            override fun onFailure(call: Call<LoanResponse>, t: Throwable) {
                t.message?.let { binding.root.snackbar(it) }
                dialogLoader?.hideProgressDialog()

            }
        })



    }



    override fun setupClickListeners() {
    }

}