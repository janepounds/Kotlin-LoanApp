package com.cabral.emaishapay.ui.fragments.navigation

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.cabral.emaishapay.databinding.FragmentAccountBinding
import com.cabral.emaishapay.R
import com.cabral.emaishapay.ui.activities.MainActivity
import com.cabral.emaishapay.ui.base.BaseFragment
import com.cabral.emaishapay.ui.viewModels.LoanViewModel
import com.cabral.emaishapay.utils.snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AccountFragment : BaseFragment<FragmentAccountBinding>() {

    private val mViewModel: LoanViewModel by activityViewModels()


    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentAccountBinding.inflate(inflater, container, false)

    override fun setupTheme() {
        loadAppVersion()

        /************get user from shared preferences********************/

        context?.let {
            mViewModel.getCurrentUser( false, it).observe(viewLifecycleOwner, { user ->
                binding.layoutAccountAbove.user=user
            })
        }


    }


    override fun setupClickListeners() {
        binding.layoutAccountAbove.editBtn.setOnClickListener { }
        binding.layoutAccountCategories.customerSupportCardView.setOnClickListener{}
        binding.layoutAccountCategories.settingsCardView.setOnClickListener { }
        binding.layoutAccountCategories.faqCardView.setOnClickListener { }
        binding.layoutAccountCategories.loanPolicyCardView.setOnClickListener { }
        binding.rateApp.setOnClickListener { rateAppFun() }
        binding.shareApp.setOnClickListener { shareAppFun() }
        binding.logoutBtn.setOnClickListener { logOut() }
    }

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