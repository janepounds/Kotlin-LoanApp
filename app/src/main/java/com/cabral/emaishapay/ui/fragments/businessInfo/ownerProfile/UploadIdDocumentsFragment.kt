package com.cabral.emaishapay.ui.fragments.businessInfo.ownerProfile

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.cabral.emaishapay.R
import com.cabral.emaishapay.constants.Constants
import com.cabral.emaishapay.data.models.responses.IdDocumentData
import com.cabral.emaishapay.data.models.responses.IdDocumentResponse
import com.cabral.emaishapay.databinding.FragmentUploadIdDocumentsBinding
import com.cabral.emaishapay.network.ApiClient
import com.cabral.emaishapay.network.ApiRequests
import com.cabral.emaishapay.ui.activities.ImagePickerActivity
import com.cabral.emaishapay.ui.base.BaseFragment
import com.cabral.emaishapay.ui.viewModels.LoginViewModel
import com.cabral.emaishapay.utils.*
import com.google.gson.JsonObject
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.io.ByteArrayOutputStream

@AndroidEntryPoint
class UploadIdDocumentsFragment : BaseFragment<FragmentUploadIdDocumentsBinding>() {
    private  val TAG = "UploadIdDocumentsFragme"

    private val mViewModel: LoginViewModel by activityViewModels()
    private var mode: Int = -1
    private var nidFrontSidePhotoUri: Uri? = null
    private var encodedNidFrontSidePhotoID: String? = null
    private var nidBackSidePhotoUri: Uri? = null
    private var encodedNidBackSidePhotoID: String? = null
    private var profilePhotoUri: Uri? = null
    private var encodedProfilePhotoID: String? = null
    private var selfieInBusinessPhotoUri: Uri? = null
    private var encodedSelfieInBusinessPhotoID: String? = null
    private val apiRequests: ApiRequests? by lazy { ApiClient.getLoanInstance() }
    private var dialogLoader: DialogLoader? = null
    private var images: ArrayList<String>? = null

