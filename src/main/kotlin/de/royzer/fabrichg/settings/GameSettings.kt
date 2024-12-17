package de.royzer.fabrichg.settings

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable


@Serializable
data class GameSettings @OptIn(ExperimentalSerializationApi::class) constructor(
    @EncodeDefault
    var maxIngameTime: Int = 30 * 60,
    @EncodeDefault
    var minifeastEnabled: Boolean = true,
    @EncodeDefault
    var mushroomCowNerf: Boolean = true,
    @EncodeDefault
    var kitAmount: Int = 1,
    @EncodeDefault
    var pitEnabled: Boolean = false,
    @EncodeDefault
    var pitStartTimeBeforeEnd: Int = 10 * 60,
) {
    override fun toString(): String {
        return "GameSettings(maxIngameTime=$maxIngameTime, minifeastEnabled=$minifeastEnabled, mushroomCowNerf=$mushroomCowNerf, kitAmount=$kitAmount, pitEnabled=$pitEnabled, pitStartTimeBeforeEnd=$pitStartTimeBeforeEnd)"
    }
}