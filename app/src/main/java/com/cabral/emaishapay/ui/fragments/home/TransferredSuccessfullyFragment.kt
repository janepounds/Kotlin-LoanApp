package com.cabral.emaishapay.ui.fragments.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.cabral.emaishapay.R
import com.cabral.emaishapay.databinding.FragmentTransferredSuccessfullyBinding
import com.cabral.emaishapay.ui.base.BaseFragment
import com.cabral.emaishapay.ui.viewModels.LoanViewModel
import com.cabral.emaishapay.utils.navigateUsingPopUp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TransferredSuccessfullyFragment : BaseFragment<FragmentTransferredSuccessfullyBinding>() {

    private val mViewModel: LoanViewModel by activityViewModels()


    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentTransferredSuccessfullyBinding.inflate(inflater, container, false)

    override fun setupTheme() {
        /************get user from shared preferences********************/

        context?.let {
            mViewModel.getCurrentUser( false, it).observe(viewLifecycleOwner, { user ->
                user?.let {
                    binding.user = it
                }
            })
        }


    }

    override fun setupClickListeners() {
        binding.viewDetailsBtn.setOnClickListener {

        }
        binding.continueBtn.setOnClickListener {
            navController.navigateUsingPopUp(R.id.withdrawFundsFragment, R.id.action_global_homeFragment)
        }
    }

}