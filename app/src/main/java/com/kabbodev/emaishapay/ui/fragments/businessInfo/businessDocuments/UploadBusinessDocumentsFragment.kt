package com.kabbodev.emaishapay.ui.fragments.businessInfo.businessDocuments

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.kabbodev.emaishapay.R
import com.kabbodev.emaishapay.constants.Constants
import com.kabbodev.emaishapay.data.config.Config
import com.kabbodev.emaishapay.data.enums.EnterPinType
import com.kabbodev.emaishapay.data.models.responses.BusinessDetailsResponse
import com.kabbodev.emaishapay.data.models.responses.BusinessDocumentsData
import com.kabbodev.emaishapay.data.models.responses.BusinessDocumentsResponse
import com.kabbodev.emaishapay.data.models.responses.IdDocumentData
import com.kabbodev.emaishapay.databinding.FragmentUploadBusinessDocumentsBinding
import com.kabbodev.emaishapay.network.ApiClient
import com.kabbodev.emaishapay.network.ApiRequests
import com.kabbodev.emaishapay.ui.activities.ImagePickerActivity
import com.kabbodev.emaishapay.ui.base.BaseFragment
import com.kabbodev.emaishapay.ui.viewModels.LoginViewModel
import com.kabbodev.emaishapay.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.io.ByteArrayOutputStream

@AndroidEntryPoint
class UploadBusinessDocumentsFragment : BaseFragment<FragmentUploadBusinessDocumentsBinding>() {

    private val mViewModel: LoginViewModel by activityViewModels()
    private var mode: Int = -1
    private var tradeLicensePhotoUri: Uri? = null
    private var registrationCertificatePhotoUri: Uri? = null
    private var taxRegCertificatePhotoUri: Uri? = null
    private var taxClearanceCertificatePhotoUri: Uri? = null
    private var bankStatementPhotoUri: Uri? = null
    private var auditedFinancialsPhotoUri: Uri? = null
    private var businessPlanPhotoUri: Uri? = null
    private var receiptBookPhotoUri: Uri? = null
    private var encodedTradeLicensePhotoID: String? =null
    private var encodedRegistrationCertificatePhotoID: String? =null
    private var encodedTaxRegCertificatePhotoID: String? =null
    private var encodedTaxClearanceCertificatePhotoID: String? =null
    private var encodedBankStatementPhotoID: String? =null
    private var encodedAuditedFinancialsPhotoID: String? =null
    private var encodedBusinessPlanPhotoID: String? =null
    private var encodedReceiptBookPhotoID: String? =null
    private val apiRequests: ApiRequests? by lazy { ApiClient.getLoanInstance() }
    private var dialogLoader: DialogLoader? = null

