package com.kabbodev.emaishapay.ui.fragments.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.kabbodev.emaishapay.R
import com.kabbodev.emaishapay.databinding.FragmentTransferredSuccessfullyBinding
import com.kabbodev.emaishapay.ui.base.BaseFragment
import com.kabbodev.emaishapay.ui.viewModels.LoanViewModel
import com.kabbodev.emaishapay.utils.navigateUsingPopUp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TransferredSuccessfullyFragment : BaseFragment<FragmentTransferredSuccessfullyBinding>() {

    private val mViewModel: LoanViewModel by activityViewModels()


    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentTransferredSuccessfullyBinding.inflate(inflater, container, false)

    override fun setupTheme() {
        mViewModel.getCurrentUser("user_id", false).observe(viewLifecycleOwner, { user ->
            user?.let {
                binding.user = it
            }
        })
    }

    override fun setupClickListeners() {
        binding.viewDetailsBtn.setOnClickListener {

        }
        binding.continueBtn.setOnClickListener {
            navController.navigateUsingPopUp(R.id.withdrawFundsFragment, R.id.action_global_homeFragment)
        }
    }

}