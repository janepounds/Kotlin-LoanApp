package com.cabral.emaishapay.ui.fragments.navigation

import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.net.toUri
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope

import com.cabral.emaishapay.databinding.FragmentAccountBinding
import com.cabral.emaishapay.R
import com.cabral.emaishapay.constants.Constants
import com.cabral.emaishapay.data.enums.EnterPinType
import com.cabral.emaishapay.data.models.User
import com.cabral.emaishapay.data.models.screen.AccountExpandableLayout
import com.cabral.emaishapay.databinding.DialogLoanStatusBinding
import com.cabral.emaishapay.databinding.LoanPolicyDialogBinding
import com.cabral.emaishapay.singleton.MyApplication
import com.cabral.emaishapay.ui.activities.MainActivity
import com.cabral.emaishapay.ui.base.BaseFragment
import com.cabral.emaishapay.ui.viewModels.LoanViewModel
import com.cabral.emaishapay.utils.*
import com.pixplicity.easyprefs.library.Prefs

import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AccountFragment : BaseFragment<FragmentAccountBinding>() {

    private val mViewModel: LoanViewModel by activityViewModels()
    private lateinit var statusDialogBinding: LoanPolicyDialogBinding
    private lateinit var statusDialog: Dialog


    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentAccountBinding.inflate(inflater, container, false)

    override fun setupTheme() {
        loadAppVersion()
        binding.layoutAccountAbove.userImage.loadImage((Constants.LOAN_API_URL+ Prefs.getString("profile_pic")).toUri())

        /************get user from shared preferences********************/

        context?.let {
            mViewModel.getCurrentUser( false, it).observe(viewLifecycleOwner, { user ->
                binding.layoutAccountAbove.user=user
            })
        }
        updateAccountDetails()
        setupDialog()
    }


    override fun setupClickListeners() {
        binding.layoutAccountAbove.editBtn.setOnClickListener { updateAccountInfo()}
        binding.layoutCustomerSupport.addToggleClickListeners{
            if(binding.layoutCustomerSupport.tvText1.isChecked){
                navController.navigate(R.id.action_accountFragment_to_sendEmailFragment)

            }else if(binding.layoutCustomerSupport.tvText3.isChecked){
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:0123456789")
                startActivity(intent)
            }
        }
        binding.layoutAccountCategories.settingsCardView.setOnClickListener {navController.navigate(R.id.action_accountFragment_to_changePinFragment) }
        binding.layoutAccountCategories.faqCardView.setOnClickListener { }
        binding.layoutLoanPolicy.addToggleClickListeners {
            if(binding.layoutLoanPolicy.tvText1.isChecked){
                statusDialog.show()
            }else if(binding.layoutLoanPolicy.tvText3.isChecked){
                statusDialog.show()
            }
        }
        binding.rateApp.setOnClickListener { rateAppFun() }
        binding.shareApp.setOnClickListener { shareAppFun() }
        binding.logoutBtn.setOnClickListener { logOut() }
    }

    private fun setupDialog(){
        statusDialogBinding = LoanPolicyDialogBinding.inflate(layoutInflater, binding.root as ViewGroup, false)
        statusDialog = createFullScreenDialog(statusDialogBinding, R.drawable.slider_bg, false)

//        if(binding.layoutLoanPolicy.tvText3.isChecked){
//            statusDialogBinding.tvTitle.text = getString(R.string.recovery_policy)
//        }
        statusDialogBinding.okBtn.setOnClickListener {
            statusDialog.dismiss()

            navController.navigateUsingPopUp(R.id.accountFragment, R.id.accountFragment)
        }
    }
    private fun updateAccountInfo() {
        navController.navigate(R.id.action_accountFragment_to_updateAccountDetailsFragment)


    }
    private fun updateAccountDetails(){
        binding.layoutCustomerSupport.accountExpandableItem = getCustomerSupport()

        binding.layoutLoanPolicy.accountExpandableItem = getLoanPolicy()

    }


    private fun getCustomerSupport():AccountExpandableLayout = AccountExpandableLayout(
        title = getString(R.string.customer_support),
        tv_text_1 = getString(R.string.email),
        tv_text_2 = getString(R.string.live_chat),
        tv_text_3 = getString(R.string.toll_free),
        sub_title = getString(R.string.do_you_need_help),
        image = resources.getDrawable(R.drawable.ic_customer_support)

    )

    private fun getLoanPolicy():AccountExpandableLayout = AccountExpandableLayout(
        title = getString(R.string.loan_policy),
        tv_text_1 = getString(R.string.lending_disclosure),
        tv_text_2 = getString(R.string.loan_agreement),
        tv_text_3 = getString(R.string.recovery_policy),
        sub_title = getString(R.string.what_you_need_to_know),
        image = resources.getDrawable(R.drawable.ic_terms_and_conditions)

    )


    private fun loadAppVersion() {
        var version = "1.0"
        try {
            val packageInfo = requireActivity().packageManager.getPackageInfo(requireActivity().packageName, 0)
            version = packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        binding.appVersion = version
    }


    private fun rateAppFun() {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${requireContext().packageName}")))
        } catch (e: ActivityNotFoundException) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=${requireContext().packageName}")))
        }
    }

    private fun shareAppFun() {
        try {
            val shareAppIntent = Intent(Intent.ACTION_SEND)
            shareAppIntent.type = "text/plain"
            shareAppIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_intent_msg))
            val shareMessage = "http://play.google.com/store/apps/details?id=${requireContext().packageName}"
            shareAppIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
            startActivity(Intent.createChooser(shareAppIntent, getString(R.string.app_name)))
        } catch (e: Exception) {
            binding.root.snackbar("Something went wrong! ${e.message}")
        }
    }

    private fun logOut() {
        lifecycleScope.launch { userPreferences.clear() }
        val intent = Intent(requireActivity(), MainActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }



}