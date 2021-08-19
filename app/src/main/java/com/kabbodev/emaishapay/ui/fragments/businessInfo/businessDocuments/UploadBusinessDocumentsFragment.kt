package com.kabbodev.emaishapay.ui.fragments.businessInfo.businessDocuments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.fragment.app.activityViewModels
import com.kabbodev.emaishapay.R
import com.kabbodev.emaishapay.databinding.FragmentUploadBusinessDocumentsBinding
import com.kabbodev.emaishapay.ui.activities.ImagePickerActivity
import com.kabbodev.emaishapay.ui.base.BaseFragment
import com.kabbodev.emaishapay.ui.viewModels.LoginViewModel
import com.kabbodev.emaishapay.utils.navigateUsingPopUp
import com.kabbodev.emaishapay.utils.snackbar
import com.kabbodev.emaishapay.utils.updatePhotoLayout
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

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

    private val photosLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
        val data = activityResult.data
        if (activityResult.resultCode == Activity.RESULT_OK) {
            val result = data?.getStringExtra("result")
            Timber.d("Result $result")
            if (result != null) {
                when (mode) {
                    1 -> {
                        tradeLicensePhotoUri = result.toUri()
                        binding.tradeLicense.updatePhotoLayout(tradeLicensePhotoUri)
                    }
                    2 -> {
                        registrationCertificatePhotoUri = result.toUri()
                        binding.registrationCertificate.updatePhotoLayout(registrationCertificatePhotoUri)
                    }
                    3 -> {
                        taxRegCertificatePhotoUri = result.toUri()
                        binding.taxRegCertificate.updatePhotoLayout(taxRegCertificatePhotoUri)
                    }
                    4 -> {
                        taxClearanceCertificatePhotoUri = result.toUri()
                        binding.taxClearanceCertificate.updatePhotoLayout(taxClearanceCertificatePhotoUri)
                    }
                    5 -> {
                        bankStatementPhotoUri = result.toUri()
                        binding.bankStatement.updatePhotoLayout(bankStatementPhotoUri)
                    }
                    6 -> {
                        auditedFinancialsPhotoUri = result.toUri()
                        binding.auditedFinancials.updatePhotoLayout(auditedFinancialsPhotoUri)
                    }
                    7 -> {
                        businessPlanPhotoUri = result.toUri()
                        binding.businessPlan.updatePhotoLayout(businessPlanPhotoUri)
                    }
                    8 -> {
                        receiptBookPhotoUri = result.toUri()
                        binding.receiptBook.updatePhotoLayout(receiptBookPhotoUri)
                    }
                }
            }
        }
    }

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentUploadBusinessDocumentsBinding.inflate(inflater, container, false)

    override fun setupTheme() {
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

        navController.navigateUsingPopUp(R.id.homeFragment, R.id.action_global_homeFragment)
    }

}