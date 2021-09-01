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
                        encodedOfficeShopPhotoID = encodeSelectedImage(BitmapFactory.decodeFile(result))
                        officeShopPhotoUri = result.toUri()
                        binding.officeShopPhoto.updatePhotoLayout(officeShopPhotoUri)
                    }
                    2 -> {
                        encodedOfficeShopVideoID = encodeSelectedImage(BitmapFactory.decodeFile(result))
                        officeShopVideoUri = result.toUri()
                        binding.officeShopVideo.updatePhotoLayout(officeShopVideoUri)
                    }
                    3 -> {
                        encodedSelfieShopOfficePhoto = encodeSelectedImage(BitmapFactory.decodeFile(result))
                        selfieShopOfficePhotoUri = result.toUri()
                        binding.selfieShopOffice.updatePhotoLayout(selfieShopOfficePhotoUri)
                    }
                    4 -> {
                        encodedNeighbourhoodPhoto = encodeSelectedImage(BitmapFactory.decodeFile(result))
                        neighbourhoodPhotoUri = result.toUri()
                        binding.neighbourhoodPhoto.updatePhotoLayout(neighbourhoodPhotoUri)
                    }
                    5 -> {
                        encodedUtilityBillPhoto = encodeSelectedImage(BitmapFactory.decodeFile(result))
                        utilityBillPhotoUri = result.toUri()
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

        val requestObject = JSONObject()
        val data = VerificationDocumentsData(
           business_photo = encodedOfficeShopPhotoID!!,
            business_video = encodedOfficeShopVideoID!!,
            selfie_in_business = encodedSelfieShopOfficePhoto!!,
            neighbourhood_photo = encodedNeighbourhoodPhoto!!,
            utility_bill = encodedUtilityBillPhoto!!
        )
        requestObject.put("params",data)
        dialogLoader?.showProgressDialog()
        var call: Call<VerificationDocumentsResponse>? = apiRequests?.postVerificationDocuments(
            Constants.ACCESS_TOKEN,
            requestObject,
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