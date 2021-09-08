package com.kabbodev.emaishapay.ui.fragments

import android.text.InputType
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.google.android.material.textview.MaterialTextView
import com.kabbodev.emaishapay.databinding.FragmentUpdateAcountDetailsBinding
import com.kabbodev.emaishapay.ui.base.BaseFragment
import com.kabbodev.emaishapay.ui.viewModels.LoanViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class UpdateAccountDetailsFragment :  BaseFragment<FragmentUpdateAcountDetailsBinding>() {
    private val mViewModel: LoanViewModel by activityViewModels()

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentUpdateAcountDetailsBinding.inflate(inflater, container, false)

    override fun setupTheme() {
        /************get user from shared preferences********************/

        context?.let {
            mViewModel.getCurrentUser( false, it).observe(viewLifecycleOwner, { user ->
                binding.user=user
            })
        }
    }

    override fun setupClickListeners() {
        binding.editBtn.setOnClickListener{activateTextViewEditable()}
       binding.confirmBtn.setOnClickListener{checkInputs()}
    }

    private fun activateTextViewEditable(){

        allowEditable(binding.tvFullNameValue)
        allowEditable(binding.tvPhoneNumberValue)
        allowEditable(binding.tvMeailValue)

    }

    private fun checkInputs(){
        /**********endpoint to update account details**************/

    }

    private fun allowEditable(textview: MaterialTextView){
        textview.isCursorVisible = true;
        textview.isFocusableInTouchMode = true;
        textview.inputType = InputType.TYPE_CLASS_TEXT;
        textview.requestFocus(); //to trigger the soft input
    }

}