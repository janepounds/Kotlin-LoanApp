package com.cabral.emaishapay.ui.interfaces

interface FirebaseCallback {
  fun onSuccessListener()
  fun onFailureListener(e: Exception)
}