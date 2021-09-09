package com.cabral.emaishapay.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.cabral.emaishapay.R
import com.cabral.emaishapay.constants.Constants
import com.cabral.emaishapay.data.models.AuthenticationResponse
import com.cabral.emaishapay.data.models.responses.ChangePinResponse
import com.cabral.emaishapay.databinding.FragmentAccountBinding
import com.cabral.emaishapay.databinding.FragmentChangePinBinding
import com.cabral.emaishapay.network.ApiClient
import com.cabral.emaishapay.network.ApiRequests
import com.cabral.emaishapay.ui.base.BaseFragment
import com.cabral.emaishapay.ui.viewModels.LoginViewModel
import com.cabral.emaishapay.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@AndroidEntryPoint
class ChangePinFragment : BaseFragment<FragmentChangePinBinding>(){
    private val apiRequests: ApiRequests? by lazy { ApiClient.getLoanInstance() }
    private var pin: String? = null
    private var dialogLoader: DialogLoader? = null
    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentChangePinBinding.inflate(inflater, container, false)

    override fun setupTheme() {
        binding.etOldPin.editText?.transformationMethod = AsteriskPasswordTransformationMethod()
        binding.eetNewPin.editText?.transformationMethod = AsteriskPasswordTransformationMethod()
        binding.etConfirmPin.editText?.transformationMethod = AsteriskPasswordTransformationMethod()
        binding.etOldPin.addEndIconClickListener()
        binding.eetNewPin.addEndIconClickListener()
        binding.etConfirmPin.addEndIconClickListener()

        lifecycleScope.launch { pin = userPreferences.user?.first()?.pin }
    }

    override fun setupClickListeners() {
        binding.toolbarLayout.backBtn.setOnClickListener { requireActivity().onBackPressed() }
        binding.tvRememberMe.setOnClickListener { onRememberMeClick() }
        binding.confirmPinBtn.setOnClickListener { checkInputs() }
        binding.closeBtn.setOnClickListener { requireActivity().onBackPressed() }
    }
    private fun onRememberMeClick(){
        val tag = binding.tvRememberMe.tag.toString()
        if (tag == "remembered") {
            binding.tvRememberMe.tag = "not_remembered"
            binding.tvRememberMe.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check, 0, 0, 0)
        } else {
            binding.tvRememberMe.tag = "remembered"
            binding.tvRememberMe.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_filled, 0, 0, 0)
        }

    }
    private fun checkInputs(){
        val oldPin = binding.etOldPin.editText?.text.toString().trim()
        val newPin = binding.eetNewPin.editText?.text.toString().trim()
        val confirmPin = binding.etConfirmPin.editText?.text.toString().trim()
        val rememberMeValue = binding.tvRememberMe.tag
        var error: String? = null


        if(oldPin!= pin) error = getString(R.string.invalid_pin)
        if (newPin != confirmPin) error = getString(R.string.confirm_pin_not_matches)
        if (newPin.length != 4 || confirmPin.length != 4) error = getString(R.string.invalid_pin)
        if (confirmPin.isEmpty()) error = getString(R.string.confirm_pin_cannot_be_empty)
        if (newPin.isEmpty()) error = getString(R.string.pin_cannot_be_empty)
        if (oldPin.isEmpty()) error = getString(R.string.pin_cannot_be_empty)

        if (!error.isNullOrEmpty()) {
            binding.root.snackbar(error)
            return
        }

        dialogLoader = context?.let { DialogLoader(it) }
        dialogLoader?.showProgressDialog()
        /**********************Retrofit to initiate login *********************/
        var call: Call<ChangePinResponse>? = apiRequests?.changePin(
            "","", generateRequestId(),"","" +
                    "",""

        )
        call!!.enqueue(object : Callback<ChangePinResponse> {
            override  fun onResponse(
                call: Call<ChangePinResponse>,
                response: Response<ChangePinResponse>
            ) {
                if (response.isSuccessful) {
                    dialogLoader?.hideProgressDialog()
                    if (response.body()!!.status == 1) {

                    }else{
                        response.body()!!.message?.let { binding.root.snackbar(it) }
                    }

                } else {
                    response.body()!!.message?.let { binding.root.snackbar(it) }
                }

            }

            override fun onFailure(call: Call<ChangePinResponse>, t: Throwable) {
                t.message?.let { binding.root.snackbar(it) }
                dialogLoader?.hideProgressDialog()

            }
        })

    }

}