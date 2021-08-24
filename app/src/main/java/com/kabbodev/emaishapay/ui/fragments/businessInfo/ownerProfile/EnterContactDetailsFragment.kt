package com.kabbodev.emaishapay.ui.fragments.businessInfo.ownerProfile

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import com.kabbodev.emaishapay.R
import com.kabbodev.emaishapay.databinding.FragmentEnterContactDetailsBinding
import com.kabbodev.emaishapay.ui.base.BaseFragment
import com.kabbodev.emaishapay.ui.viewModels.LoginViewModel
import com.kabbodev.emaishapay.utils.initSpinner
import com.kabbodev.emaishapay.utils.isPhoneNumberValid
import com.kabbodev.emaishapay.utils.snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EnterContactDetailsFragment : BaseFragment<FragmentEnterContactDetailsBinding>() {

    private val mViewModel: LoginViewModel by activityViewModels()


    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentEnterContactDetailsBinding.inflate(
        inflater,
        container,
        false
    )

    override fun setupTheme() {
        binding.spinnerResidentialType.initSpinner(this)

        val districtListAdapter: ArrayAdapter<String>? =
            context?.let { ArrayAdapter<String>(it, android.R.layout.simple_dropdown_item_1line, listOf(*resources.getStringArray(R.array.residential_types))) }
        binding.autoCompleteDistrict.threshold = 1
        binding.autoCompleteDistrict.setAdapter(districtListAdapter)
        binding.autoCompleteDistrict.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                binding.autoCompleteDistrict.showDropDown()
            }

        })
    }

    override fun setupClickListeners() {
        binding.progressLayout.layoutOwnerInfo.backBtn.setOnClickListener { requireActivity().onBackPressed() }
        binding.saveBtn.setOnClickListener { checkInputs(false) }
        binding.saveAndNextBtn.setOnClickListener { checkInputs(true) }
    }

    private fun checkInputs(proceedNext: Boolean) {
        val residentialTypes: List<String> = listOf(*resources.getStringArray(R.array.residential_types))

        val district = binding.autoCompleteDistrict?.text.toString().trim()
        val village = binding.etVillage.editText?.text.toString().trim()
        val mobileNumber = binding.etMobileNumber.etPhoneNumber.editText?.text.toString().trim()
        val landLordName = binding.etLandlordName.editText?.text.toString().trim()
        val landlordPhoneNumber = binding.etLandlordPhoneNumber.etPhoneNumber.editText?.text.toString().trim()
        var residentialType: String? = null
        var error: String? = landlordPhoneNumber.isPhoneNumberValid()
        val mobileNumberError = mobileNumber.isPhoneNumberValid()

        if (binding.spinnerResidentialType.selectedIndex >= 0) {
            residentialType = residentialTypes[binding.spinnerResidentialType.selectedIndex]
        } else {
            error = String.format(
                getString(R.string.select_error),
                getString(R.string.residential_type)
            )
        }

        if (mobileNumberError != null) error = mobileNumberError
        if (landlordPhoneNumber.isEmpty()) error = String.format(
            getString(R.string.cannot_be_empty_error), getString(
                R.string.land_lord_phone_number
            )
        )
        if (landLordName.isEmpty()) error = String.format(
            getString(R.string.cannot_be_empty_error), getString(
                R.string.land_lord_name
            )
        )
        if (mobileNumber.isEmpty()) error = String.format(
            getString(R.string.cannot_be_empty_error), getString(
                R.string.mobile_number
            )
        )
        if (village.isEmpty()) error = String.format(
            getString(R.string.cannot_be_empty_error), getString(
                R.string.village
            )
        )
        if (district.isEmpty()) error = String.format(
            getString(R.string.cannot_be_empty_error), getString(
                R.string.district
            )
        )

        if (!error.isNullOrEmpty()) {
            binding.root.snackbar(error)
            return
        }
        if (proceedNext) navController.navigate(R.id.action_enterContactDetailsFragment_to_enterGuarantorDetailsFragment)
    }

}