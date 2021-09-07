package com.kabbodev.emaishapay.ui.fragments

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.kabbodev.emaishapay.R
import com.kabbodev.emaishapay.databinding.FragmentSplashBinding
import com.kabbodev.emaishapay.ui.base.BaseFragment
import com.kabbodev.emaishapay.utils.navigateUsingPopUp
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class SplashFragment : BaseFragment<FragmentSplashBinding>() {

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentSplashBinding.inflate(inflater, container, false)

    override fun setupTheme() {}

    override fun setupClickListeners() {}

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch(Dispatchers.IO) {
            val isLoggedIn = userPreferences.isLoggedIn.first()
            delay(1500)

            withContext(Dispatchers.Main) {
                if (isLoggedIn == true) {
                    /************check if token expired*******************/

                    navController.navigateUsingPopUp(R.id.splashFragment, R.id.action_global_homeFragment)

                } else {
                    val showIntro = userPreferences.showIntro.first()
                    if (showIntro == false) {
                        navController.navigateUsingPopUp(R.id.splashFragment, R.id.action_global_welcomeFragment)
                    } else {
                        navController.navigate(R.id.action_splashFragment_to_introFragment)
                    }
                }
            }
        }
    }
}