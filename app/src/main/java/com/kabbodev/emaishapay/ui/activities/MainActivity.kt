package com.kabbodev.emaishapay.ui.activities

import android.content.DialogInterface
import android.view.LayoutInflater
import androidx.navigation.ui.setupWithNavController
import com.kabbodev.emaishapay.R
import com.kabbodev.emaishapay.databinding.ActivityMainBinding
import com.kabbodev.emaishapay.ui.base.BaseActivity
import com.kabbodev.emaishapay.ui.interfaces.AlertDialogCallback
import com.kabbodev.emaishapay.utils.makeGone
import com.kabbodev.emaishapay.utils.makeVisible
import com.kabbodev.emaishapay.utils.showAlertDialog
import dagger.hilt.android.AndroidEntryPoint

private const val HOME_FRAGMENT = 1

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(), AlertDialogCallback {

    private var currentFragment = -1

    override fun getActivityBinding(inflater: LayoutInflater) = ActivityMainBinding.inflate(inflater)

    override fun setupTheme() {
    }

    override fun setupNavigation() {
        val bottomNavIds = setOf(
            R.id.homeFragment,
            R.id.myBusinessFragment,
            R.id.creditScoreFragment,
            R.id.accountFragment,
        )
        binding.navView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            val id = destination.id
            if (bottomNavIds.contains(id)) binding.navView.makeVisible() else binding.navView.makeGone()
            currentFragment = if (id == R.id.homeFragment) HOME_FRAGMENT else -1
        }
    }

    override fun onBackPressed() {
        when (currentFragment) {
            HOME_FRAGMENT -> {
                showAlertDialog(this, getString(R.string.exit), title = getString(R.string.exit_app), subTitle = getString(R.string.exit_app_body), listener = this)
                return
            }
        }
        super.onBackPressed()
    }

    override fun onNegativeButtonClick(dialog: DialogInterface) {
        dialog.dismiss()
    }

    override fun onPositiveButtonClick() {
        if (currentFragment == HOME_FRAGMENT) finishAndRemoveTask()
    }

}