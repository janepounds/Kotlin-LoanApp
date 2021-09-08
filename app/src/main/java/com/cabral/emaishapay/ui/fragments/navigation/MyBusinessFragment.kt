package com.cabral.emaishapay.ui.fragments.navigation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.cabral.emaishapay.R
import com.cabral.emaishapay.data.models.User
import com.cabral.emaishapay.data.models.screen.BusinessExpandableLayout
import com.cabral.emaishapay.databinding.FragmentMyBusinessBinding
import com.cabral.emaishapay.ui.base.BaseFragment
import com.cabral.emaishapay.ui.viewModels.LoanViewModel
import com.cabral.emaishapay.utils.addToggleClickListeners
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyBusinessFragment : BaseFragment<FragmentMyBusinessBinding>() {

    private val mViewModel: LoanViewModel by activityViewModels()



    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentMyBusinessBinding.inflate(inflater, container, false)

    override fun setupTheme() {
        updateBusinessLayoutValues(null)
        /************get user from shared preferences********************/

            context?.let {
                mViewModel.getCurrentUser( false, it).observe(viewLifecycleOwner, { user ->
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

    private fun updateBusinessLayoutValues(user: User?) {
        binding.layoutOwnerProfile.businessExpandableItem = getOwnerProfileItem(
            valueOne = user?.fullName ?: "",
            valueTwo = user?.dateOfBirth ?: "",
            valueThree = user?.nin ?:""
        )
        binding.layoutBusinessProfile.businessExpandableItem = getBusinessProfileItem(
            valueOne = user?.fullName ?: "",
            valueTwo = user?.regDate ?: "",
            valueThree = user?.location ?: ""
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