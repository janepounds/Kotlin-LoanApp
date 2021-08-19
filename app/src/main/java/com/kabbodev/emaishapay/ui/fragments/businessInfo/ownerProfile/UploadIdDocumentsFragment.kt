package com.kabbodev.emaishapay.ui.fragments.businessInfo.ownerProfile

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.fragment.app.activityViewModels
import com.kabbodev.emaishapay.R
import com.kabbodev.emaishapay.databinding.FragmentUploadIdDocumentsBinding
import com.kabbodev.emaishapay.ui.activities.ImagePickerActivity
import com.kabbodev.emaishapay.ui.base.BaseFragment
import com.kabbodev.emaishapay.ui.viewModels.LoginViewModel
import com.kabbodev.emaishapay.utils.navigateUsingPopUp
import com.kabbodev.emaishapay.utils.snackbar
import com.kabbodev.emaishapay.utils.updatePhotoLayout
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class UploadIdDocumentsFragment : BaseFragment<FragmentUploadIdDocumentsBinding>() {

    private val mViewModel: LoginViewModel by activityViewModels()
    private var mode: Int = -1
    private var nidFrontSidePhotoUri: Uri? = null
    private var nidBackSidePhotoUri: Uri? = null
    private var profilePhotoUri: Uri? = null
    private var selfieInBusinessPhotoUri: Uri? = null

    private val photosLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
        val data = activityResult.data
        if (activityResult.resultCode == Activity.RESULT_OK) {
            val result = data?.getStringExtra("result")
            Timber.d("Result $result")
            if (result != null) {
                when (mode) {
                    1 -> {
                        nidFrontSidePhotoUri = result.toUri()
                        binding.nationalIdFrontSide.updatePhotoLayout(nidFrontSidePhotoUri)
                    }
                    2 -> {
                        nidBackSidePhotoUri = result.toUri()
                        binding.nationalIdBackSide.updatePhotoLayout(nidBackSidePhotoUri)
                    }
                    3 -> {
                        profilePhotoUri = result.toUri()
                        binding.profilePhoto.updatePhotoLayout(profilePhotoUri)
                    }
                    4 -> {
                        selfieInBusinessPhotoUri = result.toUri()
                        binding.selfieInYourBusiness.updatePhotoLayout(selfieInBusinessPhotoUri)
                    }
                }
            }
        }
    }


    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentUploadIdDocumentsBinding.inflate(inflater, container, false)

    override fun setupTheme() {
    }

    override fun setupClickListeners() {
        binding.progressLayout.layoutOwnerInfo.backBtn.setOnClickListener { requireActivity().onBackPressed() }
        binding.saveBtn.setOnClickListener { checkInputs() }
        binding.saveAndNextBtn.setOnClickListener { checkInputs() }
        binding.nationalIdFrontSide.cardView.setOnClickListener { openCamera(1) }
        binding.nationalIdBackSide.cardView.setOnClickListener { openCamera(2) }
        binding.profilePhoto.cardView.setOnClickListener { openCamera(3) }
        binding.selfieInYourBusiness.cardView.setOnClickListener { openCamera(4) }
    }

    private fun openCamera(photoMode: Int) {
        mode = photoMode
        val intent = Intent(requireActivity(), ImagePickerActivity::class.java)
        photosLauncher.launch(intent)
    }

    private fun checkInputs() {
        var error: String? = null
        if (selfieInBusinessPhotoUri == null) error = String().format(getString(R.string.photos_error), getString(R.string.selfie_in_your_business))
        if (profilePhotoUri == null) error = String().format(getString(R.string.photos_error), getString(R.string.profile_photo))
        if (nidBackSidePhotoUri == null) error = String().format(getString(R.string.photos_error), getString(R.string.national_id_back_side))
        if (nidFrontSidePhotoUri == null) error = String().format(getString(R.string.photos_error), getString(R.string.national_id_front_side))

        if (!error.isNullOrEmpty()) {
            binding.root.snackbar(error)
            return
        }

        navController.navigateUsingPopUp(R.id.homeFragment, R.id.action_global_homeFragment)
    }

}