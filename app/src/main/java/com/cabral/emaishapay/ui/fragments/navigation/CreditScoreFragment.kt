package com.cabral.emaishapay.ui.fragments.navigation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.cabral.emaishapay.R
import com.cabral.emaishapay.constants.Constants
import com.cabral.emaishapay.data.models.responses.CreditScoreResponse
import com.cabral.emaishapay.data.models.screen.CreditFactorItem
import com.cabral.emaishapay.databinding.FragmentCreditScoreBinding
import com.cabral.emaishapay.network.ApiClient
import com.cabral.emaishapay.network.ApiRequests
import com.cabral.emaishapay.ui.adapters.screen.CreditFactorAdapter
import com.cabral.emaishapay.ui.base.BaseFragment
import com.cabral.emaishapay.ui.viewModels.LoginViewModel
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
import timber.log.Timber

@AndroidEntryPoint
class CreditScoreFragment : BaseFragment<FragmentCreditScoreBinding>() {

    private val mViewModel: LoginViewModel by activityViewModels()
    private lateinit var adapter: CreditFactorAdapter
    private val apiRequests: ApiRequests? by lazy { ApiClient.getLoanInstance() }
    private var dialogLoader: DialogLoader? = null

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentCreditScoreBinding.inflate(inflater, container, false)

    override fun setupTheme() {
        getCreditScore()

        val list = ArrayList<CreditFactorItem>()
        list.add(CreditFactorItem(color = R.color.green, title = getString(R.string.business_profile), percentage = 10))
        list.add(CreditFactorItem(color = R.color.violet, title = getString(R.string.business_documents), percentage = 20))
        list.add(CreditFactorItem(color = R.color.orange, title = getString(R.string.business_assets), percentage = 15))
        list.add(CreditFactorItem(color = R.color.darkBlue_1, title = getString(R.string.credit_history), percentage = 25))
        list.add(CreditFactorItem(color = R.color.primaryColor, title = getString(R.string.payment_history), percentage = 30))
        adapter = CreditFactorAdapter(list)

        binding.recyclerView.adapter = adapter
    }

    override fun setupClickListeners() {
        binding.layoutCreditScore.creditScoreGauge.setOnEventListener { gauge, lowValue, highValue, isRunning ->
            Timber.e("Low: $lowValue  High: $highValue")
            binding.layoutCreditScore.creditScoreValue.text = (highValue * 10).toString()
        }
    }

    private fun getCreditScore(){
        dialogLoader = context?.let { DialogLoader(it) }
        dialogLoader?.showProgressDialog()
        /**********************Retrofit to call new loan Endpoint *********************/
        var call: Call<CreditScoreResponse>? = apiRequests?.getCreditScore(
            Constants.ACCESS_TOKEN, generateRequestId(),"getUserCreditScrore"
        )
        call!!.enqueue(object : Callback<CreditScoreResponse> {
            override  fun onResponse(
                call: Call<CreditScoreResponse>,
                response: Response<CreditScoreResponse>
            ) {
                if (response.isSuccessful) {
                    dialogLoader?.hideProgressDialog()
                    if (response.body()!!.status == 1) {
                        binding.layoutCreditScore.creditScoreGauge.highValue = response.body()!!.data!!.credit_score
                    }else{
                        dialogLoader?.hideProgressDialog()
                        response.body()!!.message?.let { binding.root.snackbar(it) }
                    }

                }else if(response.code()==401){
                    lifecycleScope.launch { userPreferences.user?.first()?.let {
                        mViewModel.setPhoneNumber(
                            it.phoneNumber.substring(3 ))
                    } }
                    dialogLoader?.hideProgressDialog()
                    binding.root.snackbar(getString(R.string.session_expired))
                    startAuth(navController)
                } else{  if(response.body()!!.message?.isNotEmpty()) {
                    response.body()!!.message?.let { binding.root.snackbar(it) }
                    dialogLoader?.hideProgressDialog()
                }
                }

            }

            override fun onFailure(call: Call<CreditScoreResponse>, t: Throwable) {
                t.message?.let { binding.root.snackbar(it) }
                dialogLoader?.hideProgressDialog()

            }
        })
    }


}