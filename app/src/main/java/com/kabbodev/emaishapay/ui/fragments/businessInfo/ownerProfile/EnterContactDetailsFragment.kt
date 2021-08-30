package com.kabbodev.emaishapay.ui.fragments.businessInfo.ownerProfile

import android.opengl.Visibility
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.kabbodev.emaishapay.R
import com.kabbodev.emaishapay.constants.Constants
import com.kabbodev.emaishapay.data.models.RegistrationResponse
import com.kabbodev.emaishapay.data.models.responses.ContactResponse
import com.kabbodev.emaishapay.data.models.responses.UserResponse
import com.kabbodev.emaishapay.databinding.FragmentEnterContactDetailsBinding
import com.kabbodev.emaishapay.network.ApiClient
import com.kabbodev.emaishapay.network.ApiRequests
import com.kabbodev.emaishapay.ui.base.BaseFragment
import com.kabbodev.emaishapay.ui.fragments.EnterPinFragment
import com.kabbodev.emaishapay.ui.viewModels.LoginViewModel
import com.kabbodev.emaishapay.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.security.acl.Owner

@AndroidEntryPoint
class EnterContactDetailsFragment : BaseFragment<FragmentEnterContactDetailsBinding>() {

    private val mViewModel: LoginViewModel by activityViewModels()
    private val apiRequests: ApiRequests? by lazy { ApiClient.getLoanInstance() }
    private var dialogLoader: DialogLoader? = null


    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentEnterContactDetailsBinding.inflate(
        inflater,
        container,
        false
    )

    override fun setupTheme() {
        /*********load personal details from the server*************/
        loadContactDetails()
        binding.spinnerResidentialType.initSpinner(this)

        if(binding.spinnerResidentialType.text.equals("Owner")){
            /**********hide landord contact and name************/
            binding.tvLandlordName.visibility = GONE
            binding.etLandlordName.visibility = GONE
            binding.tvLandlordPhoneNumber.visibility = GONE
            binding.etLandlordPhoneNumber.etPhoneNumber.visibility = GONE

        }

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

    private fun loadContactDetails(){
        dialogLoader = context?.let { DialogLoader(it) }
        dialogLoader?.showProgressDialog()
        var call: Call<ContactResponse>? = apiRequests?.getContactDetails(
            Constants.ACCESS_TOKEN,
            generateRequestId(),
            "getContactDetails"
        )
        call!!.enqueue(object : Callback<ContactResponse> {
            override fun onResponse(
                call: Call<ContactResponse>,
                response: Response<ContactResponse>
            ) {
                if (response.isSuccessful) {
                    dialogLoader?.hideProgressDialog()

                    if (response.body()!!.status == 1) {
                        /************populate all fields in UI*****************/
                        binding.etDistrict.editText?.text ?:  response.body()!!.data!!.district
                        binding.etVillage.editText?.text?: response.body()!!.data!!.village
                        binding.etMobileNumber.etPhoneNumber?.editText?.text?: response.body()!!.data!!.mobile_phone
                        binding.spinnerResidentialType.text?: response.body()!!.data!!.residential_type
                        if(binding.spinnerResidentialType.text.equals("Owner",)){

                            /**********hide landord contact and name************/
                            binding.tvLandlordName.visibility = GONE
                            binding.etLandlordName.visibility = GONE
                            binding.tvLandlordPhoneNumber.visibility = GONE
                            binding.etLandlordPhoneNumber.etPhoneNumber.visibility = GONE
                        }else {

                            binding.etLandlordPhoneNumber.etPhoneNumber.editText?.text ?: response.body()!!.data!!.landlord_contact
                            binding.etLandlordName.editText?.text ?: response.body()!!.data!!.landlord

                        }


                    }else{
                        response.body()!!.message?.let { binding.root.snackbar(it) }
                        dialogLoader?.hideProgressDialog()

                    }

                } else if(response.code()==401) {
                    /***************redirect to auth*********************/
                    response.body()!!.message?.let { binding.root.snackbar(it) }
                    dialogLoader?.hideProgressDialog()
//                    EnterPinFragment.startAuth(true)


                }else{
                    response.body()!!.message?.let { binding.root.snackbar(it) }
                    dialogLoader?.hideProgressDialog()
                }

            }

            override fun onFailure(call: Call<ContactResponse>, t: Throwable) {
                t.message?.let { binding.root.snackbar(it) }
                dialogLoader?.hideProgressDialog()

            }
        })


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
        if(residentialType.equals("Tenant",ignoreCase = true)) {
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
        }
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
        if (proceedNext){
            dialogLoader?.showProgressDialog()
            /***************endpoint for updating contact details*********************/
            var call: Call<ContactResponse>? = apiRequests?.postContactDetails(
                Constants.ACCESS_TOKEN,
                district,village,residentialType,mobileNumber,landLordName,landlordPhoneNumber,
                generateRequestId(),
                "saveContactDetails"
            )
            call!!.enqueue(object : Callback<ContactResponse> {
                override fun onResponse(
                    call: Call<ContactResponse>,
                    response: Response<ContactResponse>
                ) {
                    if (response.isSuccessful) {
                        dialogLoader?.hideProgressDialog()
                        if (response.body()!!.status == 1) {

                            navController.navigate(R.id.action_enterContactDetailsFragment_to_enterGuarantorDetailsFragment)
                        } else {
                            response.body()!!.message?.let { binding.root.snackbar(it) }

                        }

                    } else if(response.code()==401){
                        response.body()!!.message?.let { binding.root.snackbar(it) }
                        dialogLoader?.hideProgressDialog()
//                        EnterPinFragment.startAuth(true)
                    }else{
                        response.body()!!.message?.let { binding.root.snackbar(it) }
                        dialogLoader?.hideProgressDialog()
                    }

                }

                override fun onFailure(call: Call<ContactResponse>, t: Throwable) {
                    t.message?.let { binding.root.snackbar(it) }
                    dialogLoader?.hideProgressDialog()

                }
            })


        }
    }

}