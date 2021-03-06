package com.cabral.emaishapay.ui.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.cabral.emaishapay.R
import com.cabral.emaishapay.constants.Constants
import com.cabral.emaishapay.data.config.Config
import com.cabral.emaishapay.data.enums.CreatePinType
import com.cabral.emaishapay.data.enums.EnterPinType
import com.cabral.emaishapay.data.models.AuthenticationResponse
import com.cabral.emaishapay.data.models.Loan
import com.cabral.emaishapay.data.models.User
import com.cabral.emaishapay.data.models.Withdraw
import com.cabral.emaishapay.data.models.responses.LoanInitiationResponse
import com.cabral.emaishapay.data.models.responses.LoanRepaymentResponse
import com.cabral.emaishapay.data.models.responses.LoanResponse
import com.cabral.emaishapay.data.models.responses.WithdrawResponse
import com.cabral.emaishapay.databinding.DialogLoanStatusBinding
import com.cabral.emaishapay.databinding.FragmentEnterPinBinding
import com.cabral.emaishapay.network.ApiClient
import com.cabral.emaishapay.network.ApiRequests
import com.cabral.emaishapay.singleton.MyApplication
import com.cabral.emaishapay.ui.base.BaseFragment
import com.cabral.emaishapay.ui.viewModels.LoanViewModel
import com.cabral.emaishapay.ui.viewModels.LoginViewModel
import com.cabral.emaishapay.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@AndroidEntryPoint
class EnterPinFragment : BaseFragment<FragmentEnterPinBinding>() {
    private  val TAG = "EnterPinFragment"

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
        /************get user from shared preferences********************/
        if(loginType!=EnterPinType.LOGIN)
        lifecycleScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                context?.let {
                    loanViewModel.getCurrentUser(false, it )
                        .observe(viewLifecycleOwner, { user ->
                            user?.let {
                                statusDialogBinding.user = it

                            }
                        })
                }
            }

        }

    }

    override fun setupClickListeners() {
        binding.toolbarLayout.backBtn.setOnClickListener { requireActivity().onBackPressed() }
        binding.closeBtn.setOnClickListener { requireActivity().onBackPressed() }
        binding.keypadLayout.deleteBtn.setOnClickListener { onDeleteBtnClick() }
        binding.keypadLayout.doneBtn.setOnClickListener { onDoneBtnClick() }
        binding.keypadLayout.setKeyPadListener { keyValue -> onKeyPadClick(keyValue) }
        binding.clickHereTv.setOnClickListener{navController.navigate(R.id.action_enterPinFragment_to_forgotPinFragment, bundleOf(Config.CREATE_PIN_TYPE to CreatePinType.FORGOT_PIN)) }
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
        dialogLoader = context?.let { DialogLoader(it) }
        dialogLoader?.showProgressDialog()
        val pinValue = binding.etEnterPin.editText?.text.toString()
        if (pinValue.length != 4) {
            binding.root.snackbar(getString(R.string.invalid_pin))
            return
        }
        mViewModel.setPin(pinValue)

        when (loginType) {
            EnterPinType.LOGIN -> {
                /**********************Retrofit to initiate login *********************/
                var call: Call<AuthenticationResponse>? = apiRequests?.initiateLogin(
                    getString(R.string.phone_code)+mViewModel.getPhoneNumber(),
                    Constants.PREPIN+pinValue,
                    generateRequestId(),
                    "initiateUserLogin"
                )
                call!!.enqueue(object : Callback<AuthenticationResponse> {
                    override  fun onResponse(
                        call: Call<AuthenticationResponse>,
                        response: Response<AuthenticationResponse>
                    ) {
                        if (response.isSuccessful) {
                            dialogLoader?.hideProgressDialog()
                            if (response.body()!!.status == 1) {
                                Constants.ACCESS_TOKEN = response.body()!!.access_token
                                /************user_data in shared preferences****************************/

                                lifecycleScope.launch { response.body()?.let { userPreferences.saveUserData(it!!.data, it!!.access_token,pinValue,true) }}
                                navController.navigateUsingPopUp(R.id.welcomeFragment, R.id.action_global_homeFragment)
                            }else{
                                response.body()!!.message?.let { binding.root.snackbar(it) }
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
            EnterPinType.NEW_LOAN -> {
                /**********************Retrofit to call new loan Endpoint *********************/
                var call: Call<LoanInitiationResponse>? = apiRequests?.postNewLoan(
                    Constants.ACCESS_TOKEN,
                    loanViewModel.loanAmount.toDouble(),
                    loanViewModel.duration,
                    loanViewModel.type,
                    loanViewModel.loanDueAmount.toDouble(),
                    loanViewModel.interestRate,
                    loanViewModel.processingFee.toDouble(),
                    Constants.PREPIN+pinValue,
                    generateRequestId(),
                    "applyForLoan"
                )
                call!!.enqueue(object : Callback<LoanInitiationResponse> {
                    override  fun onResponse(
                        call: Call<LoanInitiationResponse>,
                        response: Response<LoanInitiationResponse>
                    ) {
                        if (response.isSuccessful) {
                            dialogLoader?.hideProgressDialog()
                            if (response.body()!!.status == 1) {
                                statusDialog.show()
                            }else{
                                dialogLoader?.hideProgressDialog()
                                response.body()!!.message?.let { binding.root.snackbar(it) }
                            }

                        }else if(response.code()==401){
                            lifecycleScope.launch { userPreferences.user?.first()?.let {
                                mViewModel.setPhoneNumber(
                                    it.phoneNumber.substring(3 ))
                            } }
                            dialogLoader?.hideProgressDialog()
                            binding.root.snackbar(getString(R.string.session_expired))
                            startAuth(navController)
                        } else {  if(response.body()!!.message?.isNotEmpty()) {
                            response.body()!!.message?.let { binding.root.snackbar(it) }
                            dialogLoader?.hideProgressDialog()
                        }
                        }

                    }

                    override fun onFailure(call: Call<LoanInitiationResponse>, t: Throwable) {
                        t.message?.let { binding.root.snackbar(it) }
                        dialogLoader?.hideProgressDialog()

                    }
                })

            }

            EnterPinType.WITHDRAW ->{
                /**********************Retrofit to call withdraw funds Endpoint *********************/
                var call: Call<WithdrawResponse>? = apiRequests?.withdrawFunds(
                    Constants.ACCESS_TOKEN,
                    loanViewModel.getWithdraw()?.phoneNumber,
                    loanViewModel.getWithdraw()?.amount,
                    Constants.PREPIN+pinValue,
                    getString(R.string.currency_code),
                    generateRequestId(),
                    "mobileMoneyWithdraw"
                )
                call!!.enqueue(object : Callback<WithdrawResponse> {
                    override  fun onResponse(
                        call: Call<WithdrawResponse>,
                        response: Response<WithdrawResponse>
                    ) {
                        if (response.isSuccessful) {
                            dialogLoader?.hideProgressDialog()
                            if (response.body()!!.status == 1) {
                                navController.navigateUsingPopUp(R.id.welcomeFragment, R.id.action_global_transferredSuccessfullyFragment)
                            }else{
                                response.body()!!.message?.let { binding.root.snackbar(it) }
                            }

                        }else if(response.code()==401){
                            lifecycleScope.launch { userPreferences.user?.first()?.let {
                                mViewModel.setPhoneNumber(
                                    it.phoneNumber.substring(3 ))
                            } }
                            dialogLoader?.hideProgressDialog()
                            binding.root.snackbar(getString(R.string.session_expired))
                            startAuth(navController)
                        } else {  if(response.body()!!.message?.isNotEmpty()) {
                            response.body()!!.message?.let { binding.root.snackbar(it) }
                            dialogLoader?.hideProgressDialog()
                        }
                        }

                    }

                    override fun onFailure(call: Call<WithdrawResponse>, t: Throwable) {
                        t.message?.let { binding.root.snackbar(it) }
                        dialogLoader?.hideProgressDialog()

                    }
                })

            }

            EnterPinType.MAKE_PAYMENT -> {
                /**********************Retrofit to call make payment Endpoint *********************/
                var call: Call<LoanRepaymentResponse>? = apiRequests?.makeLoanPayment(
                    Constants.ACCESS_TOKEN,
                    loanViewModel.getPayment()?.amount?.toDouble(),
                    loanViewModel.getPayment()?.phoneNumber,
                    Constants.PREPIN+pinValue,
                    getString(R.string.currency_code),
                    loanViewModel.getLoan()!!.loanId,
                    generateRequestId(),
                "mobileMoneyLoanPayment"
                )
                call!!.enqueue(object : Callback<LoanRepaymentResponse> {
                    override  fun onResponse(
                        call: Call<LoanRepaymentResponse>,
                        response: Response<LoanRepaymentResponse>
                    ) {
                        if (response.isSuccessful) {
                            dialogLoader?.hideProgressDialog()
                            if (response.body()!!.status == 1) {
                                statusDialog.show()
                            }else{
                                response.body()!!.message?.let { binding.root.snackbar(it) }
                            }

                        } else if(response.code()==401){
                            lifecycleScope.launch { userPreferences.user?.first()?.let {
                                mViewModel.setPhoneNumber(
                                    it.phoneNumber.substring(3 ))
                            } }
                            dialogLoader?.hideProgressDialog()
                            binding.root.snackbar(getString(R.string.session_expired))
                            startAuth(navController)
                        }else {  if(response.body()!!.message?.isNotEmpty()) {
                            response.body()!!.message?.let { binding.root.snackbar(it) }
                            dialogLoader?.hideProgressDialog()
                        }
                        }

                    }

                    override fun onFailure(call: Call<LoanRepaymentResponse>, t: Throwable) {
                        t.message?.let { binding.root.snackbar(it) }
                        dialogLoader?.hideProgressDialog()

                    }
                })

            }

        }
    }

    fun interface KeyPadListener {
        fun onKeyClick(keyValue: Int)
    }


}