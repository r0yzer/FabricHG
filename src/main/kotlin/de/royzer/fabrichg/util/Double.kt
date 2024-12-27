package de.royzer.fabrichg.util

import kotlin.math.pow

fun Double.round(digits: Int): Double {
    require(digits >= 0) { "ich bin UP" }
    val factor = 10.0.pow(digits)
    return kotlin.math.round(this * factor) / factor
}

fun Double.toStringWithoutTrailing0s(): String {
    // gibts da ne java methode

    val str = toString()

    return if (str.endsWith(".00")) str.substring(0, str.length-4)
    else str
}
