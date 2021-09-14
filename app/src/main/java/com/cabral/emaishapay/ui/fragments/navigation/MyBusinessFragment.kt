package com.cabral.emaishapay.ui.fragments.navigation

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.cabral.emaishapay.R
import com.cabral.emaishapay.constants.Constants
import com.cabral.emaishapay.data.models.User
import com.cabral.emaishapay.data.models.screen.BusinessExpandableLayout
import com.cabral.emaishapay.databinding.FragmentMyBusinessBinding
import com.cabral.emaishapay.ui.base.BaseFragment
import com.cabral.emaishapay.ui.viewModels.LoanViewModel
import com.cabral.emaishapay.utils.addToggleClickListeners
import com.cabral.emaishapay.utils.loadImage
import com.cabral.emaishapay.utils.updatePhotoLayout
import com.pixplicity.easyprefs.library.Prefs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.NullPointerException

@AndroidEntryPoint
class MyBusinessFragment : BaseFragment<FragmentMyBusinessBinding>() {
    private  val TAG = "MyBusinessFragment"

    private val mViewModel: LoanViewModel by activityViewModels()
    private var dob:String =""
    private var nin:String =""
    private var regDate:String =""
    private var location:String =""



    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentMyBusinessBinding.inflate(inflater, container, false)

    override fun setupTheme() {
        updateBusinessLayoutValues(null)
        binding.userImage.loadImage((Constants.LOAN_API_URL+Prefs.getString("profile_pic")).toUri())

        /***********get user from shared preferences********************/
                context?.let {
                    mViewModel.getCurrentUser(false, it).observe(viewLifecycleOwner, { user ->
                        binding.user = user
                        updateBusinessLayoutValues(user)
                    })
                }

    }



    override fun setupClickListeners() {
        binding.layoutOwnerProfile.addToggleClickListeners {
            navController.navigate(R.id.action_myBusinessFragment_to_enterPersonalDetailsFragment)
        }
        binding.layoutBusinessProfile.addToggleClickListeners {
            navController.navigate(R.id.action_myBusinessFragment_to_enterBusinessDetailsFragment)
        }
        binding.layoutBusinessDocuments.addToggleClickListeners {
            navController.navigate(R.id.action_myBusinessFragment_to_uploadBusinessDocumentsFragment)
        }
    }

    private  fun updateBusinessLayoutValues(user: User?) {
        binding.layoutOwnerProfile.businessExpandableItem = getOwnerProfileItem(
            valueOne = user?.fullName ?: "",
            valueTwo = Prefs.getString("dob"),
            valueThree = Prefs.getString("nin")
        )
        binding.layoutBusinessProfile.businessExpandableItem = getBusinessProfileItem(
            valueOne = Prefs.getString("biz_name"),
            valueTwo = Prefs.getString("date_registered"),
            valueThree = Prefs.getString("location")
        )
        binding.layoutBusinessDocuments.businessExpandableItem = getBusinessDocumentsItem(
            valueOne = getString(R.string.not_uploaded),
            valueTwo = getString(R.string.not_uploaded),
            valueThree = getString(R.string.not_uploaded)
        )
    }

    private fun getOwnerProfileItem(valueOne: String, valueTwo: String, valueThree: String): BusinessExpandableLayout = BusinessExpandableLayout(
        title = getString(R.string.owner_profile),
        tv_text_1 = getString(R.string.name),
        tv_text_2 = getString(R.string.dob),
        tv_text_3 = getString(R.string.nin),
        value_text_1 = valueOne,
        value_text_2 = valueTwo,
        value_text_3 = valueThree
    )

    private fun getBusinessProfileItem(valueOne: String, valueTwo: String, valueThree: String): BusinessExpandableLayout = BusinessExpandableLayout(
        title = getString(R.string.business_profile),
        tv_text_1 = getString(R.string.name),
        tv_text_2 = getString(R.string.reg_date),
        tv_text_3 = getString(R.string.location),
        value_text_1 = valueOne,
        value_text_2 = valueTwo,
        value_text_3 = valueThree
    )

    private fun getBusinessDocumentsItem(valueOne: String, valueTwo: String, valueThree: String): BusinessExpandableLayout = BusinessExpandableLayout(
        title = getString(R.string.business_documents),
        tv_text_1 = getString(R.string.trade_license),
        tv_text_2 = getString(R.string.reg_certificate),
        tv_text_3 = getString(R.string.tax_certificate),
        value_text_1 = valueOne,
        value_text_2 = valueTwo,
        value_text_3 = valueThree
    )

}