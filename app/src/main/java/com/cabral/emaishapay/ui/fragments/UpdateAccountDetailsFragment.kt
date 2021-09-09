package com.cabral.emaishapay.ui.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.text.InputType
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.fragment.app.activityViewModels
import com.google.android.material.textview.MaterialTextView
import com.cabral.emaishapay.databinding.FragmentUpdateAcountDetailsBinding
import com.cabral.emaishapay.ui.activities.ImagePickerActivity
import com.cabral.emaishapay.ui.base.BaseFragment
import com.cabral.emaishapay.ui.viewModels.LoanViewModel
import com.cabral.emaishapay.utils.getRealPathFromUri
import com.cabral.emaishapay.utils.loadImage
import com.cabral.emaishapay.utils.updatePhotoLayout
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber


@AndroidEntryPoint
class UpdateAccountDetailsFragment :  BaseFragment<FragmentUpdateAcountDetailsBinding>() {
    private val mViewModel: LoanViewModel by activityViewModels()
    private var profilePicUri: Uri? = null
    private var mode: Int = -1

    private val photosLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
        val data = activityResult.data
        if (activityResult.resultCode == Activity.RESULT_OK) {
            val result = data?.getStringExtra("result")
            Timber.d("Result $result")
            if (result != null) {
                when (mode) {
                    1 -> {

                        profilePicUri = result.toUri()
                        binding.userImage.loadImage(profilePicUri!!)

                    }
                }
            }
        }
    }
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
        binding.userImage.setOnClickListener { openCamera(1) }
        binding.tvPersonalDetailsEdit.setOnClickListener{activateTextViewEditable()}
       binding.confirmBtn.setOnClickListener{checkInputs()}
    }


    private fun openCamera(photoMode: Int) {
        mode = photoMode
        val intent = Intent(requireActivity(), ImagePickerActivity::class.java)
        photosLauncher.launch(intent)
    }
    private fun activateTextViewEditable(){
        allowEditable(binding.tvPhoneNumberValue)
        allowEditable(binding.tvMeailValue)
        allowEditable(binding.tvFullNameValue)

    }

    private fun checkInputs(){
        /**********endpoint to update account details**************/

    }

    private fun allowEditable(editTextView: TextInputEditText){
        editTextView.isCursorVisible = true;
        editTextView.isFocusableInTouchMode = true;
        editTextView.inputType = InputType.TYPE_CLASS_TEXT;
        editTextView.requestFocus(); //to trigger the soft input
    }

}