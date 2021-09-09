package com.cabral.emaishapay.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.cabral.emaishapay.utils.DialogLoader
import com.cabral.emaishapay.utils.generateRequestId
import com.cabral.emaishapay.utils.navigateUsingPopUp
import com.cabral.emaishapay.utils.snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@AndroidEntryPoint
class ChangePinFragment : BaseFragment<FragmentChangePinBinding>(){
    private val apiRequests: ApiRequests? by lazy { ApiClient.getLoanInstance() }
    private var dialogLoader: DialogLoader? = null
    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentChangePinBinding.inflate(inflater, container, false)

    override fun setupTheme() {}

    override fun setupClickListeners() {
        binding.confirmPinBtn.setOnClickListener{
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

}