package com.kabbodev.emaishapay.ui.fragments.businessInfo.businessProfile

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
import com.kabbodev.emaishapay.data.models.responses.IdDocumentData
import com.kabbodev.emaishapay.data.models.responses.VerificationDocumentsData
import com.kabbodev.emaishapay.data.models.responses.VerificationDocumentsResponse
import com.kabbodev.emaishapay.databinding.FragmentVerificationDocumentsBinding
import com.kabbodev.emaishapay.network.ApiClient
import com.kabbodev.emaishapay.network.ApiRequests
import com.kabbodev.emaishapay.ui.activities.ImagePickerActivity
import com.kabbodev.emaishapay.ui.base.BaseFragment
import com.kabbodev.emaishapay.ui.viewModels.LoginViewModel
import com.kabbodev.emaishapay.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
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
class VerificationDocumentsFragment : BaseFragment<FragmentVerificationDocumentsBinding>() {

    private val mViewModel: LoginViewModel by activityViewModels()
    private var mode: Int = -1
    private var officeShopPhotoUri: Uri? = null
    private var officeShopVideoUri: Uri? = null
    private var selfieShopOfficePhotoUri: Uri? = null
    private var neighbourhoodPhotoUri: Uri? = null
    private var utilityBillPhotoUri: Uri? = null
    private val apiRequests: ApiRequests? by lazy { ApiClient.getLoanInstance() }
    private var dialogLoader: DialogLoader? = null
    private var encodedOfficeShopPhotoID: String? =null
    private var encodedOfficeShopVideoID: String? =null
    private var encodedSelfieShopOfficePhoto: String? =null
    private var encodedNeighbourhoodPhoto: String? =null
    private var encodedUtilityBillPhoto: String? =null

    private val photosLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
        val data = activityResult.data
        if (activityResult.resultCode == Activity.RESULT_OK) {
            val result = data?.getStringExtra("result")
            Timber.d("Result $result")
            if (result != null) {
                when (mode) {
                    1 -> {
                        officeShopPhotoUri = result.toUri()
                        encodedOfficeShopPhotoID = encodeSelectedImage(getRealPathFromUri(requireContext(),officeShopPhotoUri)!!)
                        binding.officeShopPhoto.updatePhotoLayout(officeShopPhotoUri)
                    }
                    2 -> {
                        officeShopVideoUri = result.toUri()
                        encodedOfficeShopVideoID = encodeSelectedImage(getRealPathFromUri(requireContext(),officeShopVideoUri)!!)
                        binding.officeShopVideo.updatePhotoLayout(officeShopVideoUri)
                    }
                    3 -> {
                        selfieShopOfficePhotoUri = result.toUri()
                        encodedSelfieShopOfficePhoto = encodeSelectedImage(getRealPathFromUri(requireContext(),selfieShopOfficePhotoUri)!!)
                        binding.selfieShopOffice.updatePhotoLayout(selfieShopOfficePhotoUri)
                    }
                    4 -> {
                        neighbourhoodPhotoUri = result.toUri()
                        encodedNeighbourhoodPhoto = encodeSelectedImage(getRealPathFromUri(requireContext(),neighbourhoodPhotoUri)!!)
                        binding.neighbourhoodPhoto.updatePhotoLayout(neighbourhoodPhotoUri)
                    }
                    5 -> {
                        utilityBillPhotoUri = result.toUri()
                        encodedUtilityBillPhoto = encodeSelectedImage(getRealPathFromUri(requireContext(),utilityBillPhotoUri)!!)
                        binding.utilityBill.updatePhotoLayout(utilityBillPhotoUri)
                    }
                }
            }
        }
    }


    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentVerificationDocumentsBinding.inflate(inflater, container, false)

    override fun setupTheme() {
        loadVerificationDocuments()
    }

    private fun loadVerificationDocuments(){
        dialogLoader = context?.let { DialogLoader(it) }
        dialogLoader?.showProgressDialog()
        var call: Call<VerificationDocumentsResponse>? = apiRequests?.getVerificationDocuments(
            Constants.ACCESS_TOKEN,
            generateRequestId(),
            "getVerificationDocs"
        )
        call!!.enqueue(object : Callback<VerificationDocumentsResponse> {
            override fun onResponse(
                call: Call<VerificationDocumentsResponse>,
                response: Response<VerificationDocumentsResponse>
            ) {
                if (response.isSuccessful) {
                    dialogLoader?.hideProgressDialog()

                    if (response.body()!!.status == 1) {
                        /************populate all fields in UI*****************/
                        binding.officeShopPhoto.updatePhotoLayout((Constants.LOAN_API_URL+response.body()!!.data?.business_photo).toUri())
                        binding.officeShopVideo.updatePhotoLayout((Constants.LOAN_API_URL+response.body()!!.data?.business_video).toUri())
                        binding.selfieShopOffice.updatePhotoLayout((Constants.LOAN_API_URL+response.body()!!.data?.selfie_in_business).toUri())
                        binding.neighbourhoodPhoto.updatePhotoLayout((Constants.LOAN_API_URL+response.body()!!.data?.neighbourhood_photo).toUri())
                        binding.utilityBill.updatePhotoLayout((Constants.LOAN_API_URL+response.body()!!.data?.utility_bill).toUri())
                        response.body()!!.message?.let { binding.root.snackbar(it) }


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

            override fun onFailure(call: Call<VerificationDocumentsResponse>, t: Throwable) {
                t.message?.let { binding.root.snackbar(it) }
                dialogLoader?.hideProgressDialog()


            }
        })
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

        val requestBody: RequestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("business_photo", encodedOfficeShopPhotoID!!)
            .addFormDataPart("business_video", encodedOfficeShopVideoID!!)
            .addFormDataPart("selfie_in_business", encodedSelfieShopOfficePhoto!!)
            .addFormDataPart("neighbourhood_photo", encodedNeighbourhoodPhoto!!)
            .addFormDataPart("utility_bill", encodedUtilityBillPhoto!!)
            .build()
        dialogLoader?.showProgressDialog()
        var call: Call<VerificationDocumentsResponse>? = apiRequests?.postVerificationDocuments(
            Constants.ACCESS_TOKEN,
            "application/x-www-form-urlencoded",
            requestBody,
            generateRequestId(),
            "saveVerificationDocs"
        )
        call!!.enqueue(object : Callback<VerificationDocumentsResponse> {
            override fun onResponse(
                call: Call<VerificationDocumentsResponse>,
                response: Response<VerificationDocumentsResponse>
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

            override fun onFailure(call: Call<VerificationDocumentsResponse>, t: Throwable) {
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