package de.royzer.fabrichg.settings

import de.royzer.fabrichg.game.PlayerList
import de.royzer.fabrichg.kit.Kit
import de.royzer.fabrichg.kit.kits.noneKit


object GameSettings {
    var minifeastEnabled = true
    var mushroomCowNerf = true
    val disabledKits = mutableListOf<Kit>()
    fun disableKit(kit: Kit) {
        disabledKits.add(kit)
        PlayerList.alivePlayers.forEach {
            if (it.hasKit(kit)) {
                it.kits.remove(kit)
                it.kits.add(noneKit)
            }
        }
    }

    override fun toString(): String {
        return "Minifeast enabled: $minifeastEnabled\n" +
                "Mushroom cow nerf enabled: $mushroomCowNerf\n" +
                "Disabled kits: ${disabledKits.joinToString()}\n"
    }
}