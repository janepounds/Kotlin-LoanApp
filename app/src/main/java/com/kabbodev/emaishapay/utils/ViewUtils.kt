package com.kabbodev.emaishapay.utils

import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AutoCompleteTextView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.findViewTreeLifecycleOwner
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.kabbodev.emaishapay.R
import com.kabbodev.emaishapay.databinding.LayoutBusinessExpandableBinding
import com.kabbodev.emaishapay.databinding.LayoutUploadDocumentBinding
import com.skydoves.powerspinner.PowerSpinnerView

fun View.snackbar(message: String) {
    Snackbar
        .make(this, message, Snackbar.LENGTH_LONG)
        .also { snackbar ->
            snackbar.setAction("Ok") {
                snackbar.dismiss()
            }
            snackbar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text).isSingleLine = false
        }.show()
}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(this.windowToken, 0)
}

fun TextInputLayout.showErrorOnEditText(errorMessage: String) {
    this.isErrorEnabled = true
    this.error = errorMessage
}

fun TextInputLayout.addEndIconClickListener() {
    var isPasswordVisible = false
    this.setEndIconOnClickListener {
        if (isPasswordVisible) {
            isPasswordVisible = false
            this.editText!!.transformationMethod = AsteriskPasswordTransformationMethod()
        } else {
            isPasswordVisible = true
            this.editText!!.transformationMethod = HideReturnsTransformationMethod()
        }
    }
}

fun LayoutBusinessExpandableBinding.addToggleClickListeners(callback: () -> Unit) {
    val context = this.root.context
    this.clickToExpandLayout.setOnClickListener {
        this.expandableLayout.toggle()
        if (this.expandableLayout.isExpanded) {
            this.expandIcon.loadImage(R.drawable.ic_up_arrow)
            this.tvCollapseOrExpand.text = context.getString(R.string.click_to_collapse_tab)
        } else {
            this.expandIcon.loadImage(R.drawable.ic_down_arrow)
            this.tvCollapseOrExpand.text = context.getString(R.string.click_to_expand_tab)
        }
    }
    this.editBtn.setOnClickListener { callback() }
}

fun PowerSpinnerView.initSpinner(lifecycleOwner: LifecycleOwner) {
    this.lifecycleOwner = lifecycleOwner
    this.setOnSpinnerOutsideTouchListener { _, _ -> this.dismiss() }
}

fun AutoCompleteTextView.initAutoCompleteTextView(textView: AutoCompleteTextView){
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable){
            textView.showDropDown()
        }} )
}

fun LayoutUploadDocumentBinding.updatePhotoLayout(selectedUri: Uri?) {
    this.uploadedPhoto = selectedUri != null
    this.uploadImage.loadImage(selectedUri!!)
}

fun MaterialButton.enableButton() {
    this.isEnabled = true
    this.setTextColor(ContextCompat.getColor(this.context, R.color.white))
}

fun MaterialButton.disableButton() {
    this.isEnabled = false
    this.setTextColor(Color.argb(70, 255, 255, 255))
}

fun View.makeVisible() {
    this.visibility = View.VISIBLE
}

fun View.makeInvisible() {
    this.visibility = View.INVISIBLE
}

fun View.makeGone() {
    this.visibility = View.GONE
}

fun getHomeViewPagerHtmlText(): String = "You can now borrow up to <font color=\"#179bd7\">Ugx 700,000</font> Repay in instalments of Daily, Weekly or Monthly"

fun getPayBackText(payBackAmount: String, type: String, typeAmount: String, interestRate: Int): String =
    "You will pay back <font color=\"#179bd7\">UGX $payBackAmount</font> in total with interest of ${interestRate}% and $type payment is <font color=\"#179bd7\">UGX $typeAmount</font>."

