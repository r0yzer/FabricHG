package de.royzer.fabrichg.util

import java.math.BigDecimal
import kotlin.math.pow

fun Double.round(digits: Int): Double {
    require(digits >= 0) { "ich bin UP" }
    val factor = 10.0.pow(digits)
    return kotlin.math.round(this * factor) / factor
}

fun Double.toStringWithoutTrailing0s(): String {
    return BigDecimal(this).stripTrailingZeros().toPlainString()
}
