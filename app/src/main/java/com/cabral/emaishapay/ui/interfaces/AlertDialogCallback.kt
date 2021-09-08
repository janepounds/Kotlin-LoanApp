package com.cabral.emaishapay.ui.interfaces

import android.content.DialogInterface

interface AlertDialogCallback {
  fun onNegativeButtonClick(dialog: DialogInterface)
  fun onPositiveButtonClick()
}