    private val photosLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
        val data = activityResult.data
        if (activityResult.resultCode == Activity.RESULT_OK) {
            val result = data?.getStringExtra("result")
            Timber.d("Result $result")
            if (result != null) {
                when (mode) {
                    1 -> {
                        nidFrontSidePhotoUri = result.toUri()
                        encodedNidFrontSidePhotoID = encodeSelectedImage(getRealPathFromUri(requireContext(),nidFrontSidePhotoUri)!!)
                        binding.nationalIdFrontSide.updatePhotoLayout(nidFrontSidePhotoUri)

                    }
                    2 -> {
                        nidBackSidePhotoUri = result.toUri()
                        encodedNidBackSidePhotoID = encodeSelectedImage(getRealPathFromUri(requireContext(),nidBackSidePhotoUri)!!)
                        binding.nationalIdBackSide.updatePhotoLayout(nidBackSidePhotoUri)
                    }
                    3 -> {
                        profilePhotoUri = result.toUri()
                        encodedProfilePhotoID = encodeSelectedImage(getRealPathFromUri(requireContext(),profilePhotoUri)!!)
                        binding.profilePhoto.updatePhotoLayout(profilePhotoUri)
                    }
                    4 -> {
                        selfieInBusinessPhotoUri = result.toUri()
                        encodedSelfieInBusinessPhotoID =encodeSelectedImage(getRealPathFromUri(requireContext(),selfieInBusinessPhotoUri)!!)
                        binding.selfieInYourBusiness.updatePhotoLayout(selfieInBusinessPhotoUri)
                    }
                }

            }
        }
    }


    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentUploadIdDocumentsBinding.inflate(inflater, container, false)

    override fun setupTheme() {
        loadIdDocuments()
    }

    private fun loadIdDocuments(){
        dialogLoader = context?.let { DialogLoader(it) }
        dialogLoader?.showProgressDialog()
        var call: Call<IdDocumentResponse>? = apiRequests?.getIdDocuments(
            Constants.ACCESS_TOKEN,
            generateRequestId(),
            "getIdDocuments"
        )
        call!!.enqueue(object : Callback<IdDocumentResponse> {
            override fun onResponse(
                call: Call<IdDocumentResponse>,
                response: Response<IdDocumentResponse>
            ) {
                if (response.isSuccessful) {
                    dialogLoader?.hideProgressDialog()

                    if (response.body()!!.status == 1) {
                        /************populate all fields in UI*****************/
                        binding.nationalIdFrontSide.updatePhotoLayout((Constants.LOAN_API_URL+ response.body()!!.data?.national_id_front).toUri())
                        binding.nationalIdBackSide.updatePhotoLayout((Constants.LOAN_API_URL+ response.body()!!.data?.national_id_back).toUri())
                        binding.profilePhoto.updatePhotoLayout((Constants.LOAN_API_URL+ response.body()!!.data?.profile_picture).toUri())
                        binding.selfieInYourBusiness.updatePhotoLayout((Constants.LOAN_API_URL+ response.body()!!.data?.selfie_in_business).toUri())

                    } else {
                        response.body()!!.message?.let { binding.root.snackbar(it) }
                        dialogLoader?.hideProgressDialog()

                    }

                } else if (response.code() == 401) {
                    /***************redirect to auth*********************/
                    lifecycleScope.launch { userPreferences.user?.first()?.let {
                        mViewModel.setPhoneNumber(
                            it.phoneNumber.substring(3 ))
                    } }
                    dialogLoader?.hideProgressDialog()
                    binding.root.snackbar(getString(R.string.session_expired))
                    startAuth(navController)


                } else {
                    response.body()!!.message?.let { binding.root.snackbar(it) }
                    dialogLoader?.hideProgressDialog()
                }

            }

            override fun onFailure(call: Call<IdDocumentResponse>, t: Throwable) {
                t.message?.let { binding.root.snackbar(it) }
                dialogLoader?.hideProgressDialog()


            }
        })


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
        dialogLoader = context?.let { DialogLoader(it) }
        dialogLoader?.showProgressDialog()
        var error: String? = null
        if (selfieInBusinessPhotoUri == null) error = String().format(getString(R.string.photos_error), getString(R.string.selfie_in_your_business))
        if (profilePhotoUri == null) error = String().format(getString(R.string.photos_error), getString(R.string.profile_photo))
        if (nidBackSidePhotoUri == null) error = String().format(getString(R.string.photos_error), getString(R.string.national_id_back_side))
        if (nidFrontSidePhotoUri == null) error = String().format(getString(R.string.photos_error), getString(R.string.national_id_front_side))

        if (!error.isNullOrEmpty()) {
            binding.root.snackbar(error)
            return
        }

        /*****************post documents and redirect to home*************************/
        val jsonObject = JsonObject()
        jsonObject.addProperty("national_id_back",encodedNidBackSidePhotoID)
        jsonObject.addProperty("national_id_front",encodedNidFrontSidePhotoID)
        jsonObject.addProperty("profile_picture",encodedProfilePhotoID)
        jsonObject.addProperty("selfie_in_business",encodedSelfieInBusinessPhotoID)


        dialogLoader?.showProgressDialog()
        var call: Call<IdDocumentResponse>? = apiRequests?.postIdDocuments(
            Constants.ACCESS_TOKEN,
            jsonObject,
            generateRequestId(),
            "saveIdDocuments"
            )
        call!!.enqueue(object : Callback<IdDocumentResponse> {
            override fun onResponse(
                call: Call<IdDocumentResponse>,
                response: Response<IdDocumentResponse>
            ) {
                if (response.isSuccessful) {
                    dialogLoader?.hideProgressDialog()
                    if (response.body()!!.status == 1) {
                        response.body()!!.message?.let { binding.root.snackbar(it) }
                        navController.navigateUsingPopUp(R.id.homeFragment, R.id.action_global_homeFragment)
                    } else {
                        response.body()!!.message?.let { binding.root.snackbar(it) }

                    }

                } else if(response.code()==401) {
                    lifecycleScope.launch { userPreferences.user?.first()?.let {
                        mViewModel.setPhoneNumber(
                            it.phoneNumber.substring(3 ))
                    } }
                    dialogLoader?.hideProgressDialog()
                    binding.root.snackbar(getString(R.string.session_expired))
                    startAuth(navController)
                }else{
                    response.body()!!.message?.let { binding.root.snackbar(it) }
                    dialogLoader?.hideProgressDialog()
                }

            }

            override fun onFailure(call: Call<IdDocumentResponse>, t: Throwable) {
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