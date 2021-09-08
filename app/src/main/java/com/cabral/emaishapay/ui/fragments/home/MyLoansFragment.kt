package com.cabral.emaishapay.ui.fragments.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.cabral.emaishapay.databinding.FragmentMyLoansBinding
import com.cabral.emaishapay.ui.adapters.screen.ViewPagerAdapter
import com.cabral.emaishapay.ui.base.BaseFragment
import com.cabral.emaishapay.ui.viewModels.LoanViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyLoansFragment  : BaseFragment<FragmentMyLoansBinding>() {

    private val mViewModel: LoanViewModel by activityViewModels()

//    private var newLoanCheck = true
//
//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//        arguments?.getBoolean("check")
//        newLoanCheck = it
//    }
    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentMyLoansBinding.inflate(inflater, container, false)

    override fun setupTheme() {
        binding.newLoanChecked = true
        setupViewPager()
    }

    override fun setupClickListeners() {
        binding.toolbarLayout.backBtn.setOnClickListener { requireActivity().onBackPressed() }
        binding.newLoanCardView.setOnClickListener {
            binding.newLoanChecked = true
            binding.viewpager.currentItem = 0

        }
        binding.loanHistoryCardView.setOnClickListener {
            binding.newLoanChecked = false
            binding.viewpager.currentItem = 1
        }
    }

    private fun setupViewPager() {
        val fragments: ArrayList<Fragment> = ArrayList()
        fragments.add(NewLoanFragment())
        fragments.add(LoanHistoryFragment())

        val adapter = ViewPagerAdapter(this, fragments)
        binding.viewpager.adapter = adapter
        binding.viewpager.offscreenPageLimit = 3
        binding.viewpager.isUserInputEnabled = false
    }


}