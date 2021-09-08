package com.kabbodev.emaishapay.utils

import android.text.method.LinkMovementMethod
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView
import com.kabbodev.emaishapay.R
import com.kabbodev.emaishapay.data.config.Config
import com.kabbodev.emaishapay.data.enums.LoanStatus
import com.kabbodev.emaishapay.utils.TopSpacingItemDecoration
import com.kabbodev.emaishapay.utils.loadImage

@BindingAdapter("imageSrc")
fun ImageView.loadImageSrc(imageSrc: Any?) {
    imageSrc?.let { this.loadImage(imageSrc) }
}

@BindingAdapter("circleImageSrc")
fun ImageView.loadCircleImageSrc(imageSrc: Any?) {
    imageSrc?.let { this.loadCircleImage(imageSrc) }
}

@BindingAdapter("setAdapter")
fun RecyclerView.bindRecyclerViewAdapter(recyclerViewAdapter: RecyclerView.Adapter<*>) {
    this.apply {
        val topSpacingItemDecoration = TopSpacingItemDecoration(30)
        addItemDecoration(topSpacingItemDecoration)
        adapter = recyclerViewAdapter
    }
}

@BindingAdapter("htmlString")
fun MaterialTextView.setHtmlText(text: String) {
    this.text = spannedFromHtml(text)
}

@BindingAdapter("prepareTermsAndConditions")
fun MaterialTextView.prepareTermsAndConditionsText(terms: Boolean) {
    val htmlText = "I have read and agree with the <a href=\"${Config.TERMS_AND_CONDITIONS_URL}\">Terms and Conditions</a> of using ${this.context.getString(R.string.app_name)}"
    this.text = spannedFromHtml(htmlText)
    this.isClickable = true
    this.movementMethod = LinkMovementMethod.getInstance()
}

@BindingAdapter("setOtpVerifySubtitleText")
fun MaterialTextView.setOtpVerifySubtitleText(phoneNumber: String) {
    val htmlText = "Enter 4-digit code we have sent to <u><font color=\"#179bd7\">+256 $phoneNumber</font></u>"
    this.text = spannedFromHtml(htmlText)
}

@BindingAdapter("progressBackgroundTint")
fun MaterialTextView.setProgressBackgroundTint(isCurrentItem: Boolean) {
    this.backgroundTintList = ContextCompat.getColorStateList(this.context, if (isCurrentItem) R.color.primaryColor else R.color.grey_3)
}

@BindingAdapter("progressTextColor")
fun MaterialTextView.setProgressTextColor(isCurrentItem: Boolean) {
    this.setTextColor(ContextCompat.getColorStateList(this.context, if (isCurrentItem) R.color.primaryColor else R.color.grey_3))
}

@BindingAdapter("statusText")
fun MaterialTextView.setStatusText(loanStatus: LoanStatus) {
    this.text = loanStatus.status
    when (loanStatus) {
        LoanStatus.APPROVED -> {
            this.setTextColor(ContextCompat.getColorStateList(this.context, R.color.green))
            this.setBackgroundResource(R.drawable.slider_bg_with_stroke_7)
        }
        LoanStatus.PAID -> {
            this.setTextColor(ContextCompat.getColorStateList(this.context, R.color.green))
            this.setBackgroundResource(R.drawable.slider_bg_with_stroke_7)
        }
        LoanStatus.PENDING -> {
            this.setTextColor(ContextCompat.getColorStateList(this.context, R.color.primaryColor))
            this.setBackgroundResource(R.drawable.slider_bg_with_stroke_2)
        }
        LoanStatus.REJECTED -> {
            this.setTextColor(ContextCompat.getColorStateList(this.context, R.color.red))
            this.setBackgroundResource(R.drawable.slider_bg_with_stroke_6)
        }
    }
}

@BindingAdapter("isCardViewSelected")
fun MaterialCardView.setCardViewBackgroundTint(isSelected: Boolean) {
    this.setCardBackgroundColor(ContextCompat.getColorStateList(this.context, if (isSelected) R.color.primaryColor else R.color.white))
}

@BindingAdapter("isImageSelected")
fun ImageView.setImageViewBackgroundTint(isSelected: Boolean) {
    this.backgroundTintList = ContextCompat.getColorStateList(this.context, if (isSelected) R.color.white else R.color.primaryColor)
    this.imageTintList = ContextCompat.getColorStateList(this.context, if (isSelected) R.color.white else R.color.primaryColor)
}

@BindingAdapter("isTextViewSelected")
fun MaterialTextView.setTextViewBackgroundTint(isSelected: Boolean) {
    this.setTextColor(ContextCompat.getColorStateList(this.context, if (isSelected) R.color.white else R.color.primaryColor))
}


@BindingAdapter("agreementText")
fun MaterialTextView.prepareAgreementText(agreement: Boolean) {
    val loanDisclosureText = "<a href=\"${Config.LOAN_DISCLOSURE_URL}\">Loan Disclosure</a>"
    val loanAgreementText = "<a href=\"${Config.LOAN_AGREEMENT_URL}\">Loan Agreement</a>"
    val recoveryPolicyText = "<a href=\"${Config.RECOVERY_POLICY_URL}\">Recovery Policy</a>"
    val htmlText = "I have received and agreed to the $loanDisclosureText, $loanAgreementText and $recoveryPolicyText provided."
    this.text = spannedFromHtml(htmlText)
    this.isClickable = true
    this.movementMethod = LinkMovementMethod.getInstance()
}

@BindingAdapter("imageTintColor")
fun ImageView.setImageTint(color: Int) {
    this.imageTintList = ContextCompat.getColorStateList(this.context, color)
}