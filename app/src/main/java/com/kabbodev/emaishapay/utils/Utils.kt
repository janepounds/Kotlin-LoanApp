package com.kabbodev.emaishapay.utils

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.Spanned
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import com.kabbodev.emaishapay.R
import com.kabbodev.emaishapay.data.config.Config
import com.kabbodev.emaishapay.data.enums.EnterPinType
import com.kabbodev.emaishapay.singleton.MyApplication

fun NavController.navigateUsingPopUp(popUpFragId: Int, destinationId: Int, args: Bundle? = null) {
    val navOptions = NavOptions.Builder()
        .setEnterAnim(R.anim.slide_in_right)
        .setExitAnim(R.anim.slide_out_left)
        .setPopEnterAnim(R.anim.slide_in_left)
        .setPopExitAnim(R.anim.slide_out_right)
        .setPopUpTo(popUpFragId, true)
        .build()
    navigate(destinationId, args, navOptions)
}

fun Context.hasPermissions(permission: String?): Boolean {
    return ContextCompat.checkSelfPermission(this, permission!!) == PackageManager.PERMISSION_GRANTED
}

fun spannedFromHtml(text: String): Spanned? {
    return if (Build.VERSION.SDK_INT >= 24) {
        Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY)
    } else {
        @Suppress("DEPRECATION")
        Html.fromHtml(text)
    }
}

fun String.isPhoneNumberValid(): String? {
    val context = MyApplication.getAppContext()
    if (this.isEmpty()) return context.getString(R.string.phone_cannot_be_empty)
    if (this.length != 9) return context.getString(R.string.invalid_phone_number)
    return null
}

fun startAuth( navController: NavController){

    //call fragment
    if (navController.currentDestination!!.id !== R.id.enterPinFragment
    ) {
        navController.popBackStack(R.id.homeFragment, false)
        navController.navigate(R.id.action_homeFragment_to_enterPinFragment,
            bundleOf(Config.LOGIN_TYPE to EnterPinType.LOGIN)
        )
    }
}