    private val photosLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
        val data = activityResult.data
        if (activityResult.resultCode == Activity.RESULT_OK) {
            val result = data?.getStringExtra("result")
            Timber.d("Result $result")
            if (result != null) {
                when (mode) {
                    1 -> {

                        tradeLicensePhotoUri = result.toUri()
                        encodedTradeLicensePhotoID = encodeSelectedImage(getRealPathFromUri(requireContext(),tradeLicensePhotoUri)!!)
                        binding.tradeLicense.updatePhotoLayout(tradeLicensePhotoUri)
                    }
                    2 -> {

                        registrationCertificatePhotoUri = result.toUri()
                        encodedRegistrationCertificatePhotoID = encodeSelectedImage(getRealPathFromUri(requireContext(),registrationCertificatePhotoUri)!!)
                        binding.registrationCertificate.updatePhotoLayout(registrationCertificatePhotoUri)
                    }
                    3 -> {
                        taxRegCertificatePhotoUri = result.toUri()
                        encodedTaxRegCertificatePhotoID = encodeSelectedImage(getRealPathFromUri(requireContext(),taxRegCertificatePhotoUri)!!)
                        binding.taxRegCertificate.updatePhotoLayout(taxRegCertificatePhotoUri)
                    }
                    4 -> {

                        taxClearanceCertificatePhotoUri = result.toUri()
                        encodedTaxClearanceCertificatePhotoID = encodeSelectedImage(getRealPathFromUri(requireContext(),taxClearanceCertificatePhotoUri)!!)
                        binding.taxClearanceCertificate.updatePhotoLayout(taxClearanceCertificatePhotoUri)
                    }
                    5 -> {

                        bankStatementPhotoUri = result.toUri()
                        encodedBankStatementPhotoID = encodeSelectedImage(getRealPathFromUri(requireContext(),bankStatementPhotoUri)!!)
                        binding.bankStatement.updatePhotoLayout(bankStatementPhotoUri)
                    }
                    6 -> {

                        auditedFinancialsPhotoUri = result.toUri()
                        encodedAuditedFinancialsPhotoID = encodeSelectedImage(getRealPathFromUri(requireContext(),auditedFinancialsPhotoUri)!!)
                        binding.auditedFinancials.updatePhotoLayout(auditedFinancialsPhotoUri)
                    }
                    7 -> {
                        businessPlanPhotoUri = result.toUri()
                        encodedBusinessPlanPhotoID = encodeSelectedImage(getRealPathFromUri(requireContext(),businessPlanPhotoUri)!!)
                        binding.businessPlan.updatePhotoLayout(businessPlanPhotoUri)
                    }
                    8 -> {

                        receiptBookPhotoUri = result.toUri()
                        encodedReceiptBookPhotoID = encodeSelectedImage(getRealPathFromUri(requireContext(),receiptBookPhotoUri)!!)
                        binding.receiptBook.updatePhotoLayout(receiptBookPhotoUri)
                    }
                }
            }
        }
    }

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentUploadBusinessDocumentsBinding.inflate(inflater, container, false)

    override fun setupTheme() {
        loadBusinessDocuments()
    }

    private fun loadBusinessDocuments(){
        dialogLoader = context?.let { DialogLoader(it) }
        dialogLoader?.showProgressDialog()
        var call: Call<BusinessDocumentsResponse>? = apiRequests?.getBusinessDocuments(
            Constants.ACCESS_TOKEN,
            generateRequestId(),
            "getBusinessDocs"
        )
        call!!.enqueue(object : Callback<BusinessDocumentsResponse> {
            override fun onResponse(
                call: Call<BusinessDocumentsResponse>,
                response: Response<BusinessDocumentsResponse>
            ) {
                if (response.isSuccessful) {
                    dialogLoader?.hideProgressDialog()

                    if (response.body()!!.status == 1) {
                        /************populate all fields in UI*****************/
                        binding.tradeLicense.updatePhotoLayout((Constants.LOAN_API_URL+response.body()!!.data?.trade_license).toUri())
                        binding.registrationCertificate.updatePhotoLayout((Constants.LOAN_API_URL+response.body()!!.data?.reg_certificate).toUri())
                        binding.taxRegCertificate.updatePhotoLayout((Constants.LOAN_API_URL+response.body()!!.data?.tax_reg_certificate).toUri())
                        binding.taxClearanceCertificate.updatePhotoLayout((Constants.LOAN_API_URL+response.body()!!.data?.tax_clearance_certificate).toUri())
                        binding.bankStatement.updatePhotoLayout((Constants.LOAN_API_URL+response.body()!!.data?.bank_statement).toUri())
                        binding.auditedFinancials.updatePhotoLayout((Constants.LOAN_API_URL+response.body()!!.data?.audited_financials).toUri())
                        binding.receiptBook.updatePhotoLayout((Constants.LOAN_API_URL+response.body()!!.data?.receipt_book).toUri())
                        response.body()!!.message?.let { binding.root.snackbar(it) }

                    } else {
                        response.body()!!.message?.let { binding.root.snackbar(it) }
                        dialogLoader?.hideProgressDialog()

                    }

                } else if (response.code() == 401) {
                    /***************redirect to auth*********************/
                    response.body()!!.message?.let { binding.root.snackbar(it)}
                    dialogLoader?.hideProgressDialog()
                    if (navController.currentDestination?.id!! != R.id.enterPinFragment) {
                        navController.popBackStack(R.id.homeFragment, false)
                        navController.navigate(
                            R.id.action_homeFragment_to_enterPinFragment,
                            bundleOf(Config.LOGIN_TYPE to EnterPinType.LOGIN)
                        )
                    }

                } else {
                    response.body()!!.message?.let { binding.root.snackbar(it) }
                    dialogLoader?.hideProgressDialog()
                }

            }

            override fun onFailure(call: Call<BusinessDocumentsResponse>, t: Throwable) {
                t.message?.let { binding.root.snackbar(it) }
                dialogLoader?.hideProgressDialog()


            }
        })
    }

    override fun setupClickListeners() {
        binding.layoutOwnerInfo.backBtn.setOnClickListener { requireActivity().onBackPressed() }
        binding.saveBtn.setOnClickListener { checkInputs() }
        binding.saveAndNextBtn.setOnClickListener { checkInputs() }
        binding.tradeLicense.cardView.setOnClickListener { openCamera(1) }
        binding.registrationCertificate.cardView.setOnClickListener { openCamera(2) }
        binding.taxRegCertificate.cardView.setOnClickListener { openCamera(3) }
        binding.taxClearanceCertificate.cardView.setOnClickListener { openCamera(4) }
        binding.bankStatement.cardView.setOnClickListener { openCamera(5) }
        binding.auditedFinancials.cardView.setOnClickListener { openCamera(6) }
        binding.businessPlan.cardView.setOnClickListener { openCamera(7) }
        binding.receiptBook.cardView.setOnClickListener { openCamera(8) }
    }

    private fun openCamera(photoMode: Int) {
        mode = photoMode
        val intent = Intent(requireActivity(), ImagePickerActivity::class.java)
        photosLauncher.launch(intent)
    }

    private fun checkInputs() {
        var error: String? = null
        if (tradeLicensePhotoUri == null) error = String().format(getString(R.string.photos_error), getString(R.string.trade_license_text))
        if (registrationCertificatePhotoUri == null) error = String().format(getString(R.string.photos_error), getString(R.string.registration_certificate))
        if (taxRegCertificatePhotoUri == null) error = String().format(getString(R.string.photos_error), getString(R.string.tax_reg_certificate))
        if (taxClearanceCertificatePhotoUri == null) error = String().format(getString(R.string.photos_error), getString(R.string.tax_clearance_certificate))
        if (bankStatementPhotoUri == null) error = String().format(getString(R.string.photos_error), getString(R.string.bank_statement))
        if (auditedFinancialsPhotoUri == null) error = String().format(getString(R.string.photos_error), getString(R.string.audited_financials))
        if (businessPlanPhotoUri == null) error = String().format(getString(R.string.photos_error), getString(R.string.business_plan))
        if (receiptBookPhotoUri == null) error = String().format(getString(R.string.photos_error), getString(R.string.receipt_book))

        if (!error.isNullOrEmpty()) {
            binding.root.snackbar(error)
            return
        }
        /*****************post documents and redirect to home*************************/
        val requestObject = JSONObject()
        val data = BusinessDocumentsData(
            trade_license = encodedTradeLicensePhotoID!!,
            reg_certificate = encodedRegistrationCertificatePhotoID!!,
            tax_reg_certificate = encodedTaxRegCertificatePhotoID!!,
            tax_clearance_certificate = encodedTaxClearanceCertificatePhotoID!!,
            bank_statement = encodedBankStatementPhotoID!!,
            audited_financials = encodedAuditedFinancialsPhotoID!!,
            business_plan = encodedBusinessPlanPhotoID!!,
            receipt_book = encodedReceiptBookPhotoID!!
        )
        requestObject.put("params",data)

        dialogLoader?.showProgressDialog()
        var call: Call<BusinessDocumentsResponse>? = apiRequests?.postBusinessDocuments(
            Constants.ACCESS_TOKEN,
            requestObject,
            generateRequestId(),
            "saveBusinessDocs"
        )
        call!!.enqueue(object : Callback<BusinessDocumentsResponse> {
            override fun onResponse(
                call: Call<BusinessDocumentsResponse>,
                response: Response<BusinessDocumentsResponse>
            ) {
                if (response.isSuccessful) {
                    dialogLoader?.hideProgressDialog()

                    if (response.body()!!.status == 1) {
                        response.body()!!.message?.let { binding.root.snackbar(it) }
                        navController.navigateUsingPopUp(R.id.homeFragment, R.id.action_global_homeFragment)

                    } else {
                        response.body()!!.message?.let { binding.root.snackbar(it) }
                        dialogLoader?.hideProgressDialog()

                    }

                } else if (response.code() == 401) {
                    /***************redirect to auth*********************/
                    response.body()!!.message?.let { binding.root.snackbar(it)}
                    dialogLoader?.hideProgressDialog()
//                    EnterPinFragment.startAuth(true)


                } else {
                    response.body()!!.message?.let { binding.root.snackbar(it) }
                    dialogLoader?.hideProgressDialog()
                }

            }

            override fun onFailure(call: Call<BusinessDocumentsResponse>, t: Throwable) {
                t.message?.let { binding.root.snackbar(it) }
                dialogLoader?.hideProgressDialog()


            }
        })

    }

    private fun encodeSelectedImage(bm: Bitmap): String? {
        val baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val b = baos.toByteArray()
        return Base64.encodeToString(b, Base64.DEFAULT)
    }

}