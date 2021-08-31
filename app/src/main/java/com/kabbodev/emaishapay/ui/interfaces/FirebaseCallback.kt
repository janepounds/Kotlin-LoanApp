package com.kabbodev.emaishapay.ui.interfaces

interface FirebaseCallback {
  fun onSuccessListener()
  fun onFailureListener(e: Exception)
}