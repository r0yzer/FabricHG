package de.royzer.fabrichg.kit.kits

import de.royzer.fabrichg.kit.achievements.delegate.achievement
import de.royzer.fabrichg.kit.kit
import net.minecraft.world.item.Items

val automaticKit = kit("Automatic") {
    kitSelectorItem = Items.MUSHROOM_STEW.defaultInstance

    description = "Soups automatically in your hotbar"

    beginnerKit = true

    val automaticSoupAchievement by achievement("automatic soup") {
        level(100)
        level(2500)
        level(7500)
    }

    kitEvents {
        onTakeDamage { hgPlayer, kit, source, amount ->
            val player = hgPlayer.serverPlayer ?: return@onTakeDamage amount

            if (player.health < 14) {
                val hotbar = player.inventory.items.subList(0, 9)
                val stew = hotbar.firstOrNull { it.item == Items.MUSHROOM_STEW } ?: return@onTakeDamage amount
                player.inventory.removeItem(stew)
                player.heal(5f)
                automaticSoupAchievement.awardLater(hgPlayer.serverPlayer ?: return@onTakeDamage amount)
            }

            return@onTakeDamage amount
        }
    }
}