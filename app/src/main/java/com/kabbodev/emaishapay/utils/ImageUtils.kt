package com.kabbodev.emaishapay.utils

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions

fun ImageView.loadCircleImage(imageSrc: Any) {
    val requestOptions = RequestOptions().circleCrop()
    loadImage(imageSrc, requestOptions)
}

fun ImageView.loadImage(imageSrc: Any) {
    val requestOptions = RequestOptions().fitCenter()
    loadImage(imageSrc, requestOptions)
}

fun ImageView.loadImage(imageSrc: Any, requestOptions: RequestOptions) {
    Glide
        .with(this.context)
        .load(imageSrc)
        .transition(DrawableTransitionOptions.withCrossFade())
        .apply(requestOptions)
        .into(this)
}