package de.royzer.fabrichg.settings

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable


@Serializable
data class GameSettings @OptIn(ExperimentalSerializationApi::class) constructor(
    @EncodeDefault
    var minPlayers: Int = 2,
    @EncodeDefault
    var maxIngameTime: Int = 30 * 60,
    @EncodeDefault
    var feastStartTime: Int = 10 * 60,
    @EncodeDefault
    var minifeastEnabled: Boolean = true,
    @EncodeDefault
    var mushroomCowNerf: Boolean = true,
    @EncodeDefault
    var kitAmount: Int = 1,
    @EncodeDefault
    var pitEnabled: Boolean = false,
    @EncodeDefault
    var pitStartTime: Int = 45 * 60,
    @EncodeDefault
    var gulagEnabled: Boolean = true,
    @EncodeDefault
    var achievementsEnabled: Boolean = true,
    @EncodeDefault
    var gulagEndTime: Int = 10 * 60,
    @EncodeDefault
    var minPlayersOutsideGulag: Int = 3,
    @EncodeDefault
    var critDamage: Float = 1.25f,
    @EncodeDefault
    var maxRecraftBeforeFeast: Int = 96,
    @EncodeDefault
    var surpriseOnlyEnabledKits: Boolean = true,
    @EncodeDefault
    var teamsEnabled: Boolean = false,
    @EncodeDefault
    var teamSize: Int = 2,
    @EncodeDefault
    var invincibilityTime: Int = 60 * 2,
    @EncodeDefault
    var friendlyFire: Boolean = false,
    @EncodeDefault
    var forbiddenKitCombinations: List<List<String>> = listOf(
        listOf("Anchor", "Urgal"), listOf("Gladiator", "Urgal"), listOf("Grappler", "Stomper"), listOf("Phantom", "Stomper"), listOf("Kangaroo", "Blink", "Phantom")
    )
) {
    override fun toString(): String {
        return "GameSettings(minPlayers=$minPlayers, maxIngameTime=$maxIngameTime, feastStartTime=$feastStartTime, minifeastEnabled=$minifeastEnabled, mushroomCowNerf=$mushroomCowNerf, kitAmount=$kitAmount, pitEnabled=$pitEnabled, pitStartTime=$pitStartTime, gulagEnabled=$gulagEnabled, achievementsEnabled=$achievementsEnabled, gulagEndTime=$gulagEndTime, minPlayersOutsideGulag=$minPlayersOutsideGulag, critDamage=$critDamage, maxRecraftBeforeFeast=$maxRecraftBeforeFeast, surpriseOnlyEnabledKits=$surpriseOnlyEnabledKits, teamsEnabled=$teamsEnabled, teamSize=$teamSize, invincibilityTime=$invincibilityTime, friendlyFire=$friendlyFire)"
    }
}