package de.royzer.fabrichg.settings

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable


@Serializable
data class GameSettings @OptIn(ExperimentalSerializationApi::class) constructor(
    @EncodeDefault
    var minifeastEnabled: Boolean = true,
    @EncodeDefault
    var mushroomCowNerf: Boolean = true,
    @EncodeDefault
    var kitAmount: Int = 1,
)