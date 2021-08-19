package com.kabbodev.emaishapay.ui.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.kabbodev.emaishapay.R
import com.kabbodev.emaishapay.data.config.Config
import com.kabbodev.emaishapay.data.enums.EnterPinType
import com.kabbodev.emaishapay.databinding.DialogLoanStatusBinding
import com.kabbodev.emaishapay.databinding.FragmentEnterPinBinding
import com.kabbodev.emaishapay.singleton.MyApplication
import com.kabbodev.emaishapay.ui.base.BaseFragment
import com.kabbodev.emaishapay.ui.viewModels.LoanViewModel
import com.kabbodev.emaishapay.ui.viewModels.LoginViewModel
import com.kabbodev.emaishapay.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EnterPinFragment : BaseFragment<FragmentEnterPinBinding>() {

    private val mViewModel: LoginViewModel by activityViewModels()
    private val loanViewModel: LoanViewModel by activityViewModels()
    private var loginType: EnterPinType = EnterPinType.LOGIN
    private lateinit var statusDialogBinding: DialogLoanStatusBinding
    private lateinit var statusDialog: Dialog


    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentEnterPinBinding.inflate(inflater, container, false)

    override fun setupTheme() {
        loginType = requireArguments().getSerializable(Config.LOGIN_TYPE) as EnterPinType
        binding.isLogin = loginType == EnterPinType.LOGIN
        binding.etEnterPin.editText?.transformationMethod = AsteriskPasswordTransformationMethod()
        binding.etEnterPin.editText?.inputType = InputType.TYPE_NULL
        binding.etEnterPin.addEndIconClickListener()
        setupDialog()

        when (loginType) {
            EnterPinType.NEW_LOAN -> binding.title = getString(R.string.authorize_application)
            EnterPinType.WITHDRAW -> binding.title = getString(R.string.withdraw_funds)
            EnterPinType.MAKE_PAYMENT -> binding.title = getString(R.string.authorize_payment)
        }

        loanViewModel.getCurrentUser("user_id", false).observe(viewLifecycleOwner, { user ->
            user?.let {
                statusDialogBinding.user = it
            }
        })
    }

    override fun setupClickListeners() {
        binding.toolbarLayout.backBtn.setOnClickListener { requireActivity().onBackPressed() }
        binding.closeBtn.setOnClickListener { requireActivity().onBackPressed() }
        binding.clickHereTv.setOnClickListener { }
        binding.keypadLayout.deleteBtn.setOnClickListener { onDeleteBtnClick() }
        binding.keypadLayout.doneBtn.setOnClickListener { onDoneBtnClick() }
        binding.keypadLayout.setKeyPadListener { keyValue -> onKeyPadClick(keyValue) }
    }

    private fun setupDialog() {
        statusDialogBinding = DialogLoanStatusBinding.inflate(layoutInflater, binding.root as ViewGroup, false)
        statusDialog = createFullScreenDialog(statusDialogBinding, R.drawable.slider_bg, false)
        when (loginType) {
            EnterPinType.NEW_LOAN -> {
                statusDialogBinding.successImage.visibility = View.GONE
                statusDialogBinding.statusText.text = getString(R.string.loan_review_status_text)
                statusDialogBinding.okayBtn.text = getString(R.string.okay)
            }
            EnterPinType.MAKE_PAYMENT -> {
                statusDialogBinding.successImage.visibility = View.VISIBLE
                statusDialogBinding.statusText.text = String.format(
                    getString(R.string.payment_successfully_text),
                    MyApplication.getNumberFormattedString(35000),
                    "10067143120",
                    MyApplication.getNumberFormattedString(4500)
                )
                statusDialogBinding.okayBtn.text = getString(R.string.thank_you)
            }
        }

        statusDialogBinding.okayBtn.setOnClickListener {
            statusDialog.dismiss()
            navController.navigateUsingPopUp(R.id.myLoansFragment, R.id.action_global_homeFragment)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun onKeyPadClick(keyValue: Int) {
        val pinValue = binding.etEnterPin.editText?.text.toString()
        if (pinValue.length < 4) binding.etEnterPin.editText?.setText("$pinValue$keyValue")
    }

    private fun onDeleteBtnClick() {
        val pinValue = binding.etEnterPin.editText?.text.toString()
        if (pinValue.isNotEmpty()) binding.etEnterPin.editText?.setText(pinValue.dropLast(1))
    }

    private fun onDoneBtnClick() {
        val pinValue = binding.etEnterPin.editText?.text.toString()
        if (pinValue.length != 4) {
            binding.root.snackbar(getString(R.string.invalid_pin))
            return
        }

        when (loginType) {
            EnterPinType.LOGIN -> {
                lifecycleScope.launch { userPreferences.saveIsLoggedIn(true) }
                navController.navigateUsingPopUp(R.id.welcomeFragment, R.id.action_global_homeFragment)
            }
            EnterPinType.NEW_LOAN -> statusDialog.show()
            EnterPinType.WITHDRAW -> navController.navigateUsingPopUp(R.id.welcomeFragment, R.id.action_global_transferredSuccessfullyFragment)
            EnterPinType.MAKE_PAYMENT -> statusDialog.show()
        }
    }

    fun interface KeyPadListener {
        fun onKeyClick(keyValue: Int)
    }

}