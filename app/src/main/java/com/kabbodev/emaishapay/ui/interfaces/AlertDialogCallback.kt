package com.kabbodev.emaishapay.ui.interfaces

import android.content.DialogInterface

interface AlertDialogCallback {
  fun onNegativeButtonClick(dialog: DialogInterface)
  fun onPositiveButtonClick()
}