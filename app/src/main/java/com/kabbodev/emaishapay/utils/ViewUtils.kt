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
import android.text.InputType

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener

import android.widget.EditText
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*


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

fun addDatePicker(editText: EditText, ed_: EditText, context: Context?) {
    editText.setOnClickListener{
        val mcurrentDate = Calendar.getInstance()
        val mYear = mcurrentDate[Calendar.YEAR]
        val mMonth = mcurrentDate[Calendar.MONTH]
        val mDay = mcurrentDate[Calendar.DAY_OF_MONTH]
        val mDatePicker = DatePickerDialog(
            context!!,
            { date_picker, selected_year, selected_month, selected_day ->
                val month = selected_month + 1
                val formatter: NumberFormat = DecimalFormat("00")
                ed_.setText(
                    selected_year.toString() + "-" + formatter.format(month.toLong()) + "-" + formatter.format(
                        selected_day.toLong()
                    )
                )
            }, mYear, mMonth, mDay
        )
        mDatePicker.show()
    }
    ed_.inputType = InputType.TYPE_NULL
}





fun getHomeViewPagerHtmlText(): String = "You can now borrow up to <font color=\"#179bd7\">Ugx 700,000</font> Repay in instalments of Daily, Weekly or Monthly"

fun getPayBackText(payBackAmount: String, type: String, typeAmount: String, interestRate: Int): String =
    "You will pay back <font color=\"#179bd7\">UGX $payBackAmount</font> in total with interest of ${interestRate}% and $type payment is <font color=\"#179bd7\">UGX $typeAmount</font>."

