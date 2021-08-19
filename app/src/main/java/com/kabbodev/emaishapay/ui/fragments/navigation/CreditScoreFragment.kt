package com.kabbodev.emaishapay.ui.fragments.navigation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.kabbodev.emaishapay.R
import com.kabbodev.emaishapay.data.models.screen.CreditFactorItem
import com.kabbodev.emaishapay.databinding.FragmentCreditScoreBinding
import com.kabbodev.emaishapay.ui.adapters.screen.CreditFactorAdapter
import com.kabbodev.emaishapay.ui.base.BaseFragment
import com.kabbodev.emaishapay.ui.viewModels.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class CreditScoreFragment : BaseFragment<FragmentCreditScoreBinding>() {

    private val mViewModel: LoginViewModel by activityViewModels()
    private lateinit var adapter: CreditFactorAdapter


    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentCreditScoreBinding.inflate(inflater, container, false)

    override fun setupTheme() {
        binding.layoutCreditScore.creditScoreGauge.highValue = 70f

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

}