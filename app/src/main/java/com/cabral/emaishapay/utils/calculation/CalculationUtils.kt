package com.cabral.emaishapay.utils.calculation

import kotlin.math.roundToInt

class CalculationUtils {

    companion object {
        fun calculateLoanDueAmount(amount: Long, interestRate: Int, processingFee: Long): Long = amount + (((interestRate.toDouble() / 100)) * amount).roundToInt() + processingFee
    }

}