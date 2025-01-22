package de.royzer.fabrichg.settings

import de.royzer.fabrichg.kit.kits.*
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlin.math.absoluteValue

@Serializable
enum class SoupMode {
    Eat {
        override fun toString(): String {
            return "Eat"
        }
    },
    EatAndDestroyBlock {
        override fun toString(): String {
            return "Eat & Hit Block"
        }
    },
    EatAndHit {
        override fun toString(): String {
            return "Eat & Hit (anywhere)"
        }
    };


    fun next(): SoupMode {
        val index = SoupMode.entries.indexOf(this)

        return get(index + 1)
    }

    fun last(): SoupMode {
        val index = SoupMode.entries.indexOf(this)

        return get(index - 1)
    }

    companion object {
        fun get(i: Int): SoupMode {
            if (i == -1) return entries.last()

            return entries[i % SoupMode.entries.size]
        }
    }
}

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
    var kitAmount: Int = 2,
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
        listOf(anchorKit.name, urgalKit.name),
        listOf(gladiatorKit.name, urgalKit.name),
        listOf(grapplerKit.name, stomperKit.name),
        listOf(phantomKit.name, stomperKit.name),
        listOf(kangarooKit.name, blinkKit.name, phantomKit.name),
        listOf(switcherKit.name, demomanKit.name),
        listOf(switcherKit.name, jackhammerKit.name),
        listOf(anchorKit.name, beerKit.name)
    ),
    @EncodeDefault
    var soupMode: SoupMode = SoupMode.EatAndDestroyBlock,
    @EncodeDefault
    var crossteamingAllowed: Boolean = false,
) {
    override fun toString(): String {
        return "GameSettings(minPlayers=$minPlayers, maxIngameTime=$maxIngameTime, feastStartTime=$feastStartTime, minifeastEnabled=$minifeastEnabled, mushroomCowNerf=$mushroomCowNerf, kitAmount=$kitAmount, pitEnabled=$pitEnabled, pitStartTime=$pitStartTime, gulagEnabled=$gulagEnabled, achievementsEnabled=$achievementsEnabled, gulagEndTime=$gulagEndTime, minPlayersOutsideGulag=$minPlayersOutsideGulag, critDamage=$critDamage, maxRecraftBeforeFeast=$maxRecraftBeforeFeast, surpriseOnlyEnabledKits=$surpriseOnlyEnabledKits, teamsEnabled=$teamsEnabled, teamSize=$teamSize, invincibilityTime=$invincibilityTime, friendlyFire=$friendlyFire, soupMode=$soupMode, crossteamingAllowed=$crossteamingAllowed)"
    }
}