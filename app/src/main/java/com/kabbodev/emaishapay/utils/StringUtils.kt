package com.kabbodev.emaishapay.utils

import java.math.BigInteger
import java.sql.Timestamp
import java.util.*

fun String.generateRequestId(): String {
    val randInt = (Random()).nextInt(10000)// Generate random integers in range 0 to 9999
    val timestamp = Timestamp(System.currentTimeMillis()).toString()
    val result = timestamp.replace("\\p{Punct}|\\s".toRegex(), "")
    val formattedRandInt = String.format("%021d", BigInteger(result + randInt))
    return "E$formattedRandInt"
}