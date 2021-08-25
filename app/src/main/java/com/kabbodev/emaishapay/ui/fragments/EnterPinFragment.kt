package com.kabbodev.emaishapay.ui.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.kabbodev.emaishapay.R
import com.kabbodev.emaishapay.constants.Constants
import com.kabbodev.emaishapay.data.config.Config
import com.kabbodev.emaishapay.data.enums.EnterPinType
import com.kabbodev.emaishapay.data.models.AuthenticationResponse
import com.kabbodev.emaishapay.data.preferences.UserPreferences
import com.kabbodev.emaishapay.databinding.DialogLoanStatusBinding
import com.kabbodev.emaishapay.databinding.FragmentEnterPinBinding
import com.kabbodev.emaishapay.network.ApiClient
import com.kabbodev.emaishapay.network.ApiRequests
import com.kabbodev.emaishapay.singleton.MyApplication
import com.kabbodev.emaishapay.ui.base.BaseFragment
import com.kabbodev.emaishapay.ui.viewModels.LoanViewModel
import com.kabbodev.emaishapay.ui.viewModels.LoginViewModel
import com.kabbodev.emaishapay.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@AndroidEntryPoint
class EnterPinFragment : BaseFragment<FragmentEnterPinBinding>() {

    private val mViewModel: LoginViewModel by activityViewModels()
    private val loanViewModel: LoanViewModel by activityViewModels()
    private var loginType: EnterPinType = EnterPinType.LOGIN
    private lateinit var statusDialogBinding: DialogLoanStatusBinding
    private lateinit var statusDialog: Dialog
    private val apiRequests: ApiRequests? by lazy { ApiClient.getLoanInstance() }
    private var dialogLoader: DialogLoader? = null

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

        context?.let {
            loanViewModel.getCurrentUser("user_id", false, it).observe(viewLifecycleOwner, { user ->
                user?.let {
                    statusDialogBinding.user = it
                }
            })
        }
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
//            arguments = Bundle().apply {
//                putBoolean("checked",false)
//            }

            navController.navigateUsingPopUp(R.id.myLoansFragment, R.id.myLoansFragment)
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
                dialogLoader?.showProgressDialog()
                /**********************Retrofit to initiate login *********************/
                var call: Call<AuthenticationResponse>? = apiRequests?.initiateLogin(
                    getString(R.string.phone_code)+mViewModel.getPhoneNumber(),
                    Constants.PREPIN+pinValue,
                    generateRequestId(),
                    "initiateUserLogin"
                )
                call!!.enqueue(object : Callback<AuthenticationResponse> {
                    override fun onResponse(
                        call: Call<AuthenticationResponse>,
                        response: Response<AuthenticationResponse>
                    ) {
                        if (response.isSuccessful) {
                            dialogLoader?.hideProgressDialog()
                            if (response.body()!!.status == 1) {
                                Constants.ACCESS_TOKEN = response.body()!!.access_token
                                /************user_data in shared preferences****************************/


                                /**********navigate to home fragment**************/
                                lifecycleScope.launch {
                                    pinValue?.let { userPreferences.savePreference(it, stringPreferencesKey("pin")) }
                                    response.body()!!.data?.let { userPreferences.saveUserId(it.id) }
                                    response.body()!!.access_token?.let { userPreferences.savePreference(it, stringPreferencesKey("access_token")) }
                                    response.body()!!.data?.name?.let { userPreferences.savePreference(it,stringPreferencesKey("name")) }
                                    response.body()!!.data?.email?.let { userPreferences.savePreference(it,stringPreferencesKey("email"))}
                                    response.body()!!.data?.phoneNumber?.let { userPreferences.savePreference(it,stringPreferencesKey("phoneNumber")) }
                                    response.body()!!.data?.balance?.let { userPreferences.saveDoublePreference(it.toDouble(), doublePreferencesKey("balance")) }
                                    response.body()!!.data?.interest_rate?.let { userPreferences.saveInterestRate(it) }
                                    response.body()!!.data?.processing_fee?.let { userPreferences.saveDoublePreference(it, doublePreferencesKey("processing_fee")) }
                                    userPreferences.saveIsLoggedIn(true) }
                                navController.navigateUsingPopUp(R.id.welcomeFragment, R.id.action_global_homeFragment)
                            }

                        } else {
                            response.body()!!.message?.let { binding.root.snackbar(it) }
                        }

                    }

                    override fun onFailure(call: Call<AuthenticationResponse>, t: Throwable) {
                        t.message?.let { binding.root.snackbar(it) }
                        dialogLoader?.hideProgressDialog()

                    }
                })

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