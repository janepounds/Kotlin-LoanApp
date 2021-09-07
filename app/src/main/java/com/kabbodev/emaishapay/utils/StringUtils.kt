package com.kabbodev.emaishapay.utils

import android.content.Context
import android.graphics.Bitmap
import android.util.Base64
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import com.kabbodev.emaishapay.R
import com.kabbodev.emaishapay.data.config.Config
import com.kabbodev.emaishapay.data.enums.EnterPinType
import java.io.ByteArrayOutputStream
import java.math.BigInteger
import java.sql.Timestamp
import java.util.*

fun generateRequestId(): String {
    val randInt = (Random()).nextInt(10000)// Generate random integers in range 0 to 9999
    val timestamp = Timestamp(System.currentTimeMillis()).toString()
    val result = timestamp.replace("\\p{Punct}|\\s".toRegex(), "")
    val formattedRandInt = String.format("%021d", BigInteger(result + randInt))
    return "E$formattedRandInt"
}


fun selectSpinnerItemByValue(spnr: Spinner, value: String?) {
    if (value == null) return
    val adapter = spnr.adapter as ArrayAdapter<*>
    for (position in 1 until adapter.count) {
        val item = spnr.adapter.getItem(position).toString()
        if ((item.equals(value, ignoreCase = true))) {
            spnr.setSelection(position)
            return
        }
    }


}