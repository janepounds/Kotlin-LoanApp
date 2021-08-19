package com.kabbodev.emaishapay.ui.fragments.businessInfo.businessProfile

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.fragment.app.activityViewModels
import com.kabbodev.emaishapay.R
import com.kabbodev.emaishapay.databinding.FragmentVerificationDocumentsBinding
import com.kabbodev.emaishapay.ui.activities.ImagePickerActivity
import com.kabbodev.emaishapay.ui.base.BaseFragment
import com.kabbodev.emaishapay.ui.viewModels.LoginViewModel
import com.kabbodev.emaishapay.utils.navigateUsingPopUp
import com.kabbodev.emaishapay.utils.snackbar
import com.kabbodev.emaishapay.utils.updatePhotoLayout
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class VerificationDocumentsFragment : BaseFragment<FragmentVerificationDocumentsBinding>() {

    private val mViewModel: LoginViewModel by activityViewModels()
    private var mode: Int = -1
    private var officeShopPhotoUri: Uri? = null
    private var officeShopVideoUri: Uri? = null
    private var selfieShopOfficePhotoUri: Uri? = null
    private var neighbourhoodPhotoUri: Uri? = null
    private var utilityBillPhotoUri: Uri? = null

    private val photosLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
        val data = activityResult.data
        if (activityResult.resultCode == Activity.RESULT_OK) {
            val result = data?.getStringExtra("result")
            Timber.d("Result $result")
            if (result != null) {
                when (mode) {
                    1 -> {
                        officeShopPhotoUri = result.toUri()
                        binding.officeShopPhoto.updatePhotoLayout(officeShopPhotoUri)
                    }
                    2 -> {
                        officeShopVideoUri = result.toUri()
                        binding.officeShopVideo.updatePhotoLayout(officeShopVideoUri)
                    }
                    3 -> {
                        selfieShopOfficePhotoUri = result.toUri()
                        binding.selfieShopOffice.updatePhotoLayout(selfieShopOfficePhotoUri)
                    }
                    4 -> {
                        neighbourhoodPhotoUri = result.toUri()
                        binding.neighbourhoodPhoto.updatePhotoLayout(neighbourhoodPhotoUri)
                    }
                    5 -> {
                        utilityBillPhotoUri = result.toUri()
                        binding.utilityBill.updatePhotoLayout(utilityBillPhotoUri)
                    }
                }
            }
        }
    }


    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentVerificationDocumentsBinding.inflate(inflater, container, false)

    override fun setupTheme() {
    }

    override fun setupClickListeners() {
        binding.progressLayout.layoutBusinessInfo.backBtn.setOnClickListener { requireActivity().onBackPressed() }
        binding.saveBtn.setOnClickListener { checkInputs() }
        binding.saveAndNextBtn.setOnClickListener { checkInputs() }
        binding.officeShopPhoto.cardView.setOnClickListener { openCamera(1) }
        binding.officeShopVideo.cardView.setOnClickListener { openCamera(2) }
        binding.selfieShopOffice.cardView.setOnClickListener { openCamera(3) }
        binding.neighbourhoodPhoto.cardView.setOnClickListener { openCamera(4) }
        binding.utilityBill.cardView.setOnClickListener { openCamera(5) }
    }

    private fun openCamera(photoMode: Int) {
        mode = photoMode
        val intent = Intent(requireActivity(), ImagePickerActivity::class.java)
        photosLauncher.launch(intent)
    }

    private fun checkInputs() {
        var error: String? = null
        if (officeShopPhotoUri == null) error = String().format(getString(R.string.photos_error), getString(R.string.office_shop_photo))
        if (officeShopVideoUri == null) error = String().format(getString(R.string.photos_error), getString(R.string.office_shop_video))
        if (selfieShopOfficePhotoUri == null) error = String().format(getString(R.string.photos_error), getString(R.string.selfie_in_shop_office))
        if (neighbourhoodPhotoUri == null) error = String().format(getString(R.string.photos_error), getString(R.string.neighbourhood_photo))
        if (utilityBillPhotoUri == null) error = String().format(getString(R.string.photos_error), getString(R.string.utility_bill))

        if (!error.isNullOrEmpty()) {
            binding.root.snackbar(error)
            return
        }

        navController.navigateUsingPopUp(R.id.homeFragment, R.id.action_global_homeFragment)
    }

}