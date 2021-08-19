package com.kabbodev.emaishapay.ui.fragments.businessInfo.ownerProfile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.kabbodev.emaishapay.R
import com.kabbodev.emaishapay.databinding.FragmentEnterGuarantorDetailsBinding
import com.kabbodev.emaishapay.ui.base.BaseFragment
import com.kabbodev.emaishapay.ui.viewModels.LoginViewModel
import com.kabbodev.emaishapay.utils.initSpinner
import com.kabbodev.emaishapay.utils.snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EnterGuarantorDetailsFragment : BaseFragment<FragmentEnterGuarantorDetailsBinding>() {

    private val mViewModel: LoginViewModel by activityViewModels()


    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentEnterGuarantorDetailsBinding.inflate(inflater, container, false)

    override fun setupTheme() {
        binding.firstKin.spinnerGender.initSpinner(this)
        binding.firstKin.spinnerRelationship.initSpinner(this)
        binding.secondKin.spinnerGender.initSpinner(this)
        binding.secondKin.spinnerRelationship.initSpinner(this)
    }

    override fun setupClickListeners() {
        binding.progressLayout.layoutOwnerInfo.backBtn.setOnClickListener { requireActivity().onBackPressed() }
        binding.saveBtn.setOnClickListener { checkInputs(false) }
        binding.saveAndNextBtn.setOnClickListener { checkInputs(true) }
    }

    private fun checkInputs(proceedNext: Boolean) {
//        val genders: List<String> = listOf(*resources.getStringArray(R.array.gender))
//        val educationLevelArray: List<String> = listOf(*resources.getStringArray(R.array.education_level))
//        val maritalStatusArray: List<String> = listOf(*resources.getStringArray(R.array.marital_status))
//
//        val fullName = binding.etFullName.editText?.text.toString().trim()
//        val yearsInBusiness = binding.etYearInBusiness.editText?.text.toString().trim()
//        val nationalId = binding.etNationalId.editText?.text.toString().trim()
//        var gender: String? = null
//        var educationLevel: String? = null
//        var maritalStatus: String? = null
//
//        var error: String? = null
//        if (binding.spinnerGender.selectedIndex >= 0) {
//            gender = genders[binding.spinnerGender.selectedIndex]
//        } else {
//            error = String.format(getString(R.string.select_error), getString(R.string.gender))
//        }
//
//        if (binding.spinnerEducationLevel.selectedIndex >= 0) {
//            educationLevel = educationLevelArray[binding.spinnerEducationLevel.selectedIndex]
//        } else {
//            error = String.format(getString(R.string.select_error), getString(R.string.education_level))
//        }
//
//        if (binding.spinnerMaritalStatus.selectedIndex >= 0) {
//            maritalStatus = maritalStatusArray[binding.spinnerMaritalStatus.selectedIndex]
//        } else {
//            error = String.format(getString(R.string.select_error), getString(R.string.marital_status))
//        }
//
//        if (fullName.isEmpty()) error = getString(R.string.full_name_cannot_be_empty)
//        if (yearsInBusiness.isEmpty()) error = String.format(getString(R.string.cannot_be_empty_error), getString(R.string.years_in_business))
//        if (nationalId.isEmpty()) error = String.format(getString(R.string.cannot_be_empty_error), getString(R.string.national_id))
//
//        if (!error.isNullOrEmpty()) {
//            binding.root.snackbar(error)
//            return
//        }
        if (proceedNext) navController.navigate(R.id.action_enterGuarantorDetailsFragment_to_uploadIdDocumentsFragment)
    }

}