package com.kabbodev.emaishapay.ui.fragments.businessInfo.businessProfile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.kabbodev.emaishapay.R
import com.kabbodev.emaishapay.databinding.FragmentEnterBusinessDetailsBinding
import com.kabbodev.emaishapay.ui.base.BaseFragment
import com.kabbodev.emaishapay.ui.viewModels.LoginViewModel
import com.kabbodev.emaishapay.utils.initSpinner
import com.kabbodev.emaishapay.utils.isPhoneNumberValid
import com.kabbodev.emaishapay.utils.snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EnterBusinessDetailsFragment : BaseFragment<FragmentEnterBusinessDetailsBinding>() {

    private val mViewModel: LoginViewModel by activityViewModels()


    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentEnterBusinessDetailsBinding.inflate(inflater, container, false)

    override fun setupTheme() {
        binding.spinnerBusinessType.initSpinner(this)
        binding.spinnerIndustry.initSpinner(this)
    }

    override fun setupClickListeners() {
        binding.progressLayout.layoutBusinessInfo.backBtn.setOnClickListener { requireActivity().onBackPressed() }
        binding.saveBtn.setOnClickListener { checkInputs(false) }
        binding.saveAndNextBtn.setOnClickListener { checkInputs(true) }
    }

    private fun checkInputs(proceedNext: Boolean) {
        val businessTypes: List<String> = listOf(*resources.getStringArray(R.array.business_type))
        val industryArray: List<String> = listOf(*resources.getStringArray(R.array.industry))

        val businessName = binding.etBusinessName.editText?.text.toString().trim()
        val dateRegistered = binding.etDateRegistered.editText?.text.toString().trim()
        val registrationNo = binding.etRegistrationNo.editText?.text.toString().trim()
        val location = binding.etLocation.editText?.text.toString().trim()
        val contactPerson = binding.etContactPerson.editText?.text.toString().trim()
        val phoneNumber = binding.etPhoneNumber.editText?.text.toString().trim()
        val numberOfEmployees = binding.etNumberOfEmployees.editText?.text.toString().trim()
        val avgMonthlyRevenue = binding.etAvgMonthlyRevenue.editText?.text.toString().trim()

        var industry: String? = null
        var businessType: String? = null
        var error: String? = phoneNumber.isPhoneNumberValid()

        if (binding.spinnerIndustry.selectedIndex >= 0) {
            industry = industryArray[binding.spinnerIndustry.selectedIndex]
        } else {
            error = String.format(getString(R.string.select_error), getString(R.string.industry))
        }

        if (binding.spinnerBusinessType.selectedIndex >= 0) {
            businessType = businessTypes[binding.spinnerBusinessType.selectedIndex]
        } else {
            error = String.format(getString(R.string.select_error), getString(R.string.business_type))
        }

        if (avgMonthlyRevenue.isEmpty()) error = String.format(getString(R.string.cannot_be_empty_error), getString(R.string.avg_monthly_revenue))
        if (numberOfEmployees.isEmpty()) error = String.format(getString(R.string.cannot_be_empty_error), getString(R.string.number_of_employees))
        if (contactPerson.isEmpty()) error = String.format(getString(R.string.cannot_be_empty_error), getString(R.string.contact_person))
        if (location.isEmpty()) error = String.format(getString(R.string.cannot_be_empty_error), getString(R.string.location))
        if (registrationNo.isEmpty()) error = String.format(getString(R.string.cannot_be_empty_error), getString(R.string.registration_no))
        if (dateRegistered.isEmpty()) error = String.format(getString(R.string.cannot_be_empty_error), getString(R.string.date_of_birth))
        if (businessName.isEmpty()) error = String.format(getString(R.string.cannot_be_empty_error), getString(R.string.business_name))

        if (!error.isNullOrEmpty()) {
            binding.root.snackbar(error)
            return
        }
        if (proceedNext) navController.navigate(R.id.action_enterBusinessDetailsFragment_to_verificationDocumentsFragment)
    }


}