package com.cabral.emaishapay.ui.fragments


import android.view.LayoutInflater
import android.view.ViewGroup
import com.cabral.emaishapay.databinding.FragmentSendEmailBinding
import com.cabral.emaishapay.network.ApiClient
import com.cabral.emaishapay.network.ApiRequests
import com.cabral.emaishapay.ui.base.BaseFragment
import com.cabral.emaishapay.utils.DialogLoader


class SendEmailFragment :  BaseFragment<FragmentSendEmailBinding>()  {

    private val apiRequests: ApiRequests? by lazy { ApiClient.getLoanInstance() }
    private var dialogLoader: DialogLoader? = null
    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentSendEmailBinding.inflate(inflater, container, false)

    override fun setupTheme() {

    }

    override fun setupClickListeners() {
       binding.toolbarLayout.backBtn.setOnClickListener{requireActivity().onBackPressed()}
        binding.sendEmailButton.setOnClickListener{
            dialogLoader = context?.let { DialogLoader(it) }
            dialogLoader?.showProgressDialog()

            /***********call send email endpoint***************/


        }
    }

}