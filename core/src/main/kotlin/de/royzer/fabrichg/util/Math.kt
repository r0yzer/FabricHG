package de.royzer.fabrichg.util

fun lerp(start: Float, end: Float, t: Float): Float {
    return start + t * (end - start)
